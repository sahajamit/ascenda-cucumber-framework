package com.sahajamit.api;

import com.sahajamit.dto.*;
import com.sahajamit.utils.FakerUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
public class ApiOperations {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ApiOperations.class);
    private AscendaApi ascendaApi = new AscendaApi();

    public UserResponse createNewUser(UserRequest userRequest) throws JsonProcessingException {
        SSEClient sseClient = new SSEClient();
        new Thread(() -> sseClient.connectToStream()).start();

        UserResponse userResponse = ascendaApi.createNewUser(userRequest);
        String userId = userResponse.getId();
        logger.info("User Id is: " + userResponse.getId());
        ascendaApi.waitForEnrolledStatus(sseClient, userId);
        return userResponse;
    }

    public UserDetails getUserDetails(String userId){
        UserDetails userDetails = ascendaApi
                .getUserDetails(userId);
        return userDetails;
    }

    public TransactionResponse postTransaction(String userId, String postingData,
                                               double amount, String accountType, String international_currency, String merchant_descriptor){
        Faker faker = FakerUtil.getInstance();

        TransactionRequest transactionRequest = TransactionRequest.builder()
                .user_id(userId)
                .transaction_reference(UUID.randomUUID().toString())
                .posting_date(LocalDate.parse(postingData))
                .amount(amount)
                .international_currency(international_currency)
                .is_international(false)
                .account_type(accountType)
                .description("Test TransactionData: " + faker.gameOfThrones().character())
                .merchant_category_code(String.valueOf(faker.number().numberBetween(1000, 10000)))
                .merchant_descriptor(merchant_descriptor)
                .merchant_id(UUID.randomUUID().toString())
                .card_bin(String.valueOf(faker.number().numberBetween(100000, 1000000)))
                .card_last_4(String.valueOf(faker.number().numberBetween(1000, 10000)))
                .build();

        TransactionResponse transactionResponse = ascendaApi
                .postNewTransaction(transactionRequest);

        return transactionResponse;
    }

    public TransactionHistory getTransactionsHistory(String userId){
        return ascendaApi
                .getTransactionsHistory(userId);
    }

    public TransactionHistory getTransactionsHistory(String userId, boolean isForPolling){
        log.info("Polling Transactions API: ","To wait for the transactionData processing job to get completed");
        return getTransactionsHistory(userId);
    }

    public TransactionHistory.Transaction getTransactionDetails(String userId, String transactionId){
        return ascendaApi
                .getPointsTransactionDetails (userId, transactionId);
    }
}
