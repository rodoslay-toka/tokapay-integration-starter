# Tokapay API Catalog

This document summarizes the core API endpoints available in the Tokapay platform.

---

### 1. Create Dynamic QR Code

-   **Endpoint:** `POST /v1/acquiring/qr/create`
-   **When to use:** To generate a unique QR code for a specific transaction amount. The customer scans this QR code with their app to pay. This is ideal for point-of-sale scenarios where the amount is determined at the time of purchase.
-   **Common Fields:** `productCode`, `order` (`orderTitle`, `merchantTransId`, `orderAmount`), `shopId`.
-   **Status Handling:** A successful response contains a `qrUrl` and `qrData` that you can render as a QR code. The order has a limited time to be paid (e.g., 15 minutes).

---

### 2. Create Payment Order

-   **Endpoint:** `POST /v1/acquiring/payment/create`
-   **When to use:** For "B-scan-C" (merchant scans customer) or terminal-based QR payments. The `paymentMethod` in the request body specifies the payment scenario (e.g., `productCode` from a customer's presented QR code).
-   **Common Fields:** `productCode`, `paymentMethod` (`methodType`, `methodValue`), `order` (`orderTitle`, `merchantTransId`, `orderAmount`), `shopId`.
-   **Status Handling:** The initial response may be `PROCESSING`. You must use the Payment Result Inquiry endpoint to get the final status.

---

### 3. Payment Result Inquiry

-   **Endpoint:** `POST /v1/acquiring/payment/inquiry`
-   **When to use:** To check the status of a payment initiated via "Create Payment Order" or to confirm payment of a dynamic QR. Poll this endpoint after initiating a payment.
-   **Common Fields:** `merchantTransId`.
-   **Status Handling:** Poll until the `status` is `SUCCESS`, `FAIL`, or `CANCELLED`. A `SUCCESS` status confirms the payment was completed.

---

### 4. Close Payment Order

-   **Endpoint:** `POST /v1/acquiring/payment/close`
-   **When to use:** To explicitly cancel a payment order that is no longer needed, such as when a customer abandons a purchase. This releases any held resources.
-   **Common Fields:** `merchantTransId`.
-   **Status Handling:** A successful close operation will be confirmed in the response. This is useful for preventing orders from being paid after a timeout on the merchant's side.

---

### 5. Refund Apply

-   **Endpoint:** `POST /v1/acquiring/refund/apply`
-   **When to use:** To initiate a full or partial refund for a completed payment.
-   **Common Fields:** `refundRequestId`, `paymentId` (the original payment ID from Tokapay), `refundAmount`.
-   **Status Handling:** The initial response is typically `PROCESSING`. Use the Refund Result Query to get the final refund status.

---

### 6. Refund Result Query

-   **Endpoint:** `POST /v1/acquiring/refund/inquiry`
-   **When to use:** To check the status of a refund request.
-   **Common Fields:** `refundRequestId`.
-   **Status Handling:** Poll until the `status` is `SUCCESS` or `FAIL`.

---

### 7. Get Reconciliation File Link

-   **Endpoint:** `POST /v1/acquiring/recon/get`
-   **When to use:** To obtain a secure link to download a reconciliation file for a specific date. These files contain a summary of all transactions for accounting purposes.
-   **Common Fields:** `reconDate`, `reconType` (e.g., `TRANSACTION_DETAIL`).
-   **Status Handling:** A successful response will contain a `downloadUrl` which can be used to fetch the file.
