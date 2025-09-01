import "dotenv/config";
import fs from "fs";
import axios from "axios";
import crypto from "crypto";
import { signRequest } from "../../lib/node/signing.js";

const BASE = process.env.TOKAPAY_BASE_URL!;
const CLIENT = process.env.TOKAPAY_CLIENT_ID!;
const KV = process.env.TOKAPAY_KEY_VERSION || "1";
const PRIV = fs.readFileSync(process.env.TOKAPAY_PRIVATE_KEY_PEM_PATH!, "utf8");

export async function post(path: string, body: any) {
  const bodyStr = JSON.stringify(body);
  const requestId = crypto.randomUUID().replace(/-/g, "");
  const requestTime = Date.now().toString();
  const sig = signRequest({ method: "POST", path, clientId: CLIENT, requestId, requestTimeMs: requestTime, bodyJson: bodyStr, privateKeyPem: PRIV, keyVersion: KV });
  const res = await axios.post(BASE + path, body, {
    headers: {
      "Content-Type": "application/json",
      "Client-Id": CLIENT,
      "Request-Id": requestId,
      "Request-Time": requestTime,
      "Signature": sig
    },
    timeout: 15000
  });
  return res.data;
}

export const SHOP = process.env.TOKAPAY_SHOP_ID!;
export const TERMINAL = process.env.TOKAPAY_TERMINAL_ID!;
