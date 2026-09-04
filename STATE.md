# WORLDGUARD COMPLATER — STATE (update 23:05, 2026-09-04)

## STATUS: TEST E KAMEL PASS SHOD — 14/14 + TAB-COMPLETE OK

## DONE
- Plugin build OK: target/WorldGuardComplater-1.0.0.jar (md5 db6b9206f59b16052889c6dbda626835)
- Server test: Paper 1.20.4 + WG 7.0.9-dist + WE 7.3.0 → plugin ENABLE OK
- **E2E test ba mineflayer (TestBot): 14/14 PASS**
  (help, claim, list, info, flag set/string/unset/badvalue/unknown, addmember, removemember, delete-confirm, delete-real, list-empty)
- **Tab-complete test (mineflayer tabComplete): OK**
  (/wgc → 11 subcommand; /wgc flag <r> p → passthrough/pistons/potion-splash/pvp; flag value → allow/deny/none; removemember → online players; region names list shodan)
- Git commit aval: 470e60d "WorldGuard Complater 1.0.0 - full test suite 14/14 PASS" (23 files, 2244 lines)
- Console PTY kar mikone (fill/tp/kill/rg delete bekhater console commands)
- Mob ha kill shodan + doMobSpawning false

## KEY LESSONS (test infra)
- mineflayer: bot.chat('/cmd') BAYAD slash dashte bashe baraye command (bedune slash = chat)
- bot.physicsEnabled = false → dige invalid_player_movement kick nemigire
- beine command ha 400ms sleep → disconnect.spam nemigire
- deop TestBot → baraye test e confirm flow (admin bedune confirm delete mikone)
- 2 ta "tab_complete timeout" tu ye session = mineflayer quirk bood, na bug e plugin (isolated session OK shod)

## REMAINING
1. **GUI test** — faghat dasti/tavasot user mishe (mineflayer window click nist) → user bayad too bazi /wgc gui bezane
2. SFTP upload be server asli (node-3-th.berno.app:2022) + check WorldGuard ro server asli
3. GitHub push (age gh CLI login bashe) — hala repo local commit shode
4. OPTIONAL: /wgc tp, economy claim cost test (Vault nist ro testserver)

## DEPLOY
- SFTP: node-3-th.berno.app:2022 u_09339413432.119e34bc / hGWahiygeTwoDvC8 — Paper 1.20.4-499
- Check kon WorldGuard oonjast; nabud → begu
- JAR: C:/Users/ALI/worldguard-complater/target/WorldGuardComplater-1.0.0.jar

## TEST ARTIFACTS
- wgc_test.js (14 step), wgc_tab.js (tab probe), tab_iso.js — C:/Users/ALI/vortexlink-build/bot/
- natije: bot_wgc_results.txt
- Server log: C:/Users/ALI/vortexlink-build/testserver/wgc_test_console.log
- Server console PTY: proc_38d4274c8bce (testserver hanooz RUNNING)
