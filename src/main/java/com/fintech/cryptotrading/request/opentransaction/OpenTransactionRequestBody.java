package com.fintech.cryptotrading.request.opentransaction;

import com.fintech.cryptotrading.constant.CommonConstant;
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
public class OpenTransactionRequestBody {
    @NotBlank(message = "Order Type cannot be blank")
    private String orderType;
    @Positive(message = "Number of Units cannot be blank or zero")
    private float units;
}
