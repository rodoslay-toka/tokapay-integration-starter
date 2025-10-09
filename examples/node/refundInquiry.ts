import { post } from "./common.js";
const refundRequestId = process.argv[2] || "<refundRequestId>";
post("/v2/acquiring/payment/refundQuery", { refundRequestId }).then(console.log).catch(e => console.error(e.response?.data || e));
