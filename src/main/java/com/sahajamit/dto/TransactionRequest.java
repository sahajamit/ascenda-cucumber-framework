package com.sahajamit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private String account_type;
    private String description;
    private String merchant_category_code;
    private String merchant_descriptor;
    private String merchant_id;
    private String card_bin;
    private String card_last_4;
    private String user_id;
    private String transaction_reference;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate posting_date;
    private double amount;
    private String international_currency;
    private boolean is_international;
    public TransactionRequest(String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        TransactionRequest transactionRequest = objectMapper.readValue(jsonString, TransactionRequest.class);
        System.out.println(transactionRequest);
    }
}
