package com.sahajamit.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionData {
    private String posting_date;
    private double amount;
    private String account_type;
    private String international_currency;
    private String merchantDescriptor;
}