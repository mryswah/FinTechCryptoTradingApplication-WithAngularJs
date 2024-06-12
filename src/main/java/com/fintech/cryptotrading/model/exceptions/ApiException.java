package com.fintech.cryptotrading.model.exceptions;

public class ApiException extends Exception {
    public ApiException(String errorMessage) {
        super(errorMessage);
    }
}
