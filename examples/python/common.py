import os, json, uuid, time, requests
from lib.python.signing import sign_request

BASE = os.environ["TOKAPAY_BASE_URL"]
CLIENT = os.environ["TOKAPAY_CLIENT_ID"]
KV = os.getenv("TOKAPAY_KEY_VERSION","1")
with open(os.environ["TOKAPAY_PRIVATE_KEY_PEM_PATH"],"r") as f:
    PRIV = f.read()

def post(path, body):
    body_json = json.dumps(body, separators=(',',':'))
    rid = uuid.uuid4().hex
    rtime = str(int(time.time()*1000))
    sig = sign_request("POST", path, CLIENT, rid, rtime, body_json, PRIV, key_version=KV)
    resp = requests.post(BASE+path, json=body, headers={
        "Client-Id": CLIENT,
        "Request-Id": rid,
        "Request-Time": rtime,
        "Signature": sig,
        "Content-Type": "application/json"
    }, timeout=15)
    resp.raise_for_status()
    return resp.json()
