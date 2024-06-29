package com.fintech.cryptotrading.request.closetransaction;

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
public class CloseTransactionRequest {
    @Valid
    private CustomerInformation customerInformation;
    @Valid
    private TransactionInformation transactionInformation;
    @Valid
    private CloseTransactionRequestBody closeTransactionRequestBody;
}
