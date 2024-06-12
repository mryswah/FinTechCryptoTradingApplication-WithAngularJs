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
public class CloseTransactionRequest {
    @Positive(message = "Invalid Transaction Reference Number")
    private Long transactionReferenceNumber;
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotBlank(message = "Coin Name cannot be blank")
    private String coinName;

}
