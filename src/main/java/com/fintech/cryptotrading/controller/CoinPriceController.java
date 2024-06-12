package com.fintech.cryptotrading.controller;

import com.fintech.cryptotrading.dto.CoinPriceDto;
import com.fintech.cryptotrading.service.CoinPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/coin")
public class CoinPriceController {
    private final CoinPriceService coinPriceService;

    @Autowired
    public CoinPriceController(CoinPriceService coinPriceService) {
        this.coinPriceService = coinPriceService;
    }

    @GetMapping("/getCoinPrice")
    public ResponseEntity<List<CoinPriceDto>> getCoinPrice() {
        return new ResponseEntity<>(coinPriceService.getCoinPrice(), HttpStatus.OK);
    }
}
