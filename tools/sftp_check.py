# -*- coding: utf-8 -*-
# SFTP connect to real server, find MC server dir, read logs, check plugins
import paramiko, sys

ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
ssh.connect('node-3-th.berno.app', port=2022, username='u_09339413432.119e34bc',
            password='hGWahiygeTwoDvC8', timeout=25, look_for_keys=False, allow_agent=False)

def run(cmd, t=30):
    _, so, se = ssh.exec_command(cmd, timeout=t)
    out = so.read().decode(errors='replace')
    err = se.read().decode(errors='replace')
    return out + err

print("=== HOME ===")
print(run('pwd; ls -la | head -40'))

print("=== FIND SERVER ===")
print(run('ls ~ ; find / -maxdepth 4 -name "server.properties" 2>/dev/null | head -10'))

ssh.close()
print("DONE")
