# Integration Flows and Timing

This document outlines common integration flows, timing considerations, and best practices for building a robust integration with the Tokapay API.

## Payment Flows

### 1. Dynamic QR Code Flow (Customer-Scans-Merchant)

This is the most common flow for in-person payments where the transaction amount is known at the time of purchase.

1.  **Merchant App:** Your application calls `POST /v1/acquiring/qr/create` with the order details.
2.  **Tokapay:** Generates a unique QR code and returns the `qrUrl` / `qrData`. The order is now in a `PENDING` state. A typical expiry for a dynamic QR is 15 minutes.
3.  **Merchant App:** Displays the QR code to the customer on a screen.
4.  **Customer:** Scans the QR code with their mobile app and confirms the payment.
5.  **Merchant App:** Your application needs to get the payment result. You have two options:
    *   **Polling (Recommended):** Start polling the `POST /v1/acquiring/payment/inquiry` endpoint using the `merchantTransId` from the initial request. A polling interval of ~5 seconds is a good starting point. Continue polling until the status is `SUCCESS`, `FAIL`, or you reach a timeout.
    *   **Webhook:** Configure a webhook URL in your Tokapay merchant portal. Tokapay will send a notification to this URL when the payment status changes. You must still verify the signature of the webhook payload.
6.  **Verification:** Once you receive a `SUCCESS` status, verify the signature of the response (from polling or webhook) to confirm its authenticity.

### 2. Static QR & B-scan-C Flow (Merchant-Scans-Customer)

This flow is used when a merchant scans a QR code presented by the customer. This can be a static QR for a location or a dynamic payment code from the customer's app.

1.  **Customer:** Presents a QR code to the merchant.
2.  **Merchant App:** Scans the QR code to get a `productCode`.
3.  **Merchant App:** Calls `POST /v1/acquiring/payment/create` with the `productCode` included in the `paymentMethod` object, along with order details.
4.  **Tokapay:** The initial response status is often `PROCESSING`.
5.  **Merchant App:** Poll the `POST /v1/acquiring/payment/inquiry` endpoint until a final status (`SUCCESS` or `FAIL`) is received.
6.  **Verification:** Verify the signature of the final response.

## Retries and Idempotency

Network issues can happen. To prevent duplicate transactions, the Tokapay API supports idempotency using unique request identifiers.

-   **`merchantTransId`**: For payment creation, use a unique ID for each order. If you retry a `payment/create` call with the same `merchantTransId`, Tokapay will not create a new transaction but will instead return the status of the original one.
-   **`refundRequestId`**: For refunds, use a unique ID for each refund attempt. Retrying a `refund/apply` call with the same `refundRequestId` will not process a duplicate refund.

**Retry Strategy:**
-   For network errors or HTTP 5xx server errors, implement an exponential backoff strategy for retries. For example, wait 1s, then 2s, then 4s before retrying.
-   Do not retry on HTTP 4xx client errors, as these indicate a problem with your request that needs to be fixed.
-   Always use the inquiry endpoints to confirm the status of a transaction if you are unsure whether a previous call succeeded.
