# WORLDGUARD COMPLATER — STATE (next session: edame azinja)

## DONE
- Plugin COMPILE+BUILD OK: target/WorldGuardComplater-1.0.0.jar (md5 db6b9206f59b16052889c6dbda626835, 51,514B)
- Source: src/main/java/com/vortexm/wgc/ (WorldGuardComplater, command/WgcCommand, gui/{GuiManager,GuiListener,Menus}, claim/Claims, util/{WgBridge,FlagCatalog,Lang,VaultHook,Text})
- Resources: plugin.yml, config.yml, i18n/en.yml + fa.yml
- Docs: README.md, LICENSE (MIT), CONTRIBUTING.md, PERMISSIONS.md, .gitignore
- Git init shode (main, commit NA)
- SERVER TEST: Paper 1.20.4 (C:/Users/ALI/Downloads/paper-1.20.4-499-mojang.jar) + WG 7.0.9-dist + WE 7.3.0 (har do az Modrinth; WE 7.3.9 Java21 mikhad — Java 17 bayad 7.3.0 bashe)
- Plugin ENABLE shod: "WorldGuard Complater 1.0.0 enabled. VortexM!" (console: wgc help OK, wgc list OK, claim/info=player-only az console (tscopic))
- mvn cmd: cd C:/Users/ALI/worldguard-complater && JAVA_HOME=C:\Users\ALI\vortexlink-build\tools\jdk-17.0.20.1+1 mvn.cmd package (mvn.cmd copy shode tu folder)

## IN-PROGRESS (bug e shenakhte)
- Mineflayer bot (TestBot) minevaze: "invalid_player_movement" ~5-9s bad az join (mineflayer 4.20 + Paper 1.20.4, x/z=null mishe badaz ye zamani)
- Pos probe (pos_probe.js): ta 10s stable bood, wgc help raft — ba adame rcon.
- Test e bot: C:/Users/ALI/vortexlink-build/bot/wgc_test.js (8 step: help/claim/list/info/flag x5/member x2/delete x3)
- Ravesh: rcon AZ TEST HA PAK SHOD. Platform + op + tp az tarighe PTY console (proc_0a5d1caaabd7): fill 10 -60 10 20 -60 20 stone; tp TestBot 15.5 -58.5 15.5 (ops.json TestBot op = 30fecbe1-2271-3418-8553-d3ded0e95f56)
- RCON PORT 25575 KAR NEMIKONE (accepts TCP, no response, hattâ ba password ghalat) — RCON BEKAR BE.

## REMAINING
1. Fix bot kick (yek rah: bot.entity.position.set + physics disable plugin mineflayer 'blocky-physics'?; yeye rah: test console-only be PTY + bedune player baraye hamin command hai ke player mikhad GUI/claim/info claim rooye console nistan — claim/info az console player-only hast)
2. Baad az fix: wgc_test.js run → 8 step result → fix bug ha (agar bud)
3. GUI rooye server (khodet dasti test mikoni)
4. git add+commit
5. Upload be server asli (SFTP node-3-th.berno.app:2022) + check WorldGuard ro server asli
6. Version 1.1: /wgc tp, settle claim, etc (OPTIONAL)

## DEPLOY (khodam midunam)
- SFTP: node-3-th.berno.app:2022 u_09339413432.119e34bc / hGWahiygeTwoDvC8 — server asli Paper 1.20.4-499
- Check kon WorldGuard oonjast; nabud → begu
