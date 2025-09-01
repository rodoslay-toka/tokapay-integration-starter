package lib.java.model;

public record SigningPayload(
    String method,
    String path,
    String clientId,
    String requestId,
    String requestTimeMs,
    String bodyJson,
    String privateKeyBase64,
    String keyVersion
) {}
