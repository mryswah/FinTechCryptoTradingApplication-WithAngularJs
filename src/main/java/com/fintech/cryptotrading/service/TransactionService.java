package com.fintech.cryptotrading.service;

import com.fintech.cryptotrading.dto.TransactionsDto;
import com.fintech.cryptotrading.exception.TransactionsException;
import com.fintech.cryptotrading.request.closetransaction.CloseTransactionRequest;
import com.fintech.cryptotrading.request.opentransaction.OpenTransactionRequest;

public interface TransactionService {
    TransactionsDto trade(OpenTransactionRequest openTransactionRequest) throws TransactionsException;
    TransactionsDto close(CloseTransactionRequest closeTransactionRequest) throws TransactionsException;
}
