package com.fintech.cryptotrading.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserWalletDto {
    private Long id;
    private String username;
    private String symbol;
    private float accountBalance;
}
