# -*- coding: utf-8 -*-
# SFTP: exec channel fails? Try sftp subsystem + shell fallback
import paramiko, time

ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
ssh.connect('node-3-th.berno.app', port=2022, username='u_09339413432.119e34bc',
            password='hGWahiygeTwoDvC8', timeout=25, look_for_keys=False, allow_agent=False)
print("CONNECTED")

# Try exec again with retry & banner
for attempt in range(3):
    try:
        _, so, se = ssh.exec_command('echo EXEC-OK; pwd; ls -la | head -40', timeout=20)
        print(so.read().decode(errors='replace'))
        print(se.read().decode(errors='replace'))
        break
    except Exception as e:
        print(f"exec attempt {attempt+1} failed: {e}")
        time.sleep(2)

ssh.close()
