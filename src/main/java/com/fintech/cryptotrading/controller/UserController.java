package com.fintech.cryptotrading.controller;

import com.fintech.cryptotrading.dto.TransactionsDto;
import com.fintech.cryptotrading.dto.UserCryptoWalletDto;
import com.fintech.cryptotrading.dto.UserWalletDto;
import com.fintech.cryptotrading.model.exceptions.UserException;
import com.fintech.cryptotrading.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}/walletBalance")
    public ResponseEntity<UserWalletDto> getWalletBalance(@PathVariable String username) throws UserException {
        return new ResponseEntity<>(userService.getWalletBalance(username), HttpStatus.OK);
    }

    @GetMapping("/{username}/cryptoWalletBalance")
    public ResponseEntity<List<UserCryptoWalletDto>> getCryptoWalletBalance(@PathVariable String username) throws UserException {
        return new ResponseEntity<>(userService.getCryptoWalletBalance(username), HttpStatus.OK);
    }

    @GetMapping("/{username}/transactions")
    public ResponseEntity<List<TransactionsDto>> getTransactions(@PathVariable String username) throws UserException {
        return new ResponseEntity<>(userService.getTransactions(username), HttpStatus.OK);
    }

}
