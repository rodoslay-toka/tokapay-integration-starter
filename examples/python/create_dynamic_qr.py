import os, json, uuid, time, requests, sys
from dotenv import load_dotenv
# Add lib to path
sys.path.append(os.path.join(os.path.dirname(__file__), '..', '..'))
from lib.python.signing import sign_request

load_dotenv()

BASE=os.environ["TOKAPAY_BASE_URL"]
CLIENT=os.environ["TOKAPAY_CLIENT_ID"]
with open(os.environ["TOKAPAY_PRIVATE_KEY_PEM_PATH"],"r") as f: PRIV=f.read()
SHOP=os.environ["TOKAPAY_SHOP_ID"]
KV=os.getenv("TOKAPAY_KEY_VERSION","1")

def post(path, body):
    body_json=json.dumps(body,separators=(',',':'))
    rid=uuid.uuid4().hex
    rtime=str(int(time.time()*1000))
    sig=sign_request("POST", path, CLIENT, rid, rtime, body_json, PRIV, key_version=KV)
    
    try:
        resp=requests.post(BASE+path, json=body, headers={
            "Client-Id":CLIENT,
            "Request-Id":rid,
            "Request-Time":rtime,
            "Signature":sig,
            "Content-Type":"application/json"
        }, timeout=15)
        resp.raise_for_status()
        return resp.json()
    except requests.exceptions.RequestException as e:
        print(f"ERROR: {e.response.json() if e.response else e}")
        return None

if __name__ == "__main__":
    response = post("/v1/acquiring/qr/create",{
      "productCode":"CSB_DIRECTPAY_OFFLINE_STANDARD",
      "order":{
          "orderTitle":"Coffee",
          "merchantTransId":"m-"+str(int(time.time()*1000)),
          "orderAmount":{"value":"1500","currency":"MXN"}
      },
      "shopId":SHOP
    })
    if response:
        print("SUCCESS:", json.dumps(response, indent=2))
