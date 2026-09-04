# -*- coding: utf-8 -*-
# Read latest.log tail + plugins list on real server via SFTP
import paramiko

t = paramiko.Transport(('node-3-th.berno.app', 2022))
t.connect(username='u_09339413432.119e34bc', password='hGWahiygeTwoDvC8')
sftp = paramiko.SFTPClient.from_transport(t)

def ls(path):
    try:
        return [(f.filename, f.st_size, bool(f.st_mode and (f.st_mode & 0o40000))) for f in sftp.listdir_attr(path)]
    except Exception as e:
        print("ERR ls", path, e)
        return []

print("=== plugins/ ===")
for name, size, isdir in ls('plugins'):
    print(('d ' if isdir else '- ') + f"{name} ({size}B)")

print()
print("=== logs/ (newest 8) ===")
logs = sorted(ls('logs'), key=lambda x: x[0])[-8:]
for name, size, isdir in logs:
    print(f"{name} ({size}B)")

# read tail of latest.log
try:
    with sftp.open('logs/latest.log', 'r') as f:
        f.seek(max(0, f.stat().st_size - 6000))
        tail = f.read().decode(errors='replace')
    print()
    print("=== latest.log tail ===")
    print(tail)
except Exception as e:
    print("ERR read latest.log:", e)
