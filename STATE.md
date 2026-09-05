# WORLDGUARD COMPLATER — STATE (update 14:05, 2026-09-05)

## STATUS: v1.1.0 COMPLETE — E2E 26/26 PASS + confirm-path — UPLOADED to real server (restart pending by user)

## FARDA (be tarib)
1. User restarts real server → WGC 1.1.0 load shod? (log check ba SFTP)
2. GUI dasti ro user (mineflayer click nadare): /wgc gui → parent pick, priority, redefine, guide
3. GitHub push (age gh CLI login bashe)

## E2E FINAL (testserver, Paper 1.20.4 + WG 7.0.9 + WE 7.3.0)
26/26 PASS: help, guide(GUI), claim, info(+priority/parent lines), setpriority(+bad),
define(az //pos1 //pos2 selection), define-dup, setparent, parent-info, parent-clear,
teleport, teleport-bad, setspawn, teleport-spawn, flag set/string/unset, select,
redefine, addmember, removemember, delete-admin, delete-child, list-empty.
Confirm path (non-op bot): claim -> delete (whsafar confirm) -> delete confirm = deleted.

## YADGARI-HA
- op/deop roye bot e CONNECTED = Paper disconnect.spam → dar test deop NAKON; confirm-path
  ro ba bot e dovom (DeleteBot, non-op) test kon
- WE 7.3: faghat //pos1 (do slash) - /pos1 yek-slash Unknown command
- i18n: Lang.java be on update MERGE mikone (customization nemire)
- ops.json test e ghabl pak mikone age bot deop she → console: op TestBot
- Jar e nahaee: md5 7904f7016df4e90f156ba12f6fc01e40 (71994B) - local=testserver=real server

## GIT
- 9b04612 (akharin): E2E v4 26/26 + info priority/parent
- 92853e6: i18n merge + delete NPE + center fix
- 3372bb0: v1.1.0 source
