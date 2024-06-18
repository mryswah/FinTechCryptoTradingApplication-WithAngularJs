package com.fintech.cryptotrading.request.transactioninfo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionInformation {
    @NotBlank(message = "Coin Name cannot be blank")
    private String coinName;
}
