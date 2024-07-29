package com.sahajamit.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String audience;
    public AuthenticationRequest(String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        AuthenticationRequest authenticationRequest = objectMapper.readValue(jsonString, AuthenticationRequest.class);
        System.out.println(authenticationRequest);
    }
}
