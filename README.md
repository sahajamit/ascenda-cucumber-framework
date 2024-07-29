## Ascenda Take home assignment (QE) - API

### About:
This repository has the following artifacts:

* [Test Plan](./documents/Test%20Plan.pdf)
* [Test Scenarios](./documents/Use%20Cases.pdf)
* [Test Cases](./src/test/resources/features/ascenda.feature)
* [CI/CD Strategy](./documents/CI-CD-Strategy.pdf)
* API Automation Framework - Framework Code is checked in to this same repository.

### Observations:

Sometimes I have observed that the order in which the transactions are posted is different from the order in which the transactions are processed. Due to this oder discrepancy the expected value for the `balance_as_of` and `cumulative_spend_balance` is not matching with the actual value.


### How to run the tests

```shell
gradle clean cucumberCli -PcucumberOptions="@user-creation" 
```

### How to see test report

```shell
allure serve ./build/allure-results
```

### Test Scenarios

#### 1. Posting X Random Transactions configured in a data table:

``` gherkin
  @ascenda @transactions
  Scenario: Transactions: Post multiple transactions and validate points calculation
    Given I create a new user:
      | status      | active      |
      | phoneNumber | +6590128976 |
      | birthDate   | 1987-06-05  |
      | countryCode | US          |
      | gender      | female      |
    And I make the following transactions:
      | posting_date | amount | account_type | international_currency | merchant_descriptor   |
      | 2024-07-27   | 150    | debit_card   | SGD                    | Shopping of clothes   |
      | 2024-07-28   | 250    | debit_card   | SGD                    | Shopping of jewellery |
      | 2024-07-28   | 500    | debit_card   | SGD                    | Eating Pizza          |
    And I wait for the batch job to get executed
    Then I reconcile all my transactions data with the transactions history api response
    Then I reconcile all my transactions data with the transaction detail api response
    Then I reconcile my total points balance with the user details api response
```

#### 2. Posting X Random Transactions without any configurations:

``` gherkin
  @ascenda @points-calculation @temp
  Scenario: X Random Transactions: Posting x number of random transactions and then validating the points accrued.
    Given I create a new user:
      | status      | active      |
      | phoneNumber | +6590128976 |
      | birthDate   | 1987-06-05  |
      | countryCode | US          |
      | gender      | female      |
    And I make 5 random transactions
    And I wait for the batch job to get executed
    Then I reconcile all my transactions data with the transactions history api response
    Then I reconcile all my transactions data with the transaction detail api response
    Then I reconcile my total points balance with the user details api response
```

#### 3. User creation with zero points as initial balance:

``` gherkin
  @ascenda @user-creation
  Scenario: User Creation: Create a new user and assert that the initial point's balance is shown as zero
    Given I create a new user:
      | status      | active      |
      | phoneNumber | +6590128976 |
      | birthDate   | 1987-06-05  |
      | countryCode | US          |
      | gender      | male        |
    Then the user's points balance should be 0
```
#### 4. Testing the points reversal logic:
```gherkin
  @ascenda @points-calculation
  Scenario: Reversal Points Calculation: Accrual and reversal of same amount should lead to zero points balance
    Given I create a new user:
      | status      | active      |
      | phoneNumber | +6590128976 |
      | birthDate   | 1987-06-05  |
      | countryCode | US          |
      | gender      | female      |
    And I make the following transactions:
      | posting_date | amount | account_type | international_currency | merchant_descriptor   |
      | 2024-07-27   | 10     | debit_card   | SGD                    | Shopping of clothes   |
      | 2024-07-28   | -10    | debit_card   | SGD                    | Shopping of jewellery |
    And I wait for the batch job to get executed
    Then the user's points balance should be 0
```

#### 5. Testing the points accrued for zero amount:
```gherkin
  @ascenda @points-calculation
  Scenario: Accrual Points Calculation: Purchase of zero amount should not increase the points balance
    Given I create a new user:
    | status      | active      |
    | phoneNumber | +6590128976 |
    | birthDate   | 1987-06-05  |
    | countryCode | US          |
    | gender      | female      |
    And I make the following transactions:
    | posting_date | amount | account_type | international_currency | merchant_descriptor   |
    | 2024-07-27   | 10     | debit_card   | SGD                    | Dentist Fees          |
    | 2024-07-27   | 0      | debit_card   | SGD                    | Shopping of clothes   |
    And I wait for the batch job to get executed
    Then the user's points balance should be 7.5
```
#### 6. Testing the points accrued for fractional amount:
```gherkin
  @ascenda @points-calculation @temp
  Scenario: Accrual Points Calculation: Purchase of fractional amount should increase the points balance accordingly
    Given I create a new user:
      | status      | active      |
      | phoneNumber | +6590128976 |
      | birthDate   | 1987-06-05  |
      | countryCode | US          |
      | gender      | female      |
    And I make the following transactions:
      | posting_date | amount | account_type | international_currency | merchant_descriptor   |
      | 2024-07-27   | 0.2    | debit_card   | SGD                    | Shopping of clothes   |
    And I wait for the batch job to get executed
    Then the user's points balance should be 0.15
```

### Tools & Technologies Used: ##

* Java17: as programming language
* Cucumber-jvm: To express the test scenarios in BDD gherkin format
* Okhttp: To listen to SSE event source for webhook integration
* RestAssured: Default http client for making the all the API requests
* Spring: For dependency injection
* Gradle: as build tool
* Allure: To generate beautiful test reports in HTML format
* GitHub Actions - CI/CD Integration

### Configurations:

This framework uses a `test.properties` file placed under `src/test/resources/properties` as configuration file and thing like `client_id`, `client_secret` can be configured from there.

```properties
client_id=189c1a95-b366-4444-a0f0-c7c0b56360ac
client_secret=787b36763e4b6c78c2cc0e92c72f56e1c9eab6af3d051e0d5838b33fc6a33382
grant_type=client_credentials
audience=api.us.kaligo-staging.xyz

sse_url=https://api.pipedream.com/sources/dc_eKu7BZw/sse
sse_token=Bearer 4d6dc4f542020376efdd35b840ad311c
```

### Test Report:

![Passed-Report.png](documents%2FPassed-Report.png)
