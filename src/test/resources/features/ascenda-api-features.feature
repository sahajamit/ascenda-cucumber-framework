Feature: Ascenda API features for managing users and transactions

  @ascenda-api @user-creation
  Scenario: User Creation: Create a new user and assert that the initial point's balance is shown as zero
    Given I create a new user:
      | status      | active      |
      | phoneNumber | +6590128976 |
      | birthDate   | 1987-06-05  |
      | countryCode | US          |
      | gender      | male        |
    Then the user's points balance should be 0

  @ascenda-api @transactions @single-transaction
  Scenario: Transactions: Post a single transaction and reconcile the points
    Given I create a new user:
      | status      | active      |
      | phoneNumber | +6590128976 |
      | birthDate   | 1987-06-05  |
      | countryCode | US          |
      | gender      | female      |
    And I make a single transaction:
      | posting_date           | 2024-07-27          |
      | amount                 | 100                 |
      | account_type           | debit_card          |
      | international_currency | SGD                 |
      | merchant_descriptor    | Shopping of clothes |
    And I wait for the batch job to get executed
    Then I reconcile my single transaction data with the transactions history api response
    Then I reconcile my single transaction data with the transaction detail api response
    Then I reconcile my total points balance with the user details api response


  @ascenda-api @transactions
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

  @ascenda-api @points-calculation
  Scenario: X Random Transactions: Posting x number of random transactions and then validating the points accrued.
    Given I create a new user:
      | status      | active      |
      | phoneNumber | +6590128976 |
      | birthDate   | 1987-06-05  |
      | countryCode | US          |
      | gender      | female      |
    And I make 2 random transactions
    And I wait for the batch job to get executed
    Then I reconcile all my transactions data with the transactions history api response
    Then I reconcile all my transactions data with the transaction detail api response
    Then I reconcile my total points balance with the user details api response

  @ascenda-api @points-calculation
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

  @ascenda-api @points-calculation
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

  @ascenda-api @points-calculation
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

