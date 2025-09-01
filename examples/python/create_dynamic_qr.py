import os, time
from common import post
body = {
  "productCode":"CSB_DIRECTPAY_OFFLINE_STANDARD",
  "order":{"orderTitle":"Coffee","merchantTransId":"m-"+str(int(time.time()*1000)),"orderAmount":{"value":"1500","currency":"MXN"}},
  "shopId": os.environ["TOKAPAY_SHOP_ID"]
}
print(post("/v1/acquiring/qr/create", body))
