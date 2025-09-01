import "dotenv/config";
import fs from "fs";
import axios from "axios";
import crypto from "crypto";
import { signRequest } from "../../lib/node/signing.js";

const BASE = process.env.TOKAPAY_BASE_URL!;
const CLIENT = process.env.TOKAPAY_CLIENT_ID!;
const PRIV = fs.readFileSync(process.env.TOKAPAY_PRIVATE_KEY_PEM_PATH!, "utf8");

async function main() {
  const path = "/v1/acquiring/recon/get";
  const yesterday = new Date();
  yesterday.setDate(yesterday.getDate() - 1);
  const reconDate = yesterday.toISOString().split('T')[0]; // YYYY-MM-DD

  const body = {
    reconDate: reconDate,
    reconType: "TRANSACTION_DETAIL"
  };
  const bodyStr = JSON.stringify(body);
  const requestId = crypto.randomUUID().replace(/-/g, "");
  const requestTime = Date.now().toString();

  const sig = signRequest({
    method: "POST",
    path,
    clientId: CLIENT,
    requestId,
    requestTimeMs: requestTime,
    bodyJson: bodyStr,
    privateKeyPem: PRIV,
    keyVersion: process.env.TOKAPAY_KEY_VERSION || "1"
  });

  try {
    const res = await axios.post(BASE + path, body, {
      headers: {
        "Content-Type": "application/json",
        "Client-Id": CLIENT,
        "Request-Id": requestId,
        "Request-Time": requestTime,
        "Signature": sig
      }
    });
    console.log("SUCCESS:", res.data);
  } catch (e: any) {
    console.error("ERROR:", e.response?.data || e.message);
  }
}

main();
