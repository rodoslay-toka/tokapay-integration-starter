from datetime import date
from common import post
today = date.today().isoformat()
print(post("/v1/acquiring/recon/get", {"reconDate": today, "settlePeriod": "day"}))
