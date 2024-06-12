package com.fintech.cryptotrading.service;

import com.fintech.cryptotrading.dto.TransactionsDto;
import com.fintech.cryptotrading.model.exceptions.TransactionsException;
import com.fintech.cryptotrading.model.requests.CloseTransactionRequest;
import com.fintech.cryptotrading.model.requests.TransactionRequest;

public interface TransactionService {
    TransactionsDto trade(TransactionRequest transactionRequest) throws TransactionsException;
    TransactionsDto close(CloseTransactionRequest closeTransactionRequest) throws TransactionsException;
}
