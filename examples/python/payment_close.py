import sys
from common import post
payment_id = sys.argv[1] if len(sys.argv)>1 else "<paymentId>"
print(post("/v2/acquiring/payment/close", {"paymentId": payment_id}))
