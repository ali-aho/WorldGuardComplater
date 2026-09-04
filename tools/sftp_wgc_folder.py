# -*- coding: utf-8 -*-
# Check WGC plugin folder + WG sessions summary (claims created/deleted)
import paramiko

t = paramiko.Transport(('node-3-th.berno.app', 2022))
t.connect(username='u_09339413432.119e34bc', password='hGWahiygeTwoDvC8')
sftp = paramiko.SFTPClient.from_transport(t)

def ls(path):
    try:
        return [(f.filename, f.st_size) for f in sftp.listdir_attr(path)]
    except Exception as e:
        print("ERR ls", path, e)
        return []

print("=== plugins/WorldGuardComplater/ ===")
for n, s in ls('plugins/WorldGuardComplater'):
    print(f"{n} ({s}B)")
print("--- i18n ---")
for n, s in ls('plugins/WorldGuardComplater/i18n'):
    print(f"{n} ({s}B)")

with sftp.open('logs/latest.log', 'r') as f:
    log = f.read().decode(errors='replace')
import re
cmds = re.findall(r'issued server command: (.*)', log)
wgc = [c for c in cmds if c.strip().startswith(('/wgc', '/rg'))]
print(f"\ntotal /wgc + /rg commands this session: {len(wgc)}")
saves = log.count('background saved')
print(f"WG region background saves: {saves}")
# last check: did /rg commands error after wgc? Look for lines after last /rg
last_rg = log.rfind('issued server command: /rg')
print("\ncontext around last /rg:")
print(log[max(0,last_rg-300):last_rg+400])
