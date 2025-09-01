import okhttp3.*; import java.util.*; import java.nio.charset.StandardCharsets;
import java.io.*; import java.util.Base64;
public class App {
  public static void main(String[] args) throws Exception {
    String BASE = System.getenv("TOKAPAY_BASE_URL");
    String CLIENT = System.getenv("TOKAPAY_CLIENT_ID");
    String KV = Optional.ofNullable(System.getenv("TOKAPAY_KEY_VERSION")).orElse("1");
    String SHOP = System.getenv("TOKAPAY_SHOP_ID");
    String privB64 = Base64.getEncoder().encodeToString(new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(System.getenv("TOKAPAY_PRIVATE_KEY_PEM_PATH")))).getBytes(StandardCharsets.UTF_8));
    String path = "/v1/acquiring/qr/create";
    String bodyJson = "{\"productCode\":\"CSB_DIRECTPAY_OFFLINE_STANDARD\",\"order\":{\"orderTitle\":\"Coffee\",\"merchantTransId\":\"m-"+System.currentTimeMillis()+"\",\"orderAmount\":{\"value\":\"1500\",\"currency\":\"MXN\"}},\"shopId\":\""+SHOP+"\"}";
    String rid = UUID.randomUUID().toString().replace("-","");
    String rtime = String.valueOf(System.currentTimeMillis());
    String sig = lib.java.SigningUtil.sign("POST", path, CLIENT, rid, rtime, bodyJson, privB64, KV);
    OkHttpClient client = new OkHttpClient();
    Request req = new Request.Builder().url(BASE+path)
      .addHeader("Content-Type","application/json").addHeader("Client-Id", CLIENT)
      .addHeader("Request-Id", rid).addHeader("Request-Time", rtime).addHeader("Signature", sig)
      .post(RequestBody.create(bodyJson, MediaType.parse("application/json"))).build();
    try (Response res = client.newCall(req).execute()) {
      System.out.println(res.body().string());
    }
  }
}
