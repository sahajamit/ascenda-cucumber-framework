package com.sahajamit.dto;

import com.sahajamit.config.TransactionData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestResponse {
    private TransactionResponse transactionResponse;
    private String transactionId;
    private TransactionData transactionData;
}
