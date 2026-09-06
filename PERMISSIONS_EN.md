# Permissions — WorldGuard Complater

<div align="center">

🌍 **Language selection / انتخاب زبان**

[🇮🇷 فارسی](./PERMISSIONS.md) &nbsp;·&nbsp; [🇬🇧 English](./PERMISSIONS_EN.md)

</div>

---

| Permission | Description | Default |
|------------|-------------|---------|
| `wgc.use` | Use `/wgc` and open the GUI | everyone |
| `wgc.help` | `/wgc help` | everyone |
| `wgc.info` | `/wgc info` | everyone |
| `wgc.claim` | `/wgc claim` | everyone |
| `wgc.list` | `/wgc list` | everyone |
| `wgc.guide` | `/wgc guide` | everyone |
| `wgc.select` | `/wgc select` | everyone |
| `wgc.teleport` | `/wgc teleport` | everyone |
| `wgc.flag.own` | Set flags on **your own** regions | everyone |
| `wgc.member.own` | Manage members/owners of **your own** regions | everyone |
| `wgc.delete.own` | Delete **your own** regions (with confirm) | everyone |
| `wgc.priority.own` | Change priority of **your own** regions | everyone |
| `wgc.parent.own` | Set parent of **your own** regions | everyone |
| `wgc.redefine.own` | Redefine **your own** regions | everyone |
| `wgc.setspawn.own` | Set spawn of **your own** regions | everyone |
| `wgc.limit.<n>` | Max number of claims (`wgc.limit.10` = 10 claims) | none |
| `wgc.limit.bypass` | Ignore claim limits | operator |
| `wgc.free` | Skip claim cost | operator |
| `wgc.flag.others` | Set flags on **any** region | operator |
| `wgc.member.others` | Manage members/owners of **any** region | operator |
| `wgc.delete.others` | Delete **any** region (no confirm) | operator |
| `wgc.priority.others` | Change priority of **any** region | operator |
| `wgc.parent.others` | Set parent of **any** region | operator |
| `wgc.redefine.others` | Redefine **any** region | operator |
| `wgc.setspawn.others` | Set spawn of **any** region | operator |
| `wgc.admin` | `/wgc reload`, see all regions in list & GUI, full access | operator |

## Notes

- Ownership is checked against the region's **owners** list (UUID or name).
- `wgc.flag.own` and similar require the player to be the region **owner**, not just a member.
- Claim limits: the **highest** `wgc.limit.<n>` the player has wins; otherwise `claim.default-limit` from `config.yml` applies.
- Operators (`wgc.admin`) see all regions in `/wgc list` and the GUI; their deletions skip confirmation.
