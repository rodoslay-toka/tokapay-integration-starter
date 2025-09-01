import sys, time
from common import post
payment_id = sys.argv[1] if len(sys.argv)>1 else "<paymentId>"
refundRequestId = "r-"+str(int(time.time()*1000))
body = {"paymentId": payment_id, "refundRequestId": refundRequestId, "refundAmount":{"value":"1200","currency":"MXN"}}
print(post("/v1/acquiring/refund/apply", body))
