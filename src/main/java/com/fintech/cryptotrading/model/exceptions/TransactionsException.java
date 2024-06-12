package com.fintech.cryptotrading.model.exceptions;

public class TransactionsException extends Exception {
    public TransactionsException(String errorMessage) {
        super(errorMessage);
    }
}
