
import paramiko, hashlib

HOST, PORT = "node-3-th.berno.app", 2022
USER = "u_09339413432.119e34bc"
PW = "hGWahiygeTwoDvC8"
LOCAL = r"C:\Users\ALI\worldguard-complater\target\WorldGuardComplater-1.1.0.jar"
REMOTE = "plugins/WorldGuardComplater-1.1.0.jar"

t = paramiko.Transport((HOST, PORT))
t.connect(username=USER, password=PW)
sftp = paramiko.SFTPClient.from_transport(t)

# remove old versions on server
try:
    for f in sftp.listdir("plugins"):
        if f.startswith("WorldGuardComplater-") and f != "WorldGuardComplater-1.1.0.jar":
            sftp.remove("plugins/" + f)
            print("removed old:", f)
except FileNotFoundError:
    pass

sftp.put(LOCAL, REMOTE)
st = sftp.stat(REMOTE)
print("uploaded:", REMOTE, st.st_size, "bytes")

h = hashlib.md5(open(LOCAL, "rb").read()).hexdigest()
print("local md5:", h)
sftp.close(); t.close()
