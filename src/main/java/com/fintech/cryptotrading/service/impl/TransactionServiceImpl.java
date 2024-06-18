package com.fintech.cryptotrading.service.impl;

import com.fintech.cryptotrading.constant.CommonConstant;
import com.fintech.cryptotrading.dto.TransactionsDto;
import com.fintech.cryptotrading.exception.TransactionsException;
import com.fintech.cryptotrading.model.*;
import com.fintech.cryptotrading.repository.*;
import com.fintech.cryptotrading.request.closetransaction.CloseTransactionRequest;
import com.fintech.cryptotrading.request.opentransaction.OpenTransactionRequest;
import com.fintech.cryptotrading.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.fintech.cryptotrading.mapper.TransactionsMapper.mapToTransactionsDto;

@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

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
    public TransactionsDto trade(OpenTransactionRequest openTransactionRequest) throws TransactionsException {
        long startTime = System.nanoTime();
        String orderType = openTransactionRequest.getOpenTransactionRequestBody().getOrderType().toUpperCase();
        if(!isBuyOrSell(orderType)){
            throw new TransactionsException("Order Type can only be BUY or SELL");
        }
        Optional<User> user = userRepository.findByUsername(openTransactionRequest.getCustomerInformation().getUsername());
        if (user.isEmpty()) {
            throw new TransactionsException(CommonConstant.DOES_NOT_EXIST_EXCEPTION + openTransactionRequest.getCustomerInformation().getUsername());
        }
        CoinPrice coinPrice = coinPriceRepository.findByName(openTransactionRequest.getTransactionInformation().getCoinName().toUpperCase());
        if (coinPrice == null) {
            throw new TransactionsException("The following Coin does not exist in our system : " + openTransactionRequest.getTransactionInformation().getCoinName());
        }

        Transactions transactions = setTransactionDetails(openTransactionRequest, user.get(), coinPrice, orderType);
        proceedToWalletActivities(transactions);
        transactionsRepository.saveAndFlush(transactions);

        double estimatedTime = (double) (System.nanoTime() - startTime) / CommonConstant.BILLION;
        log.info("Time taken (in seconds) for /trade/openTransaction endpoint : {}", estimatedTime);
        return mapToTransactionsDto(transactions);
    }

    // Set the Transaction details
    private static Transactions setTransactionDetails(OpenTransactionRequest openTransactionRequest, User user, CoinPrice coinPrice, String orderType) {
        Transactions transactions = new Transactions();
        transactions.setUser(user);
        transactions.setCoinName(openTransactionRequest.getTransactionInformation().getCoinName().toUpperCase());
        transactions.setOrderType(orderType);
        transactions.setUnits(openTransactionRequest.getOpenTransactionRequestBody().getUnits());
        switch (orderType) {
            case CommonConstant.BUY:
                transactions.setEntryPrice(coinPrice.getAskPrice());
                transactions.setCurrentPrice(coinPrice.getBidPrice());
                transactions.setAmountPaidFor(transactions.getEntryPrice() * transactions.getUnits());
            case CommonConstant.SELL:
                transactions.setEntryPrice(coinPrice.getBidPrice());
                transactions.setCurrentPrice(coinPrice.getAskPrice());
                transactions.setAmountPaidFor(transactions.getEntryPrice() * transactions.getUnits());
        }
        transactions.setProfitLoss(transactions.getCurrentPrice() - transactions.getEntryPrice());
        transactions.setStatus(CommonConstant.OPEN);
        return transactions;
    }

    @Transactional
    public TransactionsDto close(CloseTransactionRequest closeTransactionRequest) throws TransactionsException {
        long startTime = System.nanoTime();
        Optional<User> user = userRepository.findByUsername(closeTransactionRequest.getCustomerInformation().getUsername());
        if (user.isEmpty()) {
            throw new TransactionsException(CommonConstant.DOES_NOT_EXIST_EXCEPTION + closeTransactionRequest.getCustomerInformation().getUsername());
        }
        Transactions transactions = transactionsRepository.findByQueries(closeTransactionRequest.getCloseTransactionRequestBody().getTransactionReferenceNumber(), user.get().getId(), closeTransactionRequest.getTransactionInformation().getCoinName());

        if (transactions != null && transactions.getStatus().equals((CommonConstant.OPEN))) {
            transactions.setStatus(CommonConstant.CLOSED);
            transactions.setAmountSoldFor(transactions.getUnits() * transactions.getCurrentPrice());
            proceedToWalletActivities(transactions);
            transactionsRepository.saveAndFlush(transactions);
            double estimatedTime = (double) (System.nanoTime() - startTime) / CommonConstant.BILLION;
            log.info("Time taken (in seconds) for /trade/closeTransaction endpoint : {}", estimatedTime);
            return mapToTransactionsDto(transactions);
        }
        throw new TransactionsException(constructCloseTransactionExceptionMessage(closeTransactionRequest));
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

    // Construct the TransactionException message when trying to close a Transaction that is either CLOSED or does not exist
    private String constructCloseTransactionExceptionMessage(CloseTransactionRequest closeTransactionRequest) {
        StringBuilder str = new StringBuilder();
        str.append("The following transaction reference number ");
        str.append(closeTransactionRequest.getCloseTransactionRequestBody().getTransactionReferenceNumber());
        str.append(", done by username ");
        str.append(closeTransactionRequest.getCustomerInformation().getUsername());
        str.append(" ,for symbol ");
        str.append(closeTransactionRequest.getTransactionInformation().getCoinName());
        str.append(" has either been closed, or does not exist in our system");
        return str.toString();
    }

    private boolean isBuyOrSell(String orderType){
        return orderType.equalsIgnoreCase(CommonConstant.BUY) ||
                orderType.equalsIgnoreCase(CommonConstant.SELL);
    }

}
