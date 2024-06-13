package com.fintech.cryptotrading.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CommonConstant {

    public static final String BINANCE_URL = "https://api.binance.com/api/v3/ticker/bookTicker";
    public static final String HUOBI_URL = "https://api.huobi.pro/market/tickers";
    public static final String BINANCE = "BINANCE";
    public static final String HUOBI = "HUOBI";
    public static final String USDT = "USDT";
    public static final String SELL = "SELL";
    public static final String BUY = "BUY";
    public static final String OPEN = "OPEN";
    public static final String CLOSED = "CLOSED";
    public static final double BILLION = 1000000000;

    @Getter
    @AllArgsConstructor
    public enum SupportedSymbols {
        // Add more Crypto Currency symbols here if required
        ETHUSDT("ETHUSDT"),
        BTCUSDT("BTCUSDT"),
        ;
        private final String symbol;
    }
}
