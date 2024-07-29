  
**Use Case 1: Single Transaction with Standard Point Accrual**

* **Persona:** Bank Customer (User of the Loyalty Program)  
* **Background:** The customer has made a purchase using their linked debit card.  
* **Action:**  
  1. A transactionData is submitted to the Ascenda system through the Make Transaction API.  
  2. The batch job processes the transactionData.  
* **Expected Outcome:**  
  1. The transactionData appears in the Get Points Transactions API with a status of "confirmed" and the correct number of points calculated based on the 0.75 multiplier.  
  2. The user's points balance in the Get User Details API is updated to reflect the earned points.

**Use Case 2: Batch Transactions with Cumulative Points Calculation**

* **Persona:** Bank (Ascenda Client)  
* **Background:** The bank has processed multiple customer transactions throughout the day.  
* **Action:**  
  1. A batch of transactions is submitted to the Ascenda system.  
  1. The batch job processes all transactions.  
* **Expected Outcome:**  
  1. All transactions appear in the Get Points Transactions API in the correct order, with the appropriate points and status "confirmed."  
  1. The balance\_as\_of field in the Get Points Transactions API shows the cumulative points balance after each transactionData.  
  1. The total points balance in the Get User Details API accurately reflects the sum of all points earned in the batch.

**Use Case 3: Transaction Reversal with Points Adjustment**

* **Persona:** Bank (Ascenda Client)  
* **Background:** A customer has returned a purchase, and the bank needs to reverse the associated transactionData.  
* **Action:**  
  1. The bank submits a reversal request for the transactionData.  
  1. The batch job processes the reversal.  
* **Expected Outcome:**  
  1. The original transactionData is marked as reversed or voided in the Get Points Transactions API.  
  1. A corresponding reversal transactionData appears, deducting the previously awarded points.  
  1. The user's points balance in the Get User Details API is adjusted to remove the points from the reversed transactionData.

**Use Case 4: Partial Refund with Proportional Points Adjustment**

* **Persona:** Bank (Ascenda Client)  
* **Background:** A customer has received a partial refund for a purchase.  
* **Action:**  
  1. The bank submits a partial refund request.  
  1. The batch job processes the partial refund.  
* **Expected Outcome:**  
  1. A new points transactionData appears in the Get Points Transactions API, deducting points proportional to the refund amount.  
  1. The user's points balance in the Get User Details API is adjusted accordingly.

**Use Case 5: Batch Job Verification and Monitoring**

* **Persona:** Ascenda System Administrator  
* **Background:** The system administrator needs to verify the batch job is running correctly.  
* **Action:**  
  1. Monitor the system logs for batch job execution.  
  1. Verify that the batch job runs at 15-minute intervals.  
  1. Check that all pending transactions are processed during the batch job.  
* **Expected Outcome:**  
  1. The batch job logs indicate successful execution at the expected intervals.  
  1. No errors are reported in the logs during batch job processing.  
  1. All pending transactions are processed and updated accordingly.
