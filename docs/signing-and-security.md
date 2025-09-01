# Request Signing and Security

All requests to the Tokapay API must be digitally signed to ensure authenticity and integrity. This document explains how to create the signature and how to verify the signature on responses from Tokapay.

## Request Signing (Client → Tokapay)

### 1. Construct the Content String

The signature is generated from a specific string format. It is crucial to construct this string **exactly** as specified below, with no extra whitespace or characters.

The format is a dot-separated concatenation of five components:

`<HTTP-Method>.<HTTP-URI>.<Client-Id>.<Request-Id>.<Request-Time>.<Body>`

-   **`HTTP-Method`**: The uppercase HTTP method, e.g., `POST`.
-   **`HTTP-URI`**: The absolute path of the API endpoint, e.g., `/v1/acquiring/qr/create`.
-   **`Client-Id`**: Your unique client ID provided by Tokapay.
-   **`Request-Id`**: A unique identifier for this specific request (e.g., a UUID).
-   **`Request-Time`**: The timestamp of the request in **milliseconds** since the UNIX epoch.
-   **`Body`**: The request body as a **compact JSON string**. This means no pretty-printing, spaces, or newlines. For `GET` requests or requests with no body, this is an empty string.

**Example Content String:**
```
POST./v1/acquiring/qr/create.your_client_id.a1b2c3d4-e5f6-7890-1234-567890abcdef.1678886400000.{"productCode":"CSB_DIRECTPAY_OFFLINE_STANDARD","order":{"orderTitle":"Coffee","merchantTransId":"m-1678886400","orderAmount":{"value":"1500","currency":"MXN"}},"shopId":"your_shop_id"}
```

### 2. Generate the Signature

1.  **Sign:** Sign the UTF-8 encoded content string using **RSA-SHA256** with your private key (in PKCS#8 PEM format).
2.  **Encode:** The resulting binary signature must be encoded using **Base64URL** format (RFC 4648 §5).

### 3. Set the `Signature` Header

The final signature is sent in the `Signature` HTTP header with the following format:

`Signature: algorithm=RSA256,keyVersion=<n>,signature=<base64url_encoded_signature>`

-   **`algorithm`**: Must be `RSA256`.
-   **`keyVersion`**: The version number of the key pair you are using, provided by Tokapay.
-   **`signature`**: The Base64URL-encoded signature generated in the previous step.

**Example Header:**
```
Signature: algorithm=RSA256,keyVersion=1,signature=aBcDeFgHiJkLmNoPqRsTuVwXyZ...
```

## Response Verification (Tokapay → Client)

You should also verify the signature of responses you receive from Tokapay to ensure they have not been tampered with.

1.  **Construct the Content String:** The response content string is composed of the `Client-Id`, `Response-Time`, and the response body.
    `<Client-Id>.<Response-Time>.<Response-Body>`
2.  **Verify:** Use the **Tokapay Public Key** to verify the signature from the `Signature` header against this content string.

## Security Best Practices

-   **Key Storage:** Never commit your private key to version control. Store it in a secure location (like a hardware security module or a secure vault) and load it into your application at runtime. The `.gitignore` in this repository is set up to ignore `.pem` files and the `keys/` directory.
-   **Secret Logging:** Never log sensitive information, including the raw private key, the full signature content string, or the final signature header.
-   **Key Rotation:** Rotate your keys regularly. Contact Tokapay support to register a new public key and receive a new `keyVersion`.
