package com.fintech.cryptotrading.controller;

import com.fintech.cryptotrading.dto.TransactionsDto;
import com.fintech.cryptotrading.exception.TransactionsException;
import com.fintech.cryptotrading.request.closetransaction.CloseTransactionRequest;
import com.fintech.cryptotrading.request.opentransaction.OpenTransactionRequest;
import com.fintech.cryptotrading.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trade")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/openTransaction")
    public ResponseEntity<TransactionsDto> buy(@Valid @RequestBody OpenTransactionRequest openTransactionRequest) throws TransactionsException {
        return new ResponseEntity<>(transactionService.trade(openTransactionRequest), HttpStatus.OK);
    }

    @PostMapping("/closeTransaction")
    public ResponseEntity<TransactionsDto> close(@Valid @RequestBody CloseTransactionRequest closeTransactionRequest) throws TransactionsException {
        return new ResponseEntity<>(transactionService.close(closeTransactionRequest), HttpStatus.OK);
    }
}
