using System;
using System.IO;
using System.Net.Http;
using System.Text;
using System.Text.Json;

class Program {
  static async System.Threading.Tasks.Task Main() {
    var BASE = Environment.GetEnvironmentVariable("TOKAPAY_BASE_URL");
    var CLIENT = Environment.GetEnvironmentVariable("TOKAPAY_CLIENT_ID");
    var KV = Environment.GetEnvironmentVariable("TOKAPAY_KEY_VERSION") ?? "1";
    var SHOP = Environment.GetEnvironmentVariable("TOKAPAY_SHOP_ID");
    var priv = await File.ReadAllTextAsync(Environment.GetEnvironmentVariable("TOKAPAY_PRIVATE_KEY_PEM_PATH"));
    var path = "/v1/acquiring/qr/create";
    var body = JsonSerializer.Serialize(new {
      productCode = "CSB_DIRECTPAY_OFFLINE_STANDARD",
      order = new { orderTitle="Coffee", merchantTransId=$"m-{DateTimeOffset.Now.ToUnixTimeMilliseconds()}", orderAmount=new { value="1500", currency="MXN" } },
      shopId = SHOP
    });
    var rid = Guid.NewGuid().ToString("N");
    var rtime = DateTimeOffset.Now.ToUnixTimeMilliseconds().ToString();
    var sig = Signing.Sign("POST", path, CLIENT, rid, rtime, body, priv, KV);
    var http = new HttpClient();
    var req = new HttpRequestMessage(HttpMethod.Post, BASE+path);
    req.Content = new StringContent(body, Encoding.UTF8, "application/json");
    req.Headers.TryAddWithoutValidation("Client-Id", CLIENT);
    req.Headers.TryAddWithoutValidation("Request-Id", rid);
    req.Headers.TryAddWithoutValidation("Request-Time", rtime);
    req.Headers.TryAddWithoutValidation("Signature", sig);
    var res = await http.SendAsync(req);
    Console.WriteLine(await res.Content.ReadAsStringAsync());
  }
}
