# Permissions — WorldGuard Complater

| Node | Description | Default |
|---|---|---|
| `wgc.use` | Use `/wgc` (opens the GUI) | all players |
| `wgc.help` | `/wgc help` | all players |
| `wgc.info` | `/wgc info` | all players |
| `wgc.claim` | `/wgc claim` | all players |
| `wgc.list` | `/wgc list` | all players |
| `wgc.flag.own` | Set flags on **own** regions | all players |
| `wgc.member.own` | Manage members/owners of **own** regions | all players |
| `wgc.delete.own` | Delete **own** regions (with confirm) | all players |
| `wgc.limit.<n>` | Max number of claims (`wgc.limit.10` = 10 claims) | none |
| `wgc.limit.bypass` | Ignore claim limits | op |
| `wgc.free` | Skip claim cost (reserved; cost currently applies to everyone) | op |
| `wgc.flag.others` | Set flags on **any** region | op |
| `wgc.member.others` | Manage members/owners of **any** region | op |
| `wgc.delete.others` | Delete **any** region (no confirm) | op |
| `wgc.admin` | `/wgc reload`, full GUI access, sees all regions in list | op |

## Notes

- Ownership is checked against the region's **owner** domain (owner or member lists via UUID or name).
- `wgc.flag.own` etc. require the player to be an **owner** of the region, not just a member.
- Claim limit: the **highest** `wgc.limit.<n>` a player has wins; if none, `claim.default-limit`
  from `config.yml` applies.
- Admins (`wgc.admin`) also see all regions in `/wgc list` and in tab-complete.
