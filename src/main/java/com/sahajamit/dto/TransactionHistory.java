package com.sahajamit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistory {
    private List<Transaction> data;
    private Metadata meta;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Transaction {
        @JsonProperty("balance_as_of")
        private Double balanceAsOf;
        @JsonProperty("created_at")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private Date createdAt;
        @JsonProperty("updated_at")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private Date updatedAt;
        @JsonProperty("source_reference_id")
        private String sourceReferenceId;
        private Metadata metadata;
        private String id;
        private String type;
        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("points_account_id")
        private String pointsAccountId;
        @JsonProperty("transaction_time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private Date transactionTime;
        private String description;
        private String category;
        @JsonProperty("entry_type")
        private String entryType;
        @JsonProperty("product_type")
        private String productType;
        private String status;
        private Double amount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata {
        @JsonProperty("spend_amount")
        private Double spendAmount;
        @JsonProperty("point_currency")
        private String pointCurrency;
        @JsonProperty("statement_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private Date statementDate;
        @JsonProperty("statement_type")
        private String statementType;
        @JsonProperty("tenant_currency")
        private String tenantCurrency;
        @JsonProperty("source_spend_amount")
        private Double sourceSpendAmount;
        @JsonProperty("total_reversed_points")
        private Double totalReversedPoints;
        @JsonProperty("cumulative_spend_balance")
        private Double cumulativeSpendBalance;
        private Integer count;
        @JsonProperty("tier_id")
        private String tier_id;
        private String currency;
        @JsonProperty("group_id")
        private String groupId;
        private String merchant;
        private Boolean converted;
        @JsonProperty("file_type")
        private String fileType;
        @JsonProperty("date_string")
        private String dateString;
        @JsonProperty("merchant_id")
        private String merchantId;
    }
}

