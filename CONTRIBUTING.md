# Contributing to WorldGuard Complater

Thanks for your interest in contributing! This project is open source under the MIT license.

## Getting started

```bash
git clone https://github.com/VortexM1/WorldGuardComplater
cd WorldGuardComplater
mvn package
```

Requirements: Java 17+, Maven 3.8+. The build downloads Paper API, WorldGuard API and VaultAPI
from their repositories automatically.

## Ground rules

1. **WorldGuard API only.** Do not use NMS, reflection into WG internals, or hardcoded
   protection logic. WorldGuard must stay the single source of truth for protection.
2. **Keep the curated flag catalog in sync with the real registry.** `FlagCatalog.available()`
   filters against WorldGuard's registry at runtime, so unknown entries are harmless — but
   descriptions should stay accurate.
3. **Both languages.** Any user-facing message needs a key in `i18n/en.yml` AND `i18n/fa.yml`.
4. **No breaking config changes** without a migration note in the README and a safe default.
5. **Tab-complete is a feature, not a nicety.** Any new subcommand or argument must complete.

## Pull requests

- One feature or fix per PR
- Describe what the player/admin experiences, not just what the code does
- Confirm `mvn package` passes locally
- Small PRs win: if a refactor is needed, propose it in an issue first

## Reporting bugs

Include:
- Server type + version (Paper/Spigot, e.g. 1.20.4)
- WorldGuard + WorldEdit versions
- WorldGuardComplater version
- The exact command you ran and the full output
- Any console errors (pastebin, not screenshots of text)

## Versioning

`MAJOR.MINOR.PATCH` — MAJOR for API/config breaks, MINOR for features, PATCH for fixes.
