using System;
using System.Security.Cryptography;
using System.Text;
using System.Web;

public static class Signing {
  static string B64Url(byte[] b) => Convert.ToBase64String(b).Replace("+","-").Replace("/","_").TrimEnd('=');
  
  public static string Sign(string method, string path, string clientId, string requestId, string requestTimeMs, string bodyJson, string privateKeyPem, string keyVersion="1") {
    var content = $"{method}.{path}.{clientId}.{requestId}.{requestTimeMs}.{bodyJson}";
    
    using var rsa = RSA.Create();
    // Starting from .NET Core 3.0, ImportFromPem is available.
    // For older versions, you would need a library like BouncyCastle or parse the PEM manually.
    rsa.ImportFromPem(privateKeyPem);
    
    var sig = rsa.SignData(Encoding.UTF8.GetBytes(content), HashAlgorithmName.SHA256, RSASignaturePadding.Pkcs1);
    
    // Use Uri.EscapeDataString for URL encoding, which is safer than HttpUtility.UrlEncode
    var s = Uri.EscapeDataString(B64Url(sig));
    
    return $"algorithm=RSA256,keyVersion={keyVersion},signature={s}";
  }
}
