# API Catalog

Base URL: `https://openapi.paypay.mx`

| Endpoint | Purpose | Notes |
|---|---|---|
| `POST /v1/acquiring/qr/create` | Create Dynamic QR | Returns `qrCode`, `paymentId` |
| `POST /v1/acquiring/payment/create` | Create payment order | Static QR or B-scan-C (payment code) |
| `POST /v1/acquiring/payment/inquiry` | Check payment status | `PROCESSING` / `SUCCESS` / `FAILED` |
| `POST /v1/acquiring/payment/close` | Close unpaid order | Provide `paymentId` |
| `POST /v1/acquiring/refund/apply` | Start refund | Use `refundRequestId`; may be async |
| `POST /v1/acquiring/refund/inquiry` | Get refund result | Use `refundId` or `refundRequestId` |
| `POST /v1/acquiring/recon/get` | Reconciliation links | Hourly / daily links |
