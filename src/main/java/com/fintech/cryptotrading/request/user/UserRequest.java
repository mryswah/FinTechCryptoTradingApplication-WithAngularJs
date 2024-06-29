package com.fintech.cryptotrading.request.user;

import com.fintech.cryptotrading.request.customerinfo.CustomerInformation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    @Valid
    private CustomerInformation customerInformation;
}
