# Tokapay Integration Starter (Template)

> 👉 Click **Use this template** to start a new repo. Do **not** fork.

This template gives partner developers a working starter for Tokapay acquiring APIs with:
- **Signing helpers** (RSA-SHA256) for Node, Python, Java, and .NET
- **7 runnable examples** (Node & Python): create dynamic QR, create payment order, inquiry, close, refund apply, refund inquiry, recon get
- **Postman collection** with pre-request scaffolding
- **Docs** on signing, API catalog, flows/timing
- **CI** secret scan + sanity tests
- **Security** guidance for key handling and rotation

## 3-minute Quickstart
1. Create a repo from this template → clone it.
2. Copy `.env.example` to `.env`. Put your keys under `./keys/` (never commit).
3. Run an example:
   - **Node**: `pnpm i && pnpm ts-node examples/node/createDynamicQr.ts`
   - **Python**: `pip install -r requirements.txt && python examples/python/create_dynamic_qr.py`

Then:
- Call **payment inquiry** until `SUCCESS`.
- Try **refund apply** → optionally **refund inquiry**.
- Fetch **recon file link** for today.

## Required headers & signing

**Content-To-Be-Signed (request)**  
`<HTTP-Method>.<HTTP-URI>.<Client-Id>.<Request-Id>.<Request-Time>.<Body>`

- Use **compact JSON** for `<Body>` (no spaces).
- `Request-Time` is **milliseconds** since epoch.
- Compute **RSA-SHA256 (PKCS#1 v1.5)** → **base64url** (no padding) → **URL-encode**.

**Signature header**  
`Signature: algorithm=RSA256,keyVersion=<n>,signature=<base64url>`

**Response verification**  
Build `<Client-Id>.<Response-Time>.<Response-Body>` and verify with Tokapay **public key** before trusting the body.

## API endpoints (base: `https://openapi.paypay.mx`)
- `POST /v1/acquiring/qr/create` → Dynamic QR
- `POST /v1/acquiring/payment/create` → Static QR or B-scan-C (Payment Code)
- `POST /v1/acquiring/payment/inquiry` → Poll status
- `POST /v1/acquiring/payment/close` → Close unpaid order
- `POST /v1/acquiring/refund/apply` → Start refund
- `POST /v1/acquiring/refund/inquiry` → Check refund status
- `POST /v1/acquiring/recon/get` → Get recon file links

## Flows & timing
- **Dynamic QR** shows on screen; typical validity ~15 minutes; poll ~5s or use notify/webhook; always verify signatures.
- **Static QR** (terminal) and **B-scan-C** (scan user payment code) use `payment/create`; poll/notify; verify signatures.
- Use idempotency fields like `merchantTransId` (payments) and `refundRequestId` (refunds).

See `/docs` for full details.
=======
# tokapay-integration-starter

- Step 1: “Use this template” → clone.
- Step 2: copy .env.example → .env, put keys in ./keys (never commit).
- Step 3: run one working sample per language (Node, Python; Java/.NET optional if still WIP).
