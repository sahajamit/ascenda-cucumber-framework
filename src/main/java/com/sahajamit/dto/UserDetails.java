package com.sahajamit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
public class UserDetails {
    private String id;
    private String type;
    @JsonProperty("partner_user_id")
    private String partnerUserId;
    private Address address;
    @JsonProperty("country_code")
    private String countryCode;
    private String email;
    @JsonProperty("first_name")
    private String firstName;
    private String gender;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String status;
    private Boolean activated;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthdate;
    @JsonProperty("points_account")
    private PointsAccount pointsAccount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        @JsonProperty("country_code")
        private String countryCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointsAccount {
        @JsonProperty("points_balance")
        private double pointsBalance;
        private List<Tranche> tranches;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Tranche {
            @JsonProperty("expiry_date")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            private Date expiryDate;
            @JsonProperty("has_expiration_date")
            private boolean hasExpirationDate;
            private int balance;
        }
    }

    public UserDetails(String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        UserDetails userProfile = objectMapper.readValue(jsonString, UserDetails.class);
        System.out.println(userProfile);
    }
}

