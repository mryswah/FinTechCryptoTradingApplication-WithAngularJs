package com.fintech.cryptotrading.service.impl;

import com.fintech.cryptotrading.constant.CommonConstant;
import com.fintech.cryptotrading.dto.TransactionsDto;
import com.fintech.cryptotrading.dto.UserCryptoWalletDto;
import com.fintech.cryptotrading.dto.UserWalletDto;
import com.fintech.cryptotrading.exception.UserException;
import com.fintech.cryptotrading.mapper.TransactionsMapper;
import com.fintech.cryptotrading.mapper.UserCryptoWalletMapper;
import com.fintech.cryptotrading.model.Transactions;
import com.fintech.cryptotrading.model.User;
import com.fintech.cryptotrading.model.UserCryptoWallet;
import com.fintech.cryptotrading.model.UserWallet;
import com.fintech.cryptotrading.repository.UserRepository;
import com.fintech.cryptotrading.request.user.UserRequest;
import com.fintech.cryptotrading.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fintech.cryptotrading.mapper.UserWalletMapper.mapToUserWalletDto;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserWalletDto getWalletBalance(UserRequest userRequest) throws UserException {
        Optional<User> user = userRepository.findByUsername(userRequest.getCustomerInformation().getUsername());
        if (user.isEmpty()) {
            throw new UserException(CommonConstant.DOES_NOT_EXIST_EXCEPTION + userRequest.getCustomerInformation().getUsername());
        }
        UserWallet userWallet = user.get().getUserWallet();
        if(userWallet==null){
            throw new UserException("The following username currently does not have any account balance " + userRequest.getCustomerInformation().getUsername());
        }
        return mapToUserWalletDto(userWallet);
    }

    @Override
    public List<UserCryptoWalletDto> getCryptoWalletBalance(UserRequest userRequest) throws UserException {
        Optional<User> user = userRepository.findByUsername(userRequest.getCustomerInformation().getUsername());
        if (user.isEmpty()) {
            throw new UserException(CommonConstant.DOES_NOT_EXIST_EXCEPTION + userRequest.getCustomerInformation().getUsername());
        }
        List<UserCryptoWallet> userCryptoWallet = user.get().getUserCryptoWallets();
        return userCryptoWallet.stream().map(UserCryptoWalletMapper::mapToUserCryptoWalletDto).collect(Collectors.toList());
    }

    @Override
    public List<TransactionsDto> getTransactions(UserRequest userRequest) throws UserException {
        Optional<User> user = userRepository.findByUsername(userRequest.getCustomerInformation().getUsername());
        if (user.isEmpty()) {
            throw new UserException(CommonConstant.DOES_NOT_EXIST_EXCEPTION + userRequest.getCustomerInformation().getUsername());
        }
        List<Transactions> transactions = user.get().getTransactions();
        transactions.sort((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));
        return transactions.stream().map(TransactionsMapper::mapToTransactionsDto).collect(Collectors.toList());
    }
}
