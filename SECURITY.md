# Security

- Never commit private keys, PEMs, or `.env` files.
- Store keys locally under `./keys/`. `.gitignore` blocks these by default.
- `Request-Time` must be milliseconds; drifted timestamps can cause signature failures.
- Rotate keys by:
  1) Generating a new RSA keypair.
  2) Exchanging new public keys with Tokapay.
  3) Bumping `TOKAPAY_KEY_VERSION`.
- Always verify *response* signatures before trusting the response body.
- If you suspect key exposure: revoke keys, rotate immediately, and re-exchange public keys.
