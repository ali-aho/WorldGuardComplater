# WORLDGUARD COMPLATER — STATE (update 01:35, 2026-09-05)

## STATUS: v1.1.0 BUILD OK — E2E 19/25 PASS, 3 BUG FIX SHOD, define test pending

## KOJA BUDIM (farda edame az inja)

### Kar e in shab (v1.1.0 — gostaresh e addon e kamel)
- Tahghigh ba browser (sare'): 21 command /rg + 99 flag + permission model + claim rules
- Command-haye jadid: define, redefine, select, setpriority, setparent, teleport,
  setspawn, guide (ketabkhune darun-bazi 4 bakhsh) + alias-ha
- ChatInput system: baraye flag-haye matni az GUI (type tu chat, "cancel" = laghv)
- GUI gostaresh: Teleport, SetSpawn, Priority +/-, Parent-pick menu, Redefine (shift-click)
- i18n: ~132 key jadid (en.yml + fa.yml Finglish) — Lang.java alan MERGE mikone (bug fix)
- pom + plugin.yml version 1.1.0

### Bug-haye peyda va FIX shode (v1.1.0)
1. i18n key-haye jadid load nemishodan (file ru-disk az 1.0.0 mande bud) → Lang.java
   alan be on update MERGE mikone (customization nemire) — TA'ID SHOD tu test
2. delete NPE (region namoojood → internal error) → null-check ghabl az isOwner — FIX
3. teleport be markaz = y+1 (1 block BALAYE region!) → setspawn "outside" migoft →
   WgBridge.center() alan y=max.getY() (feet dakhele top block) — TA'ID SHOD (8a PASS)
4. allow-flight=true tu server.properties e testserver (flying kick ba'd az teleport)

### Bug e baz-mande (diagnos shod, FIX nashod hanooz)
- define: ba //pos1 //pos2 (do slash, op) probe JAVAB DAD "First position set to (1,307,-3)"
  vali /wgc define hanooz goft "no selection" → IncompleteRegionException az
  WgBridge.cuboidFromSelection — YA'NI session i ke WE pos1 set mikone ba session i ke
  WGC we.getSession(p) migire YEKI NIST. Farda: check kon WE 7.3 ar chandta session dare
  (multi-session per world?) ya getSelection world match nemikone (session dar dunya ye
  world-e, WGC adapt() mizane dige world-e) → fallback: session.getAllRegions? ya
  session.setSelection? ya use session.getSelection(session.getSelectionWorld())
- E2E v3 natije: 19/25 PASS — 6 FAIL hame az hamin yek bug e define mian (5a,5b,6a,6b,6c,11)
  + 7b,13c (expected FAIL ha — region nabood)

### Ops/Test infra note
- Test e akhar /deop TestBot mikone → ops.json khali mishe → run e BA'D bayad dasti op she
  az console (proc_411c97fc37b8: `op TestBot`)
- Test v3: C:/Users/ALI/vortexlink-build/bot/wgc_test2.js (windowOpen detection baraye guide)

## DEPLOY STATUS
- Testserver: jar 1.1.0 (md5 3bb5606326ea90f918835b1f457f042a) LOAD shod, enable OK
- Server e asli: HANOOZ 1.0.0-fix1 (jar e ghadim) — 1.1.0 upload nashod (farda ba'd az
  hal e define + E2E kamel)
- SFTP: node-3-th.berno.app:2022 u_09339413432.119e34bc / hGWahiygeTwoDvC8

## GIT
- Commit 3372bb0: source e v1.1.0 (kamel, build OK) — SAFE
- Farda: commit e bug-fix e Lang/center + WgBridge + STATE.md

## FARDA (be tarib)
1. Hal e define/IncompleteRegionException (session mismatch) → test 5a/6a/11 PASS she
2. E2E v4 kamel (25 test, ba op e persistent)
3. Git commit + push (age gh CLI)
4. Upload 1.1.0 be server asli + to restart mikoni + log check
5. GUI dasti ro to (mineflayer click nadare)
