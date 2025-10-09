import sys
from common import post
refundRequestId = sys.argv[1] if len(sys.argv)>1 else "<refundRequestId>"
print(post("/v2/acquiring/payment/refundQuery", {"refundRequestId": refundRequestId}))
