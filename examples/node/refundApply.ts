import { post } from "./common.js";
const paymentId = process.argv[2] || "<paymentId>";
const refundRequestId = "r-" + Date.now();
const body = { paymentId, refundRequestId, refundAmount: { value: "1200", currency: "MXN" } };
post("/v1/acquiring/refund/apply", body).then(console.log).catch(e => console.error(e.response?.data || e));
