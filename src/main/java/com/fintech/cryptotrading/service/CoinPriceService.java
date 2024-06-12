package com.fintech.cryptotrading.service;

import com.fintech.cryptotrading.dto.CoinPriceDto;

import java.util.List;

public interface CoinPriceService {
    List<CoinPriceDto> getCoinPrice();
}
