package com.sahajamit.stepdefs;

import com.sahajamit.config.TransactionData;
import com.sahajamit.dto.TransactionRequestResponse;
import com.sahajamit.dto.UserResponse;
import com.sahajamit.dto.TransactionResponse;
import io.cucumber.datatable.DataTable;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("cucumber-glue")
@Data
public class ScenarioContext {
    private UserResponse userResponse;
    private String userId;
    private TransactionData singleTransactionData;
    private TransactionResponse transactionResponse;
    private String transactionID;
    private List<TransactionRequestResponse> transactionRequestResponses = new ArrayList<>();
}
