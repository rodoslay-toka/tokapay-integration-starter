import { post } from "./common.js";
const paymentId = process.argv[2] || "<paymentId>";
post("/v1/acquiring/payment/inquiry", { paymentId }).then(console.log).catch(e => console.error(e.response?.data || e));
