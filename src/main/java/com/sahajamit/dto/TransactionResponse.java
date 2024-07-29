package com.sahajamit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    @JsonProperty("merchant_descriptor")
    private String merchantDescriptor;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("posting_date")
    private Date postingDate;
    @JsonProperty("transaction_reference")
    private String transactionReference;
    @JsonProperty("account_type")
    private String accountType;
    @JsonProperty("merchant_category_code")
    private String merchantCategoryCode;
    private String type;
    @JsonProperty("custom_fields")
    private CustomFields customFields;
    @JsonProperty("product_reference_id")
    private String productReferenceId;
    @JsonProperty("merchant_id")
    private String merchantId;
    private String id;
    private String status;
    @JsonProperty("partner_user_id")
    private String partnerUserId;
    @JsonProperty("user_id")
    private String userId;
    private String description;
    private Double amount;
    private String currency;
    @JsonProperty("international_currency")
    private String internationalCurrency;
    @JsonProperty("is_international")
    private Boolean isInternational;
    @JsonProperty("merchant_country_code")
    private String merchantCountryCode;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomFields {
        @JsonProperty("card_bin")
        private String cardBin;
        @JsonProperty("card_last_4")
        private String cardLast4;
    }
}
