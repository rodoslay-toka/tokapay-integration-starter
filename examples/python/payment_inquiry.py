import sys
from common import post
payment_id = sys.argv[1] if len(sys.argv)>1 else "<paymentId>"
print(post("/v1/acquiring/payment/inquiry", {"paymentId": payment_id}))
