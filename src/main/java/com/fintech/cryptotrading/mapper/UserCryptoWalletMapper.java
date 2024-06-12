package com.fintech.cryptotrading.mapper;

import com.fintech.cryptotrading.dto.UserCryptoWalletDto;
import com.fintech.cryptotrading.model.UserCryptoWallet;

public class UserCryptoWalletMapper {
    public static UserCryptoWalletDto mapToUserCryptoWalletDto(UserCryptoWallet userCryptoWallet) {
        return UserCryptoWalletDto.builder()
                .id(userCryptoWallet.getId())
                .username(userCryptoWallet.getUser().getUsername())
                .symbol(userCryptoWallet.getSymbol())
                .balance(userCryptoWallet.getBalance())
                .build();
    }
}
