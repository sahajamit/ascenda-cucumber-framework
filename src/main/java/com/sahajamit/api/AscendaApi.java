package com.sahajamit.api;

import com.sahajamit.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

@Slf4j
public class AscendaApi extends BaseApi{
    private final String USERPATH = "users";
    private final String TRANSACTIONPATH = "/transactions";
    private final String POINTS_TRANSACTIONS = "/users/<userId>/points_transactions";


    @Step("Create a New User")
    public UserResponse createNewUser(UserRequest userRequest){
        return getRequestSpecification()
                .header("Authorization",getOAuthToken())
                .body(userRequest)
                .post(USERPATH)
                .then()
                .statusCode(201)
                .extract()
                .as(UserResponse.class);
    }

    @Step("Get User Details")
    public UserDetails getUserDetails(String user_id){
        return getRequestSpecification()
                .header("Authorization",getOAuthToken())
                .get(USERPATH + "/" + user_id)
                .then()
                .statusCode(200)
                .extract()
                .as(UserDetails.class);
    }

    @Step("Post a new transactionData")
    public TransactionResponse postNewTransaction(TransactionRequest transactionRequest){
        return getRequestSpecification()
                .header("Authorization",getOAuthToken())
                .body(transactionRequest)
                .post(TRANSACTIONPATH)
                .then()
                .statusCode(201)
                .extract()
                .as(TransactionResponse.class);
    }

    @Step("Get user enrollment status from the SSE stream")
    public void waitForEnrolledStatus(SSEClient sseClient, String userId) throws JsonProcessingException {
        try {
            JSONObject message = sseClient.findMessageByUserIdAndStatus(userId, "enrolled");
            log.info("Message Found: ", message.toString());
        } catch (InterruptedException e) {
            throw new RuntimeException("The user status is not turned to enrolled with in 10 seconds.");
        }
    }

    @Step("Get the points transactions history")
    public TransactionHistory getTransactionsHistory(String userId){
        return getRequestSpecification(true)
                .get(POINTS_TRANSACTIONS.replace("<userId>",userId))
                .then()
                .statusCode(200)
                .extract()
                .as(TransactionHistory.class);
    }

    @Step("Get the points transactionData details")
    public TransactionHistory.Transaction getPointsTransactionDetails(String userId, String transactionId){
        return getRequestSpecification(true)
                .get(POINTS_TRANSACTIONS.replace("<userId>",userId)+"/"+transactionId)
                .then()
                .statusCode(200)
                .extract()
                .as(TransactionHistory.Transaction.class);
    }
}
