package com.fintech.cryptotrading.service;

import com.fintech.cryptotrading.dto.TransactionsDto;
import com.fintech.cryptotrading.dto.UserCryptoWalletDto;
import com.fintech.cryptotrading.dto.UserWalletDto;
import com.fintech.cryptotrading.exception.UserException;
import com.fintech.cryptotrading.request.user.UserRequest;

import java.util.List;

public interface UserService {
    UserWalletDto getWalletBalance(UserRequest userRequest) throws UserException;
    List<UserCryptoWalletDto> getCryptoWalletBalance(UserRequest userRequest) throws UserException;
    List<TransactionsDto> getTransactions(UserRequest userRequest) throws UserException;
}
