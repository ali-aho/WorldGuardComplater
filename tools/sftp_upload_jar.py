# -*- coding: utf-8 -*-
# Upload fixed WGC jar to the real server + verify hash matches
import paramiko, hashlib

LOCAL = r"C:\Users\ALI\worldguard-complater\target\WorldGuardComplater-1.0.0.jar"
REMOTE = 'plugins/WorldGuardComplater-1.0.0.jar'

h = hashlib.md5()
with open(LOCAL, 'rb') as f:
    h.update(f.read())
print("local  md5:", h.hexdigest(), "size:", len(open(LOCAL,'rb').read()))

t = paramiko.Transport(('node-3-th.berno.app', 2022))
t.connect(username='u_09339413432.119e34bc', password='hGWahiygeTwoDvC8')
sftp = paramiko.SFTPClient.from_transport(t)
print("SFTP opened")

sftp.put(LOCAL, REMOTE)
st = sftp.stat(REMOTE)
print("uploaded:", REMOTE, st.st_size, "bytes")

with sftp.open(REMOTE, 'rb') as f:
    data = f.read()
rh = hashlib.md5()
rh.update(data)
print("remote md5:", rh.hexdigest())
print("MATCH" if rh.hexdigest() == h.hexdigest() else "MISMATCH!!")
sftp.close(); t.close()
