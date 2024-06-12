package com.fintech.cryptotrading.service;

import com.fintech.cryptotrading.dto.TransactionsDto;
import com.fintech.cryptotrading.dto.UserCryptoWalletDto;
import com.fintech.cryptotrading.dto.UserWalletDto;
import com.fintech.cryptotrading.model.exceptions.UserException;

import java.util.List;

public interface UserService {
    UserWalletDto getWalletBalance(String username) throws UserException;
    List<UserCryptoWalletDto> getCryptoWalletBalance(String username) throws UserException;
    List<TransactionsDto> getTransactions(String username) throws UserException;
}
