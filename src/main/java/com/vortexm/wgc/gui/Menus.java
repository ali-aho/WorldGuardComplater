package com.vortexm.wgc.gui;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.vortexm.wgc.WorldGuardComplater;
import com.vortexm.wgc.util.FlagCatalog;
import com.vortexm.wgc.util.Lang;
import com.vortexm.wgc.util.Text;
import com.vortexm.wgc.util.WgBridge;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * All menus: region list (45/page like VortexModel), region panel,
 * flag toggles (state flags clickable), members management, delete confirm.
 */
public final class Menus {

    // slot layout for paginated list menus (6 rows)
    private static final int LIST_SIZE = 54;
    private static final int PREV_SLOT = 45;
    private static final int NEXT_SLOT = 53;
    private static final int CLOSE_SLOT = 49;

    private final WorldGuardComplater plugin;

    public Menus(WorldGuardComplater plugin) {
        this.plugin = plugin;
    }

    /* ---------------- regions list ---------------- */

    public void openRegions(Player p, int page) {
        List<ProtectedRegion> regions = WgBridge.regionsOwnedBy(p.getWorld(), p.getUniqueId(), p.getName());
        regions.sort(Comparator.comparing(ProtectedRegion::getId));

        int perPage = 45;
        int pages = Math.max(1, (int) Math.ceil(regions.size() / (double) perPage));
        if (page < 1) page = 1;
        if (page > pages) page = pages;

        Lang lang = plugin.lang();
        String title = lang.fmt("regions-menu-title");
        Inventory inv = Bukkit.createInventory(null, LIST_SIZE, title);

        int start = (page - 1) * perPage;
        for (int i = 0; i < perPage && start + i < regions.size(); i++) {
            ProtectedRegion r = regions.get(start + i);
            List<String> lore = new ArrayList<>();
            lore.add("&7world: &f" + p.getWorld().getName());
            lore.add("&7volume: &f" + WgBridge.volume(r));
            lore.add("&7bounds: &f" + WgBridge.boundsString(r));
            lore.add("");
            lore.add("&bClick &7to manage");
            inv.setItem(i, Text.item(Material.GRASS_BLOCK, "&b&l" + r.getId(), lore));
        }

        // nav bar
        inv.setItem(PREV_SLOT - 1, Text.item(Material.ARROW, "&7Previous page", null));
        inv.setItem(NEXT_SLOT - 1, Text.item(Material.ARROW, "&7Next page", null));
        inv.setItem(CLOSE_SLOT - 1, Text.item(Material.BARRIER, "&cClose", null));

        final int fPage = page;
        final int fPages = pages;
        plugin.gui().open(p, inv, (player, e) -> {
            int slot = e.getSlot();
            if (slot == PREV_SLOT - 1 && fPage > 1) {
                openRegions(player, fPage - 1);
            } else if (slot == NEXT_SLOT - 1 && fPage < fPages) {
                openRegions(player, fPage + 1);
            } else if (slot == CLOSE_SLOT - 1) {
                player.closeInventory();
            } else if (slot >= 0 && slot < 45 && start + slot < regions.size()) {
                openRegionPanel(player, regions.get(start + slot).getId());
            }
        });
    }

    /* ---------------- region panel ---------------- */

    public void openRegionPanel(Player p, String regionId) {
        var world = p.getWorld();
        ProtectedRegion r = WgBridge.region(world, regionId);
        if (r == null) {
            plugin.lang().send(p, "not-found", regionId);
            return;
        }
        Lang lang = plugin.lang();
        Inventory inv = Bukkit.createInventory(null, 27, lang.fmt("region-menu-title", r.getId()));

        inv.setItem(10, Text.item(Material.RED_BANNER, "&c&lFlags", List.of(
                "&7Toggle the common state flags", "", "&bClick &7to open")));
        inv.setItem(12, Text.item(Material.PLAYER_HEAD, "&e&lMembers", List.of(
                "&7Owners &7and members", "", "&bClick &7to open")));
        inv.setItem(14, Text.item(Material.LIME_WOOL, "&a&lInfo", List.of(
                "&7Show details in chat", "", "&bClick &7to run &f/wgc info " + r.getId())));
        inv.setItem(16, Text.item(Material.TNT, "&c&lDelete", List.of(
                "&7Delete this region", "", "&cClick &7to confirm")));

        plugin.gui().open(p, inv, (player, e) -> {
            int slot = e.getSlot();
            switch (slot) {
                case 10 -> openFlagMenu(player, r.getId(), 0);
                case 12 -> openMembersMenu(player, r.getId());
                case 14 -> {
                    player.closeInventory();
                    Bukkit.dispatchCommand(player, "wgc info " + r.getId());
                }
                case 16 -> openDeleteConfirm(player, r.getId());
                default -> {
                }
            }
        });
    }

    /* ---------------- flags menu (paginated, state flags clickable) ---------------- */

