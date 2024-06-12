package com.fintech.cryptotrading.mapper;

import com.fintech.cryptotrading.dto.TransactionsDto;
import com.fintech.cryptotrading.model.Transactions;

public class TransactionsMapper {
    public static TransactionsDto mapToTransactionsDto(Transactions transactions){
        return TransactionsDto.builder()
                .transactionReferenceNumber(transactions.getTransactionReferenceNumber())
                .username(transactions.getUser().getUsername())
                .coinName(transactions.getCoinName())
                .orderType(transactions.getOrderType())
                .status(transactions.getStatus())
                .units(transactions.getUnits())
                .entryPrice(transactions.getEntryPrice())
                .currentPrice(transactions.getCurrentPrice())
                .amountPaidFor(transactions.getAmountPaidFor())
                .amountSoldFor(transactions.getAmountSoldFor())
                .profitLoss(transactions.getProfitLoss())
                .transactionDate(transactions.getTransactionDate())
                .updatedOn(transactions.getUpdatedOn())
                .build();
    }
}
