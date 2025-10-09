# API Catalog

Base URLs:
- Production: `https://openapi.paypay.mx`
- Testing (SIT): `https://openapi.sit.toka.whalecloud.ltd`

| Endpoint | Purpose | Notes |
|---|---|---|
| `POST /v2/acquiring/qr/create` | Create Dynamic QR | Returns `qrCode`, `paymentId` |
| `POST /v2/acquiring/payment/create` | Create payment order | Static QR or B-scan-C (payment code) |
| `POST /v2/acquiring/payment/inquiry` | Check payment status | `PROCESSING` / `SUCCESS` / `FAILED` |
| `POST /v2/acquiring/payment/close` | Close unpaid order | Provide `paymentId` |
| `POST /v2/acquiring/payment/refund` | Start refund | Use `refundRequestId`; may be async |
| `POST /v2/acquiring/payment/refundQuery` | Get refund result | Use `refundId` or `refundRequestId` |
| `POST /v1/acquiring/recon/get` | Reconciliation links | Hourly / daily links |
| `POST /v2/acquiring/user/info` | Get user info | Returns user account information |
| `POST /v2/acquiring/merchant/info` | Get merchant info | Returns merchant information |