    public void openFlagMenu(Player p, String regionId, int page) {
        var world = p.getWorld();
        ProtectedRegion r = WgBridge.region(world, regionId);
        if (r == null) {
            plugin.lang().send(p, "not-found", regionId);
            return;
        }
        List<FlagCatalog.Entry> flags = FlagCatalog.available();
        flags.sort(Comparator.comparing(FlagCatalog.Entry::name));

        int perPage = 45;
        int pages = Math.max(1, (int) Math.ceil(flags.size() / (double) perPage));
        if (page < 1) page = 1;
        if (page > pages) page = pages;

        Inventory inv = Bukkit.createInventory(null, LIST_SIZE, plugin.lang().fmt("flag-menu-title", r.getId()));
        int start = (page - 1) * perPage;

        for (int i = 0; i < perPage && start + i < flags.size(); i++) {
            FlagCatalog.Entry entry = flags.get(start + i);
            Flag<?> f = com.sk89q.worldguard.WorldGuard.getInstance().getFlagRegistry().get(entry.name());
            Object current = f != null ? r.getFlag(f) : null;
            String cur = current == null ? "unset" : String.valueOf(current);

            Material mat;
            String color;
            if (current == null) {
                mat = Material.GRAY_DYE;
                color = "&7";
            } else if (current.equals(StateFlag.State.DENY)) {
                mat = Material.RED_DYE;
                color = "&c";
            } else if (current.equals(StateFlag.State.ALLOW)) {
                mat = Material.LIME_DYE;
                color = "&a";
            } else {
                mat = Material.BOOK;
                color = "&e";
            }

            List<String> lore = new ArrayList<>();
            lore.add(color + cur);
            lore.add("&8" + entry.type());
            if (entry.stateFlag()) {
                lore.add("");
                lore.add("&bClick &7to cycle: unset -> allow -> deny");
            } else {
                lore.add("&8Use &f/wgc flag " + r.getId() + " " + entry.name() + " <value>");
            }
            inv.setItem(i, Text.item(mat, "&f" + entry.name(), lore));
        }

        inv.setItem(PREV_SLOT, Text.item(Material.ARROW, "&7Previous page", null));
        inv.setItem(NEXT_SLOT, Text.item(Material.ARROW, "&7Next page", null));
        inv.setItem(CLOSE_SLOT, Text.item(Material.BARRIER, "&cBack", null));

        final int fPage = page;
        final int fPages = pages;
        plugin.gui().open(p, inv, (player, e) -> {
            int slot = e.getSlot();
            if (slot == PREV_SLOT && fPage > 1) {
                openFlagMenu(player, r.getId(), fPage - 1);
            } else if (slot == NEXT_SLOT && fPage < fPages) {
                openFlagMenu(player, r.getId(), fPage + 1);
            } else if (slot == CLOSE_SLOT) {
                openRegionPanel(player, r.getId());
            } else if (slot >= 0 && slot < 45 && start + slot < flags.size()) {
                FlagCatalog.Entry entry = flags.get(start + slot);
                if (!entry.stateFlag()) return;
                cycleStateFlag(player, r, entry.name());
                openFlagMenu(player, r.getId(), fPage); // refresh
            }
        });
    }

    /** unset -> allow -> deny -> unset */
    private void cycleStateFlag(Player p, ProtectedRegion r, String flagName) {
        // permission: admins may flag anything; players only their own regions
        boolean admin = p.hasPermission("wgc.admin") || p.hasPermission("wgc.flag.others");
        if (!admin && !WgBridge.isOwner(r, p.getUniqueId(), p.getName())) {
            plugin.lang().send(p, "flag-no-permission-region");
            return;
        }
        Flag<?> f = com.sk89q.worldguard.WorldGuard.getInstance().getFlagRegistry().get(flagName);
        if (!(f instanceof StateFlag sf)) return;
        StateFlag.State cur = r.getFlag(sf);
        StateFlag.State next;
        if (cur == null) next = StateFlag.State.ALLOW;
        else if (cur == StateFlag.State.ALLOW) next = StateFlag.State.DENY;
        else next = null;

        r.setFlag(sf, next);
        String key = next == null ? "flag-unset" : "flag-set";
        String val = next == null ? "unset" : next.name().toLowerCase();
        p.sendMessage(plugin.lang().prefix() + plugin.lang().fmt(key, flagName, val, r.getId()));
    }

    /* ---------------- members menu ---------------- */

    public void openMembersMenu(Player p, String regionId) {
        var world = p.getWorld();
        ProtectedRegion r = WgBridge.region(world, regionId);
        if (r == null) {
            plugin.lang().send(p, "not-found", regionId);
            return;
        }
        Inventory inv = Bukkit.createInventory(null, 27, plugin.lang().fmt("members-menu-title", r.getId()));

        DefaultDomain owners = r.getOwners();
        DefaultDomain members = r.getMembers();
        List<String> ownerLore = new ArrayList<>();
        ownerLore.add("&7" + (owners == null ? 0 : owners.size()) + " owner(s)");
        ownerLore.add("");
        ownerLore.add("&bClick &7to add online players as owner");
        inv.setItem(11, Text.item(Material.GOLDEN_AXE, "&6&lOwners", ownerLore));

        List<String> memberLore = new ArrayList<>();
        memberLore.add("&7" + (members == null ? 0 : members.size()) + " member(s)");
        memberLore.add("");
        memberLore.add("&bClick &7to add online players as member");
        inv.setItem(13, Text.item(Material.IRON_AXE, "&e&lMembers", memberLore));

        inv.setItem(15, Text.item(Material.BARRIER, "&cBack", null));

        plugin.gui().open(p, inv, (player, e) -> {
            switch (e.getSlot()) {
                case 11 -> openPlayerPick(player, r.getId(), true);
                case 13 -> openPlayerPick(player, r.getId(), false);
                case 15 -> openRegionPanel(player, r.getId());
                default -> {
                }
            }
        });
    }

