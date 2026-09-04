# -*- coding: utf-8 -*-
# exec channel closed but SSH auth OK -> server may be restricted to SFTP subsystem only.
# Use the SFTP subsystem to list directories and read files.
import paramiko

t = paramiko.Transport(('node-3-th.berno.app', 2022))
t.connect(username='u_09339413432.119e34bc', password='hGWahiygeTwoDvC8')
sftp = paramiko.SFTPClient.from_transport(t)
print("SFTP OPENED")

def ls(path):
    print(f"--- {path} ---")
    try:
        for f in sftp.listdir_attr(path):
            kind = 'd' if (f.st_mode and (f.st_mode & 0o40000)) else '-'
            print(f"{kind} {f.filename}")
    except Exception as e:
        print("ERR:", e)

ls('.')
ls('..')
