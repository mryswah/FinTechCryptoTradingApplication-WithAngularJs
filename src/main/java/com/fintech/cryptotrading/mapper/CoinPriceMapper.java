package com.fintech.cryptotrading.mapper;

import com.fintech.cryptotrading.dto.CoinPriceDto;
import com.fintech.cryptotrading.model.CoinPrice;

public class CoinPriceMapper {
    public static CoinPriceDto mapToCoinPriceDto(CoinPrice coinPrice){
        return CoinPriceDto.builder()
                .id(coinPrice.getId())
                .symbol(coinPrice.getSymbol())
                .bidPrice(coinPrice.getBidPrice())
                .askPrice(coinPrice.getAskPrice())
                .updatedOn(coinPrice.getUpdatedOn())
                .build();
    }
}
