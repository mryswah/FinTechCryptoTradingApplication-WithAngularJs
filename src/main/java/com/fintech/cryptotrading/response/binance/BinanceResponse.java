package com.fintech.cryptotrading.response.binance;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BinanceResponse {
    private String symbol;
    private float bidPrice;
    private String bidQty;
    private float askPrice;
    private String askQty;
}
