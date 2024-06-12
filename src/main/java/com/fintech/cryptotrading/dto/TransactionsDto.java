package com.fintech.cryptotrading.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransactionsDto {
    private Long transactionReferenceNumber;
    private String username;
    private String coinName;
    private String orderType;
    private String status;
    private float units;
    private float entryPrice;
    private float currentPrice;
    private float amountPaidFor;
    private float amountSoldFor;
    private float profitLoss;
    private LocalDateTime transactionDate;
    private LocalDateTime updatedOn;
}
