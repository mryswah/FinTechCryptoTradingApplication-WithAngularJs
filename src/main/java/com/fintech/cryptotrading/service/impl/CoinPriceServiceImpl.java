package com.fintech.cryptotrading.service.impl;

import com.fintech.cryptotrading.dto.CoinPriceDto;
import com.fintech.cryptotrading.mapper.CoinPriceMapper;
import com.fintech.cryptotrading.model.CoinPrice;
import com.fintech.cryptotrading.repository.CoinPriceRepository;
import com.fintech.cryptotrading.service.CoinPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoinPriceServiceImpl implements CoinPriceService {
    private final CoinPriceRepository coinPriceRepository;

    @Autowired
    public CoinPriceServiceImpl(CoinPriceRepository coinPriceRepository) {
        this.coinPriceRepository = coinPriceRepository;
    }

    @Override
    public List<CoinPriceDto> getCoinPrice() {
        List<CoinPrice> coinPrice = coinPriceRepository.findAll();
        return coinPrice.stream().map(CoinPriceMapper::mapToCoinPriceDto).collect(Collectors.toList());
    }

}
