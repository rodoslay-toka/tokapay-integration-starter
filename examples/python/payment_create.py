import os, time
from common import post
body = {
  "productCode":"CSB_DIRECTPAY_OFFLINE_TERMINALQR",
  "shopId": os.environ["TOKAPAY_SHOP_ID"],
  "terminalId": os.environ["TOKAPAY_TERMINAL_ID"],
  "order":{"orderTitle":"Static QR","merchantTransId":"m-"+str(int(time.time()*1000)),"orderAmount":{"value":"1200","currency":"MXN"}}
}
print(post("/v1/acquiring/payment/create", body))
