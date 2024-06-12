package com.fintech.cryptotrading.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CoinPriceDto {
    private Long id;
    private String symbol;
    private float bidPrice;
    private float askPrice;
    private LocalDateTime updatedOn;
}
