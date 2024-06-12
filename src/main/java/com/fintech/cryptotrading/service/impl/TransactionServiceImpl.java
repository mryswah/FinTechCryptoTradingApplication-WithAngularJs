package com.fintech.cryptotrading.service.impl;

import com.fintech.cryptotrading.constant.CommonConstant;
import com.fintech.cryptotrading.dto.TransactionsDto;
import com.fintech.cryptotrading.model.*;
import com.fintech.cryptotrading.model.exceptions.TransactionsException;
import com.fintech.cryptotrading.model.requests.CloseTransactionRequest;
import com.fintech.cryptotrading.model.requests.TransactionRequest;
import com.fintech.cryptotrading.repository.*;
import com.fintech.cryptotrading.service.TransactionService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.fintech.cryptotrading.mapper.TransactionsMapper.mapToTransactionsDto;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionsRepository transactionsRepository;
    private final UserRepository userRepository;
    private final CoinPriceRepository coinPriceRepository;
    private final UserWalletRepository userWalletRepository;
    private final UserCryptoWalletRepository userCryptoWalletRepository;

    @Autowired
    public TransactionServiceImpl(TransactionsRepository transactionsRepository, UserRepository userRepository, CoinPriceRepository coinPriceRepository, UserWalletRepository userWalletRepository, UserCryptoWalletRepository userCryptoWalletRepository) {
        this.transactionsRepository = transactionsRepository;
        this.userRepository = userRepository;
        this.coinPriceRepository = coinPriceRepository;
        this.userWalletRepository = userWalletRepository;
        this.userCryptoWalletRepository = userCryptoWalletRepository;
    }

    @Transactional
    public TransactionsDto trade(TransactionRequest transactionRequest) throws TransactionsException {
        long startTime = System.nanoTime();
        Optional<User> user = userRepository.findByUsername(transactionRequest.getUsername());
        if (user.isEmpty()) {
            throw new TransactionsException("The following username does not exist in our system : " + transactionRequest.getUsername());
        }
        List<CoinPrice> coinPrice = coinPriceRepository.findAll();

        // Check if the app supports the coin specified in the request.
        CoinPrice selectedCoin = coinPrice.stream()
                .filter(coin -> coin.getSymbol().equals(transactionRequest.getCoinName().toUpperCase()))
                .findFirst()
                .orElse(null);
        if (selectedCoin == null) {
            throw new TransactionsException("The following Coin does not exist in our system : " + transactionRequest.getCoinName());
        }
        Transactions transactions = setTransactionDetails(transactionRequest, user.get(), selectedCoin);
        proceedToWalletActivities(transactions);
        transactionsRepository.saveAndFlush(transactions);

        double estimatedTime = (double) (System.nanoTime() - startTime) / CommonConstant.BILLION;
        System.out.println("[TransactionServiceImpl] Time taken (in seconds) for /trade/openTransaction endpoint : " + estimatedTime);
        return mapToTransactionsDto(transactions);
    }

    // Set the Transaction details
    private static @NotNull Transactions setTransactionDetails(TransactionRequest transactionRequest, User user, CoinPrice selectedCoin) {
        Transactions transactions = new Transactions();
        transactions.setUser(user);
        transactions.setCoinName(transactionRequest.getCoinName().toUpperCase());
        transactions.setOrderType(transactionRequest.getOrderType().toUpperCase());
        transactions.setUnits(transactionRequest.getUnits());
        switch (transactionRequest.getOrderType().toUpperCase()) {
            case CommonConstant.BUY:
                transactions.setEntryPrice(selectedCoin.getAskPrice());
                transactions.setCurrentPrice(selectedCoin.getBidPrice());
                transactions.setAmountPaidFor(transactions.getEntryPrice() * transactions.getUnits());
            case CommonConstant.SELL:
                transactions.setEntryPrice(selectedCoin.getBidPrice());
                transactions.setCurrentPrice(selectedCoin.getAskPrice());
                transactions.setAmountPaidFor(transactions.getEntryPrice() * transactions.getUnits());
        }
        transactions.setProfitLoss(transactions.getCurrentPrice() - transactions.getEntryPrice());
        transactions.setStatus(CommonConstant.OPEN);
        return transactions;
    }

    @Transactional
    public TransactionsDto close(CloseTransactionRequest closeTransactionRequest) throws TransactionsException {
        long startTime = System.nanoTime();
        Optional<User> user = userRepository.findByUsername(closeTransactionRequest.getUsername());
        if (user.isEmpty()) {
            throw new TransactionsException("The following username does not exist in our system : " + closeTransactionRequest.getUsername());
        }
        Transactions transactions = transactionsRepository.findByQueries(closeTransactionRequest.getTransactionReferenceNumber(), user.get().getId(), closeTransactionRequest.getCoinName());

        if (transactions != null && transactions.getStatus().equals((CommonConstant.OPEN))) {
            transactions.setStatus(CommonConstant.CLOSED);
            transactions.setAmountSoldFor(transactions.getUnits() * transactions.getCurrentPrice());
            proceedToWalletActivities(transactions);
            transactionsRepository.saveAndFlush(transactions);
            double estimatedTime = (double) (System.nanoTime() - startTime) / CommonConstant.BILLION;
            System.out.println("[TransactionServiceImpl] Time taken (in seconds) for /trade/closeTransaction endpoint : " + estimatedTime);
            return mapToTransactionsDto(transactions);
        }
        StringBuilder str = new StringBuilder();
        str.append("The following transaction reference number ");
        str.append(closeTransactionRequest.getTransactionReferenceNumber());
        str.append(", done by username ");
        str.append(closeTransactionRequest.getUsername());
        str.append(" ,for symbol ");
        str.append(closeTransactionRequest.getCoinName());
        str.append(" has either been closed, or does not exist in our system");
        throw new TransactionsException(str.toString());
    }

    private void proceedToWalletActivities(Transactions transactions) throws TransactionsException {
        // Deduct from Account Balance
        UserWallet userWallet = userWalletRepository.findByUsername(transactions.getUser());
        if (userWallet == null
                || (transactions.getStatus().equalsIgnoreCase(CommonConstant.OPEN) &&
                transactions.getAmountPaidFor() > userWallet.getAccountBalance())) {
            throw new TransactionsException("Insufficient/No funds in your account balance");
        }
        userWallet.setAccountBalance(setWalletBalance(transactions, userWallet));
        userWalletRepository.save(userWallet);

        // Add to Crypto Currency Wallet
        UserCryptoWallet userCryptoWallet = userCryptoWalletRepository.findByUserAndSymbol(transactions.getUser(), transactions.getCoinName());
        if (userCryptoWallet != null) {
            userCryptoWallet.setBalance(setCryptoWalletBalance(transactions, userCryptoWallet));
        } else {
            userCryptoWallet = new UserCryptoWallet();
            userCryptoWallet.setUser(transactions.getUser());
            userCryptoWallet.setSymbol(transactions.getCoinName());
            userCryptoWallet.setBalance(transactions.getUnits());
        }
        if (userCryptoWallet.getBalance() > 0) {
            userCryptoWalletRepository.save(userCryptoWallet);
        } else {
            userCryptoWalletRepository.delete(userCryptoWallet);
        }
    }

    // [OPEN - BUY]     AmountPaid  = Units * AskPrice
    // [OPEN - SELL]    AmountPaid  = Units * BidPrice
    // [CLOSE - BUY]    Profit      = Units * Current Bid Price
    // [CLOSE - SELL]   Profit      = Units * Current Ask Price
    private float setWalletBalance(Transactions transactions, UserWallet userWallet) {
        return switch (transactions.getStatus()) {
            case CommonConstant.OPEN -> userWallet.getAccountBalance() - transactions.getAmountPaidFor();
            case CommonConstant.CLOSED -> userWallet.getAccountBalance() + transactions.getAmountSoldFor();
            default -> 0;
        };
    }

    // [OPEN]   Add number of units to Crypto Wallet table
    // [CLOSE]  Subtract number of units from Crypto Wallet table
    private float setCryptoWalletBalance(Transactions transactions, UserCryptoWallet userCryptoWallet) {
        return switch (transactions.getStatus()) {
            case CommonConstant.OPEN -> userCryptoWallet.getBalance() + transactions.getUnits();
            case CommonConstant.CLOSED -> userCryptoWallet.getBalance() - transactions.getUnits();
            default -> 0;
        };
    }

}
