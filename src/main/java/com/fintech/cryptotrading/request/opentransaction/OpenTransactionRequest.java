package com.fintech.cryptotrading.request.opentransaction;

import com.fintech.cryptotrading.request.customerinfo.CustomerInformation;
import com.fintech.cryptotrading.request.transactioninfo.TransactionInformation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenTransactionRequest {
    @Valid
    private CustomerInformation customerInformation;
    @Valid
    private TransactionInformation transactionInformation;
    @Valid
    private OpenTransactionRequestBody openTransactionRequestBody;
}
