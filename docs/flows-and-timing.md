# Flows & Timing

## Dynamic QR
1) Create dynamic QR (15 min typical validity).
2) Display on screen; user scans and pays.
3) Poll every ~5s or handle notify/webhook.
4) Always verify response signature.

## Static QR (terminal) and B-scan-C (user code)
- Use `payment/create` with proper `productCode`.
- For B-scan-C include `paymentCode`.
- Poll/notify; verify signatures.

## Idempotency & retries
- Payments: use `merchantTransId`.
- Refunds: use `refundRequestId`.
- Implement bounded retries with backoff for network/timeout errors.
