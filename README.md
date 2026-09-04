# WorldGuard Complater

**WorldGuard, but completer.** A quality-of-life layer on top of [WorldGuard](https://enginehub.org/worldguard):
full tab-complete everywhere, a region management GUI, paginated help, one-command land claiming,
and optional economy support — with zero changes to how WorldGuard itself protects your world.

> Author: **Vortex_Miner1** · Brand: **VortexM** · License: **MIT**

## Features

- **Tab-complete for everything**
  - Subcommands filtered by permission
  - Region names (your own regions only, unless admin)
  - All WorldGuard flag names with descriptions
  - Smart flag values: `allow/deny/none` for state flags, gamemodes, weather, booleans
  - Online player names for member/owner management
- **GUI** (`/wgc`)
  - Paginated region list (45 per page)
  - Per-region panel: flags, members, info, delete
  - One-click state-flag cycling: `unset → allow → deny → unset`
  - Skull-based member/owner add & remove
  - Delete confirmation screen
- **Claiming** (`/wgc claim [name] [radius]`)
  - Auto region naming (`claim<Player>_<n>`)
  - Per-player limits via `wgc.limit.<n>` permissions
  - Min volume, max radius, inside-region and overlap checks
  - Optional Vault economy cost + delete refund
- **Info & listing** (`/wgc info`, `/wgc list`)
  - Flag values, owners, members, volume, bounds
  - Paginated list; admins see all, players see their own
- **Flag command** (`/wgc flag <region> <flag> [value]`)
  - No value = unset the flag
  - Full WorldGuard flag parsing (strings, ints, sets, locations…)
- **Members/owners** (`/wgc addmember|removemember|addowner|removeowner`)
- **Help** (`/wgc help [page]`) — permission-aware, paginated
- **Bilingual messages**: English + Farsi (romanized), switch with `language: fa` in config
- **Safe integration**: uses the official WorldGuard API only; WorldGuard stays fully in charge of protection

## Requirements

| Dependency | Version |
|---|---|
| Paper / Spigot | 1.20.4+ (API `1.20`) |
| Java | 17+ |
| [WorldGuard](https://enginehub.org/worldguard) | 7.0.9+ (required) |
| WorldEdit | 7.2+ (comes with WorldGuard) |
| Vault | any (optional, for claim costs) |

## Installation

1. Put `WorldGuard` (and `WorldEdit`) into `plugins/`
2. Put `WorldGuardComplater-1.0.0.jar` into `plugins/`
3. Optionally install `Vault` + an economy plugin if you want claim costs
4. Restart the server

## Commands

| Command | Description | Permission |
|---|---|---|
| `/wgc` | Open the regions GUI | `wgc.use` |
| `/wgc help [page]` | Paginated help | `wgc.help` |
| `/wgc gui` | Open the regions GUI | `wgc.use` |
| `/wgc claim [name] [radius]` | Claim the land around you | `wgc.claim` |
| `/wgc list [page]` | List regions | `wgc.list` |
| `/wgc info <region>` | Region details | `wgc.info` |
| `/wgc flag <region> <flag> [value]` | Set / unset a flag | `wgc.flag.own` |
| `/wgc addmember <region> <player>` | Add a member | `wgc.member.own` |
| `/wgc removemember <region> <player>` | Remove a member | `wgc.member.own` |
| `/wgc addowner <region> <player>` | Add an owner | `wgc.member.own` |
| `/wgc removeowner <region> <player>` | Remove an owner | `wgc.member.own` |
| `/wgc delete <region> [confirm]` | Delete a region | `wgc.delete.own` |
| `/wgc reload` | Reload the config | `wgc.admin` |

Alias: `/worldguardcomplater`

## Permissions

See [PERMISSIONS.md](PERMISSIONS.md) for the full table with defaults.

## Configuration

`plugins/WorldGuardComplater/config.yml`:

```yaml
language: en            # or: fa
claim:
  prefix: "claim"
  auto-name: true
  default-limit: 3
  default-radius: 16
  max-radius: 48
  min-volume: 64
  height: -1            # -1 = full world height
  cost: 0.0             # requires Vault
  refund: 0.0
economy:
  enabled: true
gui:
  enabled: true
prefix: "&8[&3WGC&8] &7"
```

Messages live in `plugins/WorldGuardComplater/i18n/en.yml` and `fa.yml`.

## Building

```bash
mvn package
```

Output: `target/WorldGuardComplater-1.0.0.jar`

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md). Issues and PRs are welcome.

## License

[MIT](LICENSE) © Vortex_Miner1 (VortexM)
