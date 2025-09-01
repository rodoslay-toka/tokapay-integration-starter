import { post, SHOP, TERMINAL } from "./common.js";
// Static QR terminal example (change productCode to BSC_PAYMENT_CODE for B-scan-C and add paymentCode)
const path = "/v1/acquiring/payment/create";
const body = {
  productCode: "CSB_DIRECTPAY_OFFLINE_TERMINALQR",
  shopId: SHOP,
  terminalId: TERMINAL,
  order: { orderTitle: "Static QR", merchantTransId: "m-"+Date.now(), orderAmount: { value: "1200", currency: "MXN" } }
};
post(path, body).then(console.log).catch(e => console.error(e.response?.data || e));
