# -*- coding: utf-8 -*-
# Search latest.log for WGC errors + get full WGC session history
import paramiko, re

t = paramiko.Transport(('node-3-th.berno.app', 2022))
t.connect(username='u_09339413432.119e34bc', password='hGWahiygeTwoDvC8')
sftp = paramiko.SFTPClient.from_transport(t)

with sftp.open('logs/latest.log', 'r') as f:
    log = f.read().decode(errors='replace')

lines = log.splitlines()
print(f"total lines: {len(lines)}")

print("\n=== WGC / WorldGuardComplater mentions ===")
for l in lines:
    if 'WorldGuardComplater' in l or 'WGC' in l or 'wgc' in l.lower() and 'issued' not in l:
        print(l)

print("\n=== SEVERE/ERROR/WARN (excluding known noisy plugins) ===")
noise = ('LiteBans', 'NBTAPI', 'zMenu', 'voicechat', 'VehiclesPlusPro', 'bStats', 'VortexLink', 'update', 'Update')
for l in lines:
    if ('SEVERE' in l or 'ERROR' in l) and not any(n in l for n in noise):
        print(l)
