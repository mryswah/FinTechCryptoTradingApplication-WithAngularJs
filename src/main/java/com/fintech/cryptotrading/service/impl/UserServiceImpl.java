package com.fintech.cryptotrading.service.impl;

import com.fintech.cryptotrading.dto.TransactionsDto;
import com.fintech.cryptotrading.dto.UserCryptoWalletDto;
import com.fintech.cryptotrading.dto.UserWalletDto;
import com.fintech.cryptotrading.model.*;
import com.fintech.cryptotrading.model.exceptions.UserException;
import com.fintech.cryptotrading.repository.*;
import com.fintech.cryptotrading.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fintech.cryptotrading.mapper.TransactionsMapper.mapToTransactionsDto;
import static com.fintech.cryptotrading.mapper.UserCryptoWalletMapper.mapToUserCryptoWalletDto;
import static com.fintech.cryptotrading.mapper.UserWalletMapper.mapToUserWalletDto;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserWalletDto getWalletBalance(String username) throws UserException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserException("The following username does not exist in our system : " + username);
        }
        UserWallet userWallet = user.get().getUserWallet();
        return mapToUserWalletDto(userWallet);
    }

    @Override
    public List<UserCryptoWalletDto> getCryptoWalletBalance(String username) throws UserException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserException("The following username does not exist in our system : " + username);
        }
        List<UserCryptoWallet> userCryptoWallet = user.get().getUserCryptoWallets();
        return userCryptoWallet.stream().map(wallet -> mapToUserCryptoWalletDto(wallet)).collect(Collectors.toList());
    }

    @Override
    public List<TransactionsDto> getTransactions(String username) throws UserException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserException("The following username does not exist in our system : " + username);
        }
        List<Transactions> transactions = user.get().getTransactions();
        transactions.sort((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));
        return transactions.stream().map(transaction -> mapToTransactionsDto(transaction)).collect(Collectors.toList());
    }
}
