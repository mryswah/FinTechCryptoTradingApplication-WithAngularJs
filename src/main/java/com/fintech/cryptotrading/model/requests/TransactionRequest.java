package com.fintech.cryptotrading.model.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotBlank(message = "Coin Name cannot be blank")
    private String coinName;
    @NotBlank(message = "Order Type cannot be blank")
    private String orderType;
    @Positive(message = "Number of Units cannot be blank or zero")
    private float units;
}
