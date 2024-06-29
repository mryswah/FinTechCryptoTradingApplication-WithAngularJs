package com.fintech.cryptotrading.response.huobi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HuobiResponse {
    private List<HuobiData> data;
    private String status;
    private Long ts;
}
