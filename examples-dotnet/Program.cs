using System;
using System.IO;
using System.Net.Http;
using System.Security.Cryptography;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

// Assuming the Signing class is available. For this minimal example, we include it directly.
// In a larger project, this would be in a separate library file.
public static class Signing
{
    static string B64Url(byte[] b) => Convert.ToBase64String(b).Replace("+", "-").Replace("/", "_").TrimEnd('=');

    public static string Sign(string method, string path, string clientId, string requestId, string requestTimeMs, string bodyJson, string privateKeyPem, string keyVersion = "1")
    {
        var content = $"{method}.{path}.{clientId}.{requestId}.{requestTimeMs}.{bodyJson}";
        using var rsa = RSA.Create();
        rsa.ImportFromPem(privateKeyPem);
        var sig = rsa.SignData(Encoding.UTF8.GetBytes(content), HashAlgorithmName.SHA256, RSASignaturePadding.Pkcs1);
        var s = Uri.EscapeDataString(B64Url(sig));
        return $"algorithm=RSA256,keyVersion={keyVersion},signature={s}";
    }
}

public class Program
{
    public static async Task Main(string[] args)
    {
        Console.WriteLine("--- Running Tokapay .NET Demo ---");

        DotNetEnv.Env.Load();

        var baseUrl = Environment.GetEnvironmentVariable("TOKAPAY_BASE_URL");
        var clientId = Environment.GetEnvironmentVariable("TOKAPAY_CLIENT_ID");
        var privateKeyPath = Environment.GetEnvironmentVariable("TOKAPAY_PRIVATE_KEY_PEM_PATH");
        var shopId = Environment.GetEnvironmentVariable("TOKAPAY_SHOP_ID");
        var keyVersion = Environment.GetEnvironmentVariable("TOKAPAY_KEY_VERSION") ?? "1";

        var privateKeyPem = await File.ReadAllTextAsync(privateKeyPath);

        var path = "/v1/acquiring/qr/create";
        var body = new
        {
            productCode = "CSB_DIRECTPAY_OFFLINE_STANDARD",
            order = new
            {
                orderTitle = "Coffee",
                merchantTransId = "m-" + DateTimeOffset.UtcNow.ToUnixTimeMilliseconds(),
                orderAmount = new { value = "1500", currency = "MXN" }
            },
            shopId = shopId
        };
        
        var bodyStr = JsonSerializer.Serialize(body, new JsonSerializerOptions { WriteIndented = false });
        var requestId = Guid.NewGuid().ToString("N");
        var requestTime = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds().ToString();

        var signature = Signing.Sign("POST", path, clientId, requestId, requestTime, bodyStr, privateKeyPem, keyVersion);

        using var client = new HttpClient();
        var request = new HttpRequestMessage(HttpMethod.Post, baseUrl + path)
        {
            Content = new StringContent(bodyStr, Encoding.UTF8, "application/json")
        };

        request.Headers.Add("Client-Id", clientId);
        request.Headers.Add("Request-Id", requestId);
        request.Headers.Add("Request-Time", requestTime);
        request.Headers.Add("Signature", signature);

        try
        {
            var response = await client.SendAsync(request);
            var responseBody = await response.Content.ReadAsStringAsync();
            Console.WriteLine($"Status Code: {response.StatusCode}");
            Console.WriteLine($"Response Body: {responseBody}");
        }
        catch (Exception e)
        {
            Console.Error.WriteLine($"Error sending request: {e.Message}");
        }
    }
}
