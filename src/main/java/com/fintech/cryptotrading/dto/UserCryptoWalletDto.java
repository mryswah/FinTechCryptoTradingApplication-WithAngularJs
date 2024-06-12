package com.fintech.cryptotrading.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCryptoWalletDto {
    private Long id;
    private String username;
    private String symbol;
    private float balance;
}
