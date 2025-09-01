# Signing & Security

## Request signing (mandatory)
Content to be signed:
<HTTP-Method>.<HTTP-URI>.<Client-Id>.<Request-Id>.<Request-Time>.<Body>
- JSON body must be **compact** (no pretty spaces).
- Sign with **RSA-SHA256 (PKCS#1 v1.5)**.
- Encode signature as **base64url** (no padding) and **URL-encode** for the header.

Header:
Signature: algorithm=RSA256,keyVersion=<n>,signature=<base64url>
Required headers: `Client-Id`, `Request-Id` (UUID hex), `Request-Time` (**ms**).

## Response verification
Verify `<Client-Id>.<Response-Time>.<Response-Body>` with Tokapay public key before processing.

## Key handling
- Keep private keys only on your side; never commit.
- Use `TOKAPAY_KEY_VERSION` to reflect the active public key at Tokapay.
- Rotate by generating a new keypair and exchanging public keys.