    /** Pick an online player to add/remove as owner or member. */
    private void openPlayerPick(Player p, String regionId, boolean owner) {
        ProtectedRegion r = WgBridge.region(p.getWorld(), regionId);
        if (r == null) return;
        List<Player> online = new ArrayList<>(Bukkit.getOnlinePlayers());
        online.sort(Comparator.comparing(Player::getName));

        int size = 54;
        Inventory inv = Bukkit.createInventory(null, size,
                plugin.lang().fmt("members-menu-title", r.getId()));

        int slot = 0;
        for (Player t : online) {
            if (slot >= 45) break;
            boolean inOwners = r.getOwners() != null && r.getOwners().contains(t.getUniqueId());
            boolean inMembers = r.getMembers() != null && r.getMembers().contains(t.getUniqueId());
            boolean already = owner ? inOwners : inMembers;
            List<String> lore = new ArrayList<>();
            lore.add(already ? "&aAlready in this list" : "&7Not in this list");
            lore.add("");
            lore.add("&bClick &7to " + (already ? "remove" : "add") + (owner ? " (owner)" : " (member)"));
            ItemStack head = Text.item(Material.PLAYER_HEAD,
                    (already ? "&c-" : "&a+") + " &f" + t.getName(), lore);
            // use the real player skull texture when available
            var meta = head.getItemMeta();
            if (meta instanceof org.bukkit.inventory.meta.SkullMeta sm) {
                sm.setOwningPlayer(t);
                head.setItemMeta(sm);
            }
            inv.setItem(slot++, head);
        }
        inv.setItem(CLOSE_SLOT, Text.item(Material.BARRIER, "&cBack", null));

        plugin.gui().open(p, inv, (player, e) -> {
            if (e.getSlot() == CLOSE_SLOT) {
                openMembersMenu(player, r.getId());
                return;
            }
            if (e.getSlot() < 0 || e.getSlot() >= online.size()) return;
            Player target = online.get(e.getSlot());
            boolean admin = player.hasPermission("wgc.admin") || player.hasPermission("wgc.member.others");
            if (!admin && !WgBridge.isOwner(WgBridge.region(player.getWorld(), r.getId()), player.getUniqueId(), player.getName())) {
                plugin.lang().send(player, "flag-no-permission-region");
                return;
            }
            boolean already = owner
                    ? r.getOwners().contains(target.getUniqueId())
                    : r.getMembers().contains(target.getUniqueId());
            if (owner) {
                if (already) r.getOwners().removePlayer(target.getUniqueId());
                else r.getOwners().addPlayer(target.getUniqueId());
            } else {
                if (already) r.getMembers().removePlayer(target.getUniqueId());
                else r.getMembers().addPlayer(target.getUniqueId());
            }
            String key = already ? "member-removed" : "member-added";
            String role = owner ? "owner" : "member";
            player.sendMessage(plugin.lang().prefix() + plugin.lang().fmt(key, target.getName(), role, r.getId()));
            openPlayerPick(player, r.getId(), owner); // refresh
        });
    }

    /* ---------------- delete confirm ---------------- */

    private void openDeleteConfirm(Player p, String regionId) {
        Inventory inv = Bukkit.createInventory(null, 27, plugin.lang().fmt("confirm-menu-title", regionId));
        inv.setItem(11, Text.item(Material.LIME_WOOL, "&a&lConfirm delete", List.of(
                "&7Deletes &f" + regionId, "&cThis cannot be undone!")));
        inv.setItem(15, Text.item(Material.RED_WOOL, "&c&lCancel", null));

        plugin.gui().open(p, inv, (player, e) -> {
            if (e.getSlot() == 11) {
                boolean admin = player.hasPermission("wgc.admin") || player.hasPermission("wgc.delete.others");
                if (!admin && !WgBridge.isOwner(WgBridge.region(player.getWorld(), regionId), player.getUniqueId(), player.getName())) {
                    plugin.lang().send(player, "delete-not-owner");
                    player.closeInventory();
                    return;
                }
                var rm = WgBridge.managerBukkit(player.getWorld());
                var r = WgBridge.region(player.getWorld(), regionId);
                if (rm != null && r != null && WgBridge.delete(rm, r)) {
                    plugin.lang().send(player, "delete-success", regionId);
                } else {
                    plugin.lang().send(player, "not-found", regionId);
                }
                player.closeInventory();
            } else if (e.getSlot() == 15) {
                openRegionPanel(player, regionId);
            }
        });
    }
}
