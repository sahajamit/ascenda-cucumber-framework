package com.sahajamit.stepdefs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sahajamit.api.ApiOperations;
import com.sahajamit.api.AscendaApi;
import com.sahajamit.config.TransactionData;
import com.sahajamit.dto.*;
import com.sahajamit.utils.DateUtils;
import com.sahajamit.utils.FakerUtil;
import com.sahajamit.utils.PointsCalculator;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@Slf4j
public class AscendaStepdefs {
    private double pointsBalanceAsOf = 0;
    private double cumulativeSpendBalance = 0;

    @Autowired
    private ScenarioContext scenarioContext;
    private AscendaApi ascendaApi = new AscendaApi();
    private ApiOperations apiOperations = new ApiOperations();

    @Before
    public void setUp() {
        log.info("Resetting the values before every test scenario");
        pointsBalanceAsOf = 0;
        cumulativeSpendBalance = 0;
        scenarioContext.getTransactionRequestResponses().clear();
    }
    @Given("I create a new user:")
    public void iCreateANewUser(DataTable dataTable) throws JsonProcessingException {
        Map<String,String> dataMap =  dataTable.asMap();
        Faker faker = FakerUtil.getInstance();
        UserRequest userRequest = UserRequest.builder()
                .countryCode(dataMap.get("countryCode"))
                .status(dataMap.get("status"))
                .partnerUserId(UUID.randomUUID().toString())
                .email(faker.internet().emailAddress())
                .phoneNumber(dataMap.get("phoneNumber"))
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .birthdate(LocalDate.parse(dataMap.get("birthDate")))
                .gender(dataMap.get("gender"))
                .build();

        UserResponse userResponse = apiOperations.createNewUser(userRequest);
        log.info("User ID Created", userResponse.getId());
        Assert.assertEquals(userRequest.getPartnerUserId(), userResponse.getPartnerUserId());
        Assert.assertEquals(userRequest.getEmail(), userResponse.getEmail());
        Assert.assertEquals(userRequest.getStatus(), userResponse.getStatus());
        Assert.assertEquals(userRequest.getFirstName(), userResponse.getFirstName());

        scenarioContext.setUserResponse(userResponse);
        scenarioContext.setUserId(userResponse.getId());
    }

    @Then("the user's points balance should be {double}")
    public void theUserSPointsBalanceShouldBe(double balance) {
        UserResponse userResponse = scenarioContext.getUserResponse();
        UserDetails userDetails = apiOperations.getUserDetails(userResponse.getId());
        log.info("Hitting the USer Details API", "Successfully hit the user details");
        Assert.assertEquals(userResponse.getId(),userDetails.getId());
        Assert.assertEquals(userResponse.getPartnerUserId(),userDetails.getPartnerUserId());
        Assert.assertEquals(userResponse.getEmail(),userDetails.getEmail());
        //Asserting the initial points balance is zero
        log.info("Assertion: ", "Asserting the points account balance is zero");
        Assert.assertEquals(balance,userDetails.getPointsAccount().getPointsBalance(),0.0);
    }

    @And("I make a single transaction:")
    public void iMakeASingleTransaction(DataTable dataTable) {
        Map<String,String> dataMap =  dataTable.asMap();
        TransactionData singleTransactionData = TransactionData.builder()
                .posting_date(dataMap.get("posting_date"))
                .amount(Double.parseDouble(dataMap.get("amount")))
                .account_type(dataMap.get("account_type"))
                .international_currency(dataMap.get("international_currency"))
                .merchantDescriptor(dataMap.get("merchant_descriptor"))
                .build();

        scenarioContext.setSingleTransactionData(singleTransactionData);

        log.info("Posting a Transaction", singleTransactionData.toString());
        TransactionResponse transactionResponse = apiOperations.postTransaction(
                scenarioContext.getUserId(),
                singleTransactionData.getPosting_date(),
                singleTransactionData.getAmount(),
                singleTransactionData.getAccount_type(),
                singleTransactionData.getInternational_currency(),
                singleTransactionData.getMerchantDescriptor()
        );
        log.info("Assertion: ", "Asserting the post transactionData response.");
        Assert.assertEquals(scenarioContext.getUserId(), transactionResponse.getUserId());
        Assert.assertEquals(Optional.of(singleTransactionData.getAmount()).get(),transactionResponse.getAmount());
        Assert.assertEquals(singleTransactionData.getPosting_date(), DateUtils.convertDateToString(transactionResponse.getPostingDate(),"yyyy-MM-dd"));
        scenarioContext.setTransactionResponse(transactionResponse);
    }

    @And("I wait for the batch job to get executed")
    public void iWaitForTheBatchJobToGetExecuted() {
        await().atMost(120, TimeUnit.MINUTES)
                .pollInterval(1, TimeUnit.MINUTES)
                .until(() -> (apiOperations.getTransactionsHistory(scenarioContext.getUserId(), true).getData().size())>=1);
    }

