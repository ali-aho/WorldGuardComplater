
import paramiko, hashlib

HOST, PORT = "node-3-th.berno.app", 2022
USER = "u_09339413432.119e34bc"
PW = "hGWahiygeTwoDvC8"

t = paramiko.Transport((HOST, PORT))
t.connect(username=USER, password=PW)
sftp = paramiko.SFTPClient.from_transport(t)
with sftp.open("plugins/WorldGuardComplater-1.1.0.jar", "rb") as f:
    data = f.read()
print("remote md5:", hashlib.md5(data).hexdigest(), len(data), "bytes")
sftp.close(); t.close()
