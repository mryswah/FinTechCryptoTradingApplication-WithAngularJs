package com.fintech.cryptotrading.mapper;

import com.fintech.cryptotrading.dto.UserWalletDto;
import com.fintech.cryptotrading.model.UserWallet;

public class UserWalletMapper {
    public static UserWalletDto mapToUserWalletDto(UserWallet userWallet){
        return UserWalletDto.builder()
                .id(userWallet.getId())
                .username(userWallet.getUser().getUsername())
                .symbol(userWallet.getSymbol())
                .accountBalance(userWallet.getAccountBalance())
                .build();
    }
}
