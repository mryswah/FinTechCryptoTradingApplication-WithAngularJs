package com.fintech.cryptotrading.response.huobi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HuobiData {
    private String symbol;
    private String open;
    private String high;
    private String low;
    private String close;
    private String amount;
    private String vol;
    private String count;
    private float bid;
    private String bidSize;
    private float ask;
    private String askSize;
}
