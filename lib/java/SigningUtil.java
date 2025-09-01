package lib.java;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import lib.java.model.SigningPayload;

public class SigningUtil {
  private SigningUtil() {}
  private static String b64url(byte[] b) { return Base64.getUrlEncoder().withoutPadding().encodeToString(b); }

  public static String sign(SigningPayload p) {
    try {
      String content = String.format("%s.%s.%s.%s.%s.%s", p.method(), p.path(), p.clientId(), p.requestId(), p.requestTimeMs(), p.bodyJson());
      Signature signature = Signature.getInstance("SHA256withRSA");
      PrivateKey priKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(p.privateKeyBase64())));
      signature.initSign(priKey);
      signature.update(content.getBytes(StandardCharsets.UTF_8));
      String s = b64url(signature.sign());
      return "algorithm=RSA256,keyVersion=" + p.keyVersion() + ",signature=" + URLEncoder.encode(s, "UTF-8");
    } catch(Exception e) { throw new SigningException("Failed to sign request", e); }
  }
}
