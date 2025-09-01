# Security Policy

## Key Storage and Management

-   **Private Keys:** Your private key (`merchant_private_key.pem`) is highly sensitive. It must be stored securely and should **never** be committed to version control. This repository's `.gitignore` file is configured to prevent accidental commits of `.pem` files and the `keys/` directory.
-   **Key Rotation:** We recommend rotating your keys periodically. To do this, generate a new key pair and coordinate with Tokapay support to register the new public key. You will be assigned a new `keyVersion`, which you must update in your `.env` file and API calls.
-   **Incident Response:** If you suspect your private key has been compromised, you must:
    1.  Immediately contact Tokapay support to have the associated public key revoked.
    2.  Generate a new key pair.
    3.  Securely provide the new public key to Tokapay for registration.
    4.  Update your systems with the new private key and `keyVersion`.

## Public Key Exchange

To request a new public key exchange (e.g., for key rotation or initial setup), please contact our developer support team with your `client_id`.

## CI/CD Security

The CI workflow in this repository includes a secret scanning step (`gitleaks`) to detect any accidentally committed secrets, including PEM key text. This check runs on every push and pull request to help prevent secret leaks.
