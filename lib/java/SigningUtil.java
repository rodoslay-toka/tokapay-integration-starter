import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class SigningUtil {
  private static String b64url(byte[] b) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
  }
  public static String sign(String method, String path, String clientId, String requestId, String requestTimeMs, String bodyJson, String privateKeyBase64, String keyVersion) {
    try {
      String content = String.format("%s.%s.%s.%s.%s.%s", method, path, clientId, requestId, requestTimeMs, bodyJson);
      Signature signature = Signature.getInstance("SHA256withRSA");
      // The private key must be in Base64 format, without PEM headers/footers
      byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      PrivateKey priKey = kf.generatePrivate(spec);
      
      signature.initSign(priKey);
      signature.update(content.getBytes(StandardCharsets.UTF_8));
      
      String signed = b64url(signature.sign());
      return "algorithm=RSA256,keyVersion=" + keyVersion + ",signature=" + URLEncoder.encode(signed, "UTF-8");
    } catch(Exception e) {
      throw new RuntimeException("Failed to sign request", e);
    }
  }
}
