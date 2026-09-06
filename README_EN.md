# WorldGuard Complater

**WorldGuard, but completer.** A quality-of-life addon for [WorldGuard](https://enginehub.org/worldguard) that makes region management easier with a GUI, tab-complete, an in-game guide, and more.

> Author: **Vortex_Miner1** · Brand: **VortexM** · License: **MIT**

🌍 **نسخه فارسی / Farsi version:** [README.md](README.md)

---

## 🐞 Bug reports & ⭐ Support

Found a bug or have a suggestion? Please open an [Issue](../../issues/new) — bug reports get quick attention.
If this plugin is useful to you, give the repo a ⭐ **Star** to support its development!

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for the contribution guide and the contributor agreement.

---

## ✨ Features

- 🖥️ **Full GUI** – Region list, flags, members, priority, parent, teleport, redefine
- ⌨️ **Tab-complete** – Everything is suggested (regions, flags, values, players)
- 📖 **In-game guide** – `/wgc guide` with sections for commands, flags, claims and permissions
- 🏡 **Claiming** – `/wgc claim` with auto-naming, limits and Vault economy
- 🛡️ **All WorldGuard commands** – define, redefine, select, setpriority, setparent, teleport, setspawn, flag, member, delete, info, list
- 🌍 **Bilingual** – English + Farsi, switch with `language: fa`
- 🔒 **Safe** – Uses the official WorldGuard API only, no protection bypass
- 💬 **Chat input from the GUI** – Text flags (like greeting) are typed straight into chat without closing the menu

---

## 📋 Requirements

| Dependency | Version |
|------------|---------|
| Paper / Spigot | 1.20.4+ |
| Java | 17+ |
| WorldGuard | 7.0.9+ |
| WorldEdit | 7.2+ |
| Vault | Optional (for claim costs) |

---

## 📦 Installation

1. Download `WorldGuardComplater-1.1.0.jar`
2. Put it in your server's `plugins/` folder
3. Make sure WorldGuard + WorldEdit are installed
4. Restart the server
5. Done! Use `/wgc` or `/wgc guide` to get started

> Note: Language files are generated automatically in `plugins/WorldGuardComplater/i18n/` and can be customized. Updating the plugin preserves your edits and only merges new keys.

---

## 📖 Commands

| Command | Description |
|---------|-------------|
| `/wgc help [page]` | Show help |
| `/wgc guide` | Open the in-game guide |
| `/wgc gui` | Open the GUI |
| `/wgc claim [name] [radius]` | Claim land around you |
| `/wgc define <id>` | Create a region from a WorldEdit selection (`//wand`, `//pos1`, `//pos2`) |
| `/wgc redefine <region>` | Change a region's area with a new WorldEdit selection (flags are kept) |
| `/wgc select <region>` | Make the region your WorldEdit selection |
| `/wgc flag <region> <flag> [value]` | Set/unset a flag (no value = unset) |
| `/wgc setpriority <region> <value>` | Set region priority |
| `/wgc setparent <region> [parent]` | Set a parent (no parent = clear inheritance) |
| `/wgc teleport <region> [spawn]` | Teleport to the region center or its spawn |
| `/wgc setspawn <region>` | Set the region spawn where you stand |
| `/wgc addmember <region> <player>` | Add a member |
| `/wgc removemember <region> <player>` | Remove a member |
| `/wgc addowner <region> <player>` | Add an owner |
| `/wgc removeowner <region> <player>` | Remove an owner |
| `/wgc delete <region> [confirm]` | Delete a region (owners must type confirm) |
| `/wgc list [page]` | List regions |
| `/wgc info [region]` | Region details (no argument = the region you stand in) |
| `/wgc reload` | Reload the configuration |

---

## 🔐 Permissions

See [PERMISSIONS.md](PERMISSIONS.md) for the full list.

---

## 🌐 Language

In `config.yml`:

```yaml
language: "fa"  # en / fa
```

---

## 📄 License

MIT License – see [LICENSE](LICENSE)

---

## 🤝 Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md)

---

**Made with ❤️ by VortexM**
