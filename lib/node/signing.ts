import crypto from "crypto";

export function b64url(buf: Buffer) {
  return buf.toString("base64").replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/g, "");
}

export function signRequest(opts: {
  method: "POST";
  path: string;        // e.g. /v1/acquiring/qr/create
  clientId: string;
  requestId: string;
  requestTimeMs: string;
  bodyJson: string;    // compact JSON
  privateKeyPem: string;
  keyVersion?: string;
}) {
  const { method, path, clientId, requestId, requestTimeMs, bodyJson, privateKeyPem } = opts;
  const content = `${method}.${path}.${clientId}.${requestId}.${requestTimeMs}.${bodyJson}`;
  const signer = crypto.createSign("RSA-SHA256");
  signer.update(Buffer.from(content, "utf8"));
  const sig = signer.sign(privateKeyPem);
  const signature = encodeURIComponent(b64url(sig));
  const keyVersion = opts.keyVersion ?? "1";
  return `algorithm=RSA256,keyVersion=${keyVersion},signature=${signature}`;
}
