import base64, os
from urllib.parse import quote
from cryptography.hazmat.primitives import serialization, hashes
from cryptography.hazmat.primitives.asymmetric import padding

def _b64url(b: bytes) -> str:
    return base64.urlsafe_b64encode(b).decode().rstrip("=")

def sign_request(method, path, client_id, request_id, request_time_ms, body_json, private_key_pem, key_version="1"):
    content = f"{method}.{path}.{client_id}.{request_id}.{request_time_ms}.{body_json}"
    key = serialization.load_pem_private_key(private_key_pem.encode(), password=None)
    sig = key.sign(content.encode("utf-8"), padding.PKCS1v15(), hashes.SHA256())
    return f"algorithm=RSA256,keyVersion={key_version},signature={quote(_b64url(sig))}"
