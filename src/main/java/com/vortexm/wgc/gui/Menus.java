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
import java.util.Locale;
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
        // admins see ALL regions in the GUI (same rule as /wgc list), others only their own
        List<ProtectedRegion> regions = p.hasPermission("wgc.admin")
                ? WgBridge.regionsOf(p.getWorld())
                : WgBridge.regionsOwnedBy(p.getWorld(), p.getUniqueId(), p.getName());
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

        // nav bar (45=prev, 49=close, 53=next; other bar slots get glass filler)
        inv.setItem(PREV_SLOT, Text.item(Material.ARROW, "&7Previous page", null));
        inv.setItem(CLOSE_SLOT, Text.item(Material.BARRIER, "&cClose", null));
        inv.setItem(NEXT_SLOT, Text.item(Material.ARROW, "&7Next page", null));
        ItemStack filler = Text.item(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        for (int s = 45; s <= 53; s++) {
            if (inv.getItem(s) == null) inv.setItem(s, filler);
        }

        final int fPage = page;
        final int fPages = pages;
        plugin.gui().open(p, inv, (player, e) -> {
            int slot = e.getSlot();
            if (slot == PREV_SLOT && fPage > 1) {
                openRegions(player, fPage - 1);
            } else if (slot == NEXT_SLOT && fPage < fPages) {
                openRegions(player, fPage + 1);
            } else if (slot == CLOSE_SLOT) {
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
        Inventory inv = Bukkit.createInventory(null, 45, lang.fmt("region-menu-title", r.getId()));

        inv.setItem(10, Text.item(Material.RED_BANNER, "&c&lFlags", List.of(
                "&7View and toggle all flags", "", "&bClick &7to open")));
        inv.setItem(12, Text.item(Material.PLAYER_HEAD, "&e&lMembers", List.of(
                "&7Owners &7and members", "", "&bClick &7to open")));
        inv.setItem(14, Text.item(Material.LIME_WOOL, "&a&lInfo", List.of(
                "&7Show details in chat", "", "&bClick &7to run &f/wgc info " + r.getId())));
        inv.setItem(16, Text.item(Material.TNT, "&c&lDelete", List.of(
                "&7Delete this region", "", "&cClick &7to confirm")));

        inv.setItem(19, Text.item(Material.ENDER_PEARL, "&d&lTeleport", List.of(
                "&7Go to the region center or", "&7its &fspawn/teleport &7flag", "",
                "&bClick &7to teleport")));
        inv.setItem(21, Text.item(Material.RESPAWN_ANCHOR, "&6&lSet Spawn", List.of(
                "&7Set the region spawn flag", "&7to where you are standing", "",
                "&7Must be &finside &7the region")));
        inv.setItem(23, Text.item(Material.EXPERIENCE_BOTTLE, "&b&lPriority&7: &f" + r.getPriority(), List.of(
                "&7Higher priority wins conflicts", "", "&bLeft-click &7+1  &f+10  &7+100",
                "&fShift+click &7-1  -10  -100", "&7Drop key: type a custom value")));
        inv.setItem(25, Text.item(Material.NAME_TAG, "&2&lParent&7: &f" +
                (r.getParent() != null ? r.getParent().getId() : "none"), List.of(
                "&7Child regions inherit members", "&7and flags from their parent", "",
                "&bClick &7to pick a parent", "&7Shift+click &7to remove parent")));
        inv.setItem(40, Text.item(Material.WOODEN_AXE, "&9&lRedefine", List.of(
                "&7Replace this region's area with", "&7your current WorldEdit selection", "",
                "&7Use &f//wand &7first!", "&fShift+click &7to apply")));

        plugin.gui().open(p, inv, (player, e) -> {
            int slot = e.getSlot();
            boolean shift = e.getClick().isShiftClick();
            switch (slot) {
                case 10 -> openFlagMenu(player, r.getId(), 0);
                case 12 -> openMembersMenu(player, r.getId());
                case 14 -> {
                    player.closeInventory();
                    Bukkit.dispatchCommand(player, "wgc info " + r.getId());
                }
                case 16 -> openDeleteConfirm(player, r.getId());
                case 19 -> {
                    player.closeInventory();
                    Bukkit.dispatchCommand(player, "wgc teleport " + r.getId());
                }
                case 21 -> {
                    player.closeInventory();
                    Bukkit.dispatchCommand(player, "wgc setspawn " + r.getId());
                }
                case 23 -> {
                    int delta = shift ? -10 : 10;
                    Bukkit.dispatchCommand(player, "wgc setpriority " + r.getId() + " " + (r.getPriority() + delta));
                    openRegionPanel(player, r.getId()); // refresh
                }
                case 25 -> {
                    if (shift) {
                        Bukkit.dispatchCommand(player, "wgc setparent " + r.getId());
                        openRegionPanel(player, r.getId());
                    } else {
                        openParentPick(player, r.getId());
                    }
                }
                case 40 -> {
                    if (!shift) {
                        plugin.lang().send(player, "define-shift-hint");
                        return;
                    }
                    player.closeInventory();
                    Bukkit.dispatchCommand(player, "wgc redefine " + r.getId());
                }
                default -> {
                }
            }
        });
    }

    /* ---------------- parent pick ---------------- */

    private void openParentPick(Player p, String regionId) {
        ProtectedRegion r = WgBridge.region(p.getWorld(), regionId);
        if (r == null) return;
        List<ProtectedRegion> candidates = new ArrayList<>();
        for (ProtectedRegion cand : WgBridge.regionsOf(p.getWorld())) {
            if (WgBridge.canBeParent(r, cand)) candidates.add(cand);
        }
        candidates.sort(Comparator.comparing(ProtectedRegion::getId));

        int perPage = 45;
        Inventory inv = Bukkit.createInventory(null, 54,
                plugin.lang().fmt("parent-menu-title", r.getId()));
        int shown = Math.min(perPage, candidates.size());
        for (int i = 0; i < shown; i++) {
            ProtectedRegion cand = candidates.get(i);
            inv.setItem(i, Text.item(Material.NAME_TAG, "&f" + cand.getId(), List.of(
                    "&7priority: &f" + cand.getPriority(), "",
                    "&bClick &7to set as parent")));
        }
        inv.setItem(CLOSE_SLOT, Text.item(Material.BARRIER, "&cBack", null));

        plugin.gui().open(p, inv, (player, e) -> {
            if (e.getSlot() == CLOSE_SLOT) {
                openRegionPanel(player, r.getId());
                return;
            }
            if (e.getSlot() < 0 || e.getSlot() >= shown) return;
            ProtectedRegion cand = candidates.get(e.getSlot());
            try {
                r.setParent(cand);
                plugin.lang().send(player, "parent-success", r.getId(), cand.getId());
            } catch (Exception ex) {
                plugin.lang().send(player, "parent-cycle");
            }
            openParentPick(player, r.getId()); // refresh
        });
    }

    /* ---------------- guide (the in-game handbook) ---------------- */

    private static final List<String> GUIDE_SECTIONS = List.of("commands", "flags", "claims", "permissions", "tips");

    public void openGuide(Player p, String section) {
        Lang lang = plugin.lang();
        String sec = section == null ? null
                : GUIDE_SECTIONS.stream().filter(s -> s.startsWith(section.toLowerCase(Locale.ROOT))).findFirst().orElse(null);
        if (sec == null) {
            // section chooser
            Inventory inv = Bukkit.createInventory(null, 27, lang.fmt("guide-menu-title"));
            inv.setItem(10, Text.item(Material.BOOK, "&b&lCommands", List.of(
                    "&7Every /wgc command explained", "", "&bClick &7to read")));
            inv.setItem(12, Text.item(Material.RED_DYE, "&c&lFlags", List.of(
                    "&7All flags and what they do", "", "&bClick &7to read")));
            inv.setItem(14, Text.item(Material.GRASS_BLOCK, "&a&lClaims", List.of(
                    "&7How to claim your first region", "", "&bClick &7to read")));
            inv.setItem(16, Text.item(Material.GOLDEN_AXE, "&6&lPermissions", List.of(
                    "&7Who can do what", "", "&bClick &7to read")));

            plugin.gui().open(p, inv, (player, e) -> {
                switch (e.getSlot()) {
                    case 10 -> openGuide(player, "commands");
                    case 12 -> openGuide(player, "flags");
                    case 14 -> openGuide(player, "claims");
                    case 16 -> openGuide(player, "permissions");
                    default -> {
                    }
                }
            });
            return;
        }
        openGuideSection(p, sec);
    }

    private static final int GUIDE_BACK_SLOT = 22; // bottom-middle of a 27-slot menu

    private void openGuideSection(Player p, String section) {
        Lang lang = plugin.lang();
        List<String[]> pages = GuideContent.pages(plugin, section); // [title, l1, l2, l3]
        Inventory inv = Bukkit.createInventory(null, 27, lang.fmt("guide-section-title",
                section.substring(0, 1).toUpperCase(Locale.ROOT) + section.substring(1)));
        int[] pageSlots = {2, 4, 6, 11, 15}; // 5 pages: 3 top row, 2 middle row
        for (int i = 0; i < Math.min(pageSlots.length, pages.size()); i++) {
            String[] page = pages.get(i);
            inv.setItem(pageSlots[i], Text.item(Material.PAPER, page[0], List.of(page[1], page[2], page[3])));
        }
        inv.setItem(GUIDE_BACK_SLOT, Text.item(Material.BARRIER, "&cBack", null));
        plugin.gui().open(p, inv, (player, e) -> {
            if (e.getSlot() == GUIDE_BACK_SLOT) {
                openGuide(player, null);
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