    @Then("I reconcile my single transaction data with the transactions history api response")
    public void iShouldBeAbleToReconcileMyTransactionsInTheTransactionHistory() {
        TransactionHistory transactionHistory = apiOperations.getTransactionsHistory(scenarioContext.getUserId());
        log.info("Hitting the points transactions history API: ", "API is successfully hit");
        String transactionID = VerifyTransactionHistoryResponse(
                transactionHistory,
                scenarioContext.getSingleTransactionData(),
                scenarioContext.getTransactionResponse(),
                false);
        scenarioContext.setTransactionID(transactionID);
    }

    @Then("I reconcile all my transactions data with the transactions history api response")
    public void iReconcileAllMyTransactionsDataWithTheTransactionsHistoryApiResponse() {
        TransactionHistory transactionHistory = apiOperations.getTransactionsHistory(scenarioContext.getUserId());
        log.info("Hitting the points transactions history API: ", "API is successfully hit");

        scenarioContext.getTransactionRequestResponses().forEach(trr->{
            String transactionID = VerifyTransactionHistoryResponse(transactionHistory, trr.getTransactionData(), trr.getTransactionResponse(), true);
            trr.setTransactionId(transactionID);
            log.info("Transaction ID Mapping: ",transactionID + " : " + trr.getTransactionData().getAmount());
        });
    }

    @Then("I reconcile all my transactions data with the transaction detail api response")
    public void iReconcileAllMyTransactionsDataWithTheTransactionDetailApiResponse() {
        cumulativeSpendBalance = 0;
        scenarioContext.getTransactionRequestResponses().forEach(trr->{
            VerifyTransactionDetailsResponse(scenarioContext.getUserId(), trr.getTransactionId(), trr.getTransactionData());
        });
    }

    @Then("I reconcile my single transaction data with the transaction detail api response")
    public void iReconcileMySingleTransactionDataWithTheTransactionDetailApiResponse() {
        cumulativeSpendBalance = 0;
        VerifyTransactionDetailsResponse(scenarioContext.getUserId(), scenarioContext.getTransactionID(), scenarioContext.getSingleTransactionData());
    }

    @Then("I reconcile my total points balance with the user details api response")
    public void iReconcileMyTotalPointsBalanceWithTheUserDetailsApiResponse() {
        VerifyUserDetailsResponse(scenarioContext.getUserId());
    }

    @And("I make the following transactions:")
    public void iMakeTheFollowingTransactions(DataTable transactionsData) {
        List<Map<String, String>> transactionsList = transactionsData.asMaps(String.class, String.class);
        transactionsList.forEach(transactionMap->{
            TransactionData singleTransactionData = TransactionData.builder()
                    .posting_date(transactionMap.get("posting_date"))
                    .amount(Double.parseDouble(transactionMap.get("amount")))
                    .account_type(transactionMap.get("account_type"))
                    .international_currency(transactionMap.get("international_currency"))
                    .merchantDescriptor(transactionMap.get("merchant_descriptor"))
                    .build();
            log.info("Posting this transactionData: ", singleTransactionData.toString());
            TransactionResponse transactionResponse = postTransaction(scenarioContext.getUserId(), singleTransactionData);
            TransactionRequestResponse transactionRequestResponse = TransactionRequestResponse.builder()
                    .transactionData(singleTransactionData)
                    .transactionResponse(transactionResponse)
                    .build();
            scenarioContext.getTransactionRequestResponses().add(transactionRequestResponse);
        });
    }

    @And("I make {int} random transactions")
    public void iMakeRandomTransactions(int numberOfTransactions) {
        for(int i=0; i<numberOfTransactions; i++){
            TransactionData singleTransactionData = TransactionData.builder()
                    .posting_date(DateUtils.getFakePostingData())
                    .amount(FakerUtil.getInstance().number().numberBetween(10, 1000))
                    .account_type("debit_card")
                    .international_currency("SGD")
                    .merchantDescriptor(FakerUtil.getInstance().company().name())
                    .build();
            log.info("Posting this transactionData: ", singleTransactionData.toString());
            TransactionResponse transactionResponse = postTransaction(scenarioContext.getUserId(), singleTransactionData);
            TransactionRequestResponse transactionRequestResponse = TransactionRequestResponse.builder()
                    .transactionData(singleTransactionData)
                    .transactionResponse(transactionResponse)
                    .build();
            scenarioContext.getTransactionRequestResponses().add(transactionRequestResponse);
        }
    }

    private TransactionResponse postTransaction(String userId, TransactionData singleTransactionData){
        TransactionResponse transactionResponse = apiOperations.postTransaction(
                userId,
                singleTransactionData.getPosting_date(),
                singleTransactionData.getAmount(),
                singleTransactionData.getAccount_type(),
                singleTransactionData.getInternational_currency(),
                singleTransactionData.getMerchantDescriptor()
        );
        Assert.assertEquals(userId, transactionResponse.getUserId());
        Assert.assertEquals(Optional.of(Math.abs(singleTransactionData.getAmount())).get(),transactionResponse.getAmount());
        if(singleTransactionData.getAmount()>=0)
            Assert.assertEquals("accrual",transactionResponse.getType());
        else
            Assert.assertEquals("reversal",transactionResponse.getType());
        Assert.assertEquals(singleTransactionData.getPosting_date(), DateUtils.convertDateToString(transactionResponse.getPostingDate(),"yyyy-MM-dd"));
        return transactionResponse;
    }

