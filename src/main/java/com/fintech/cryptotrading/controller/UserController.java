package com.fintech.cryptotrading.controller;

import com.fintech.cryptotrading.dto.TransactionsDto;
import com.fintech.cryptotrading.dto.UserCryptoWalletDto;
import com.fintech.cryptotrading.dto.UserWalletDto;
import com.fintech.cryptotrading.exception.UserException;
import com.fintech.cryptotrading.request.user.UserRequest;
import com.fintech.cryptotrading.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/walletBalance")
    public ResponseEntity<UserWalletDto> getWalletBalance(@Valid @RequestBody UserRequest userRequest) throws UserException {
        return new ResponseEntity<>(userService.getWalletBalance(userRequest), HttpStatus.OK);
    }

    @PostMapping("/cryptoWalletBalance")
    public ResponseEntity<List<UserCryptoWalletDto>> getCryptoWalletBalance(@Valid @RequestBody UserRequest userRequest) throws UserException {
        return new ResponseEntity<>(userService.getCryptoWalletBalance(userRequest), HttpStatus.OK);
    }

    @PostMapping("/transactions")
    public ResponseEntity<List<TransactionsDto>> getTransactions(@Valid @RequestBody UserRequest userRequest) throws UserException {
        return new ResponseEntity<>(userService.getTransactions(userRequest), HttpStatus.OK);
    }

}
