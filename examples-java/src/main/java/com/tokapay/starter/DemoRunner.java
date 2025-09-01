package com.tokapay.starter;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;
import java.util.Base64;

// Assuming SigningUtil is in the classpath, e.g., from lib/java
// For a real project, you'd package lib/java into a JAR and include it as a dependency.
// For this example, we'll assume it's available.
// import com.tokapay.SigningUtil;

@Component
public class DemoRunner implements CommandLineRunner {

    // This is a simplified copy of the SigningUtil for demonstration purposes,
    // as we cannot easily link the `lib/java` source file in this structure.
    // In a real Maven project, `lib/java/SigningUtil.java` would be under `src/main/java`
    // of its own module or within this module.
    public static class SigningUtil {
        private static String b64url(byte[] b) {
            return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(b);
        }
        public static String sign(String method, String path, String clientId, String requestId, String requestTimeMs, String bodyJson, String privateKeyPem, String keyVersion) {
            try {
                String content = String.format("%s.%s.%s.%s.%s.%s", method, path, clientId, requestId, requestTimeMs, bodyJson);
                
                privateKeyPem = privateKeyPem.replace("-----BEGIN PRIVATE KEY-----", "")
                                             .replaceAll("\\r\\n|\\n", "")
                                             .replace("-----END PRIVATE KEY-----", "");

                byte[] keyBytes = Base64.getDecoder().decode(privateKeyPem);
                java.security.spec.PKCS8EncodedKeySpec spec = new java.security.spec.PKCS8EncodedKeySpec(keyBytes);
                java.security.KeyFactory kf = java.security.KeyFactory.getInstance("RSA");
                java.security.PrivateKey priKey = kf.generatePrivate(spec);

                java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
                signature.initSign(priKey);
                signature.update(content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                
                String signed = b64url(signature.sign());
                return "algorithm=RSA256,keyVersion=" + keyVersion + ",signature=" + java.net.URLEncoder.encode(signed, "UTF-8");
            } catch(Exception e) {
                throw new RuntimeException("Failed to sign request", e);
            }
        }
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Running Tokapay Java Demo ---");

        Dotenv dotenv = Dotenv.load();
        String baseUrl = dotenv.get("TOKAPAY_BASE_URL");
        String clientId = dotenv.get("TOKAPAY_CLIENT_ID");
        String privateKeyPath = dotenv.get("TOKAPAY_PRIVATE_KEY_PEM_PATH");
        String shopId = dotenv.get("TOKAPAY_SHOP_ID");
        String keyVersion = dotenv.get("TOKAPAY_KEY_VERSION", "1");

        String privateKeyPem = new String(Files.readAllBytes(Paths.get(privateKeyPath)));

        String path = "/v1/acquiring/qr/create";
        String bodyJson = String.format("""
                {"productCode":"CSB_DIRECTPAY_OFFLINE_STANDARD","order":{"orderTitle":"Coffee","merchantTransId":"m-%s","orderAmount":{"value":"1500","currency":"MXN"}},"shopId":"%s"}""",
                Instant.now().toEpochMilli(), shopId).replaceAll("\\s", "");

        String requestId = UUID.randomUUID().toString().replace("-", "");
        String requestTimeMs = String.valueOf(Instant.now().toEpochMilli());

        String signature = SigningUtil.sign("POST", path, clientId, requestId, requestTimeMs, bodyJson, privateKeyPem, keyVersion);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .header("Client-Id", clientId)
                .header("Request-Id", requestId)
                .header("Request-Time", requestTimeMs)
                .header("Signature", signature)
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
        } catch (Exception e) {
            System.err.println("Error sending request: " + e.getMessage());
        }
        
        // Exit after run
        System.exit(0);
    }
}
