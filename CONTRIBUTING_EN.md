# Contributing to WorldGuard Complater

<div align="center">

🌍 **Language selection / انتخاب زبان**

[🇮🇷 فارسی](./CONTRIBUTING.md) &nbsp;·&nbsp; [🇬🇧 English](./CONTRIBUTING_EN.md)

</div>

---

Thanks for your interest in contributing! This project is released under the MIT license.

## Getting started

```bash
git clone https://github.com/ali-aho/WorldGuardComplater
cd WorldGuardComplater
mvn package
```

Requirements: Java 17+ and Maven 3.8+. The build pulls the Paper, WorldGuard and VaultAPI APIs automatically from Maven repositories.

## Ground rules

1. **Official WorldGuard API only.** No NMS or WorldGuard internal classes. WorldGuard must stay the single source of truth for protection.
2. **Keep the flag catalog in sync with the real registry.** `FlagCatalog` output is filtered at runtime against the WorldGuard registry, so unknown entries are harmless — but descriptions must stay accurate.
3. **Both languages.** Every message shown to a player must have a key in both `i18n/en.yml` and `i18n/fa.yml`.
4. **No breaking config changes** without a migration note in the README and a safe default.
5. **Tab-complete is a feature, not a luxury.** Every new subcommand or argument needs tab-complete.

## Pull requests

- One feature or one bugfix per PR
- Explain the player/admin experience, not just the code
- Make sure `mvn package` passes locally
- Small PRs get reviewed faster; for large refactors, propose it in an issue first

## Bug reports

Include:

- Server type and version (Paper/Spigot, e.g. 1.20.4)
- WorldGuard + WorldEdit versions
- WorldGuardComplater version
- The exact command run and its full output
- Console errors (text, not screenshots)

## Versioning

`MAJOR.MINOR.PATCH` — MAJOR for breaking API/config changes, MINOR for new features, PATCH for bugfixes.
