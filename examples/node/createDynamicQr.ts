import { post, SHOP } from "./common.js";
const path = "/v2/acquiring/qr/create";
const body = {
  productCode: "CSB_DIRECTPAY_OFFLINE_STANDARD",
  order: { orderTitle: "Coffee", merchantTransId: "m-"+Date.now(), orderAmount: { value: "1500", currency: "MXN" } },
  shopId: SHOP
};
post(path, body).then(console.log).catch(e => console.error(e.response?.data || e));
