package com.fintech.cryptotrading.request.customerinfo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerInformation {
    @NotBlank(message = "Username cannot be blank")
    private String username;
}