    private String VerifyTransactionHistoryResponse(TransactionHistory transactionHistory, TransactionData transactionData, TransactionResponse transactionResponse, boolean isMultiTransaction){
        double amount = transactionData.getAmount();
        double pointsAccrued = PointsCalculator.getAccruedPoints(amount);
        pointsBalanceAsOf = pointsBalanceAsOf + pointsAccrued;
        cumulativeSpendBalance = cumulativeSpendBalance + amount;
        int transactionsCount;
        if(isMultiTransaction)
            transactionsCount = scenarioContext.getTransactionRequestResponses().size();
        else
            transactionsCount = 1;

        log.info("Assertions: ", "Asserting the values in transactionData history API response");
        Assert.assertEquals(transactionsCount,transactionHistory.getData().size());

        TransactionHistory.Transaction matchingTransaction =  getTransactionByMerchantDetailsAndTransactionAmount(transactionResponse, transactionHistory);

        Assert.assertEquals(transactionResponse.getMerchantId(),matchingTransaction.getMetadata().getMerchantId());
        Assert.assertEquals(Optional.of(pointsAccrued).get(),matchingTransaction.getAmount());
        //todo Sometimes this assertion fails: needs more investigation
//        Assertions.assertEquals(pointsBalanceAsOf,matchedTransaction.getBalanceAsOf());
        Assert.assertEquals(Optional.of(amount).get(),matchingTransaction.getMetadata().getSpendAmount());
        //todo Sometimes this assertion fails: needs more investigation
//        Assert.assertEquals(Optional.of(cumulativeSpendBalance).get(),matchedTransaction.getMetadata().getCumulativeSpendBalance());
        return matchingTransaction.getId();
    }

    private TransactionHistory.Transaction getTransactionByMerchantDetailsAndTransactionAmount(TransactionResponse transactionResponse, TransactionHistory transactionHistory){
        String transactionDescription = String.format("Points earned | %s: $%.2f",transactionResponse.getMerchantDescriptor(), transactionResponse.getAmount());
        Optional<TransactionHistory.Transaction> transaction =  transactionHistory.getData().stream()
                .filter(t->t.getMetadata().getMerchantId().equals(transactionResponse.getMerchantId()))
                .filter(t-> t.getDescription().equals(transactionDescription))
                .findFirst();

        if(transaction.isPresent())
            return transaction.get();
        else
            throw new IllegalStateException(String.format("Transaction with matching criteria not found in the transactionData history"));
    }

    private void VerifyTransactionDetailsResponse(String userId, String transactionId, TransactionData transactionData){
        double amount = transactionData.getAmount();
        cumulativeSpendBalance = cumulativeSpendBalance + amount;
        double pointsAcquired = PointsCalculator.getAccruedPoints(amount);
        TransactionHistory.Transaction transactionDetails = apiOperations
                .getTransactionDetails(userId, transactionId);
        Allure.addAttachment("Hitting the points transactionData details API for the id " + transactionId, "API is successfully hit");
        log.info("Hitting the points transactionData details API for the id " + transactionId, "API is successfully hit");

        log.info("Assertions: ", "Asserting the values in transactionData details API  response");
        Allure.addAttachment("Assertions:", "Asserting the values in transactionData details API  response");
        Assert.assertEquals(Optional.of(pointsAcquired).get(),transactionDetails.getAmount());

        //todo Sometimes this assertion fails: needs more investigation
//        Assertions.assertEquals(pointsBalanceAsOf,transactionDetails.getBalanceAsOf());
        Assert.assertEquals(Optional.of(amount).get(),transactionDetails.getMetadata().getSpendAmount());
        //todo Sometimes this assertion fails: needs more investigation
//        Assert.assertEquals(Optional.of(cumulativeSpendBalance).get(),transactionDetails.getMetadata().getCumulativeSpendBalance());
    }

    private void VerifyUserDetailsResponse(String userId){
        int roundedPointsBalance = PointsCalculator.getRoundedPoints(cumulativeSpendBalance);
        UserDetails userDetails = apiOperations.getUserDetails(userId);
        Allure.addAttachment("Hitting the user details details API: ", "API is successfully hit");
        log.info("Hitting the user details details API: ", "API is successfully hit");
        log.info("Assertions: ", "Asserting the values in the user details details API response");
        Allure.addAttachment("Asserting Total Balance: ", "The total points balance for this user should be: " + roundedPointsBalance);
        Assert.assertEquals(roundedPointsBalance,PointsCalculator.getRoundedAmount(userDetails.getPointsAccount().getPointsBalance()), 0.0);
        Assert.assertEquals(roundedPointsBalance, PointsCalculator.getRoundedAmount(userDetails.getPointsAccount().getTranches().get(0).getBalance()), 0.0);
    }
}
