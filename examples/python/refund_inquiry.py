import sys
from common import post
refundRequestId = sys.argv[1] if len(sys.argv)>1 else "<refundRequestId>"
print(post("/v1/acquiring/refund/inquiry", {"refundRequestId": refundRequestId}))
