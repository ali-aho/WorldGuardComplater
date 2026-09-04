package com.vortexm.wgc.command;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.vortexm.wgc.WorldGuardComplater;
import com.vortexm.wgc.claim.Claims;
import com.vortexm.wgc.gui.Menus;
import com.vortexm.wgc.util.FlagCatalog;
import com.vortexm.wgc.util.Lang;
import com.vortexm.wgc.util.WgBridge;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * /wgc — main command with all subcommands and full tab-complete.
 *
 * Subcommands: help, gui, claim, list, info, flag, addmember, removemember,
 * addowner, removeowner, delete, reload
 */
public final class WgcCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBS = List.of(
            "help", "guide", "gui", "claim", "list", "info", "flag",
            "addmember", "removemember", "addowner", "removeowner",
            "define", "redefine", "select", "setpriority", "setparent",
            "teleport", "setspawn", "delete", "reload");

    private final WorldGuardComplater plugin;
    private final Claims claims;

    public WgcCommand(WorldGuardComplater plugin) {
        this.plugin = plugin;
        this.claims = new Claims(plugin);
    }

    /* ============================ execution ============================ */

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Lang lang = plugin.lang();

        if (args.length == 0) {
            // opening the GUI is the friendliest default
            if (sender instanceof Player p) {
                if (plugin.getConfig().getBoolean("gui.enabled", true)) {
                    menus().openRegions(p, 1);
                } else {
                    lang.send(p, "gui-open-denied");
                }
                return true;
            }
            help(sender, 1);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "help" -> help(sender, args.length > 1 ? parseInt(args[1], 1) : 1);
            case "guide", "book", "wiki", "helpme" -> guide(sender, args);
            case "gui" -> {
                if (!(sender instanceof Player p)) {
                    lang.send(sender, "player-only");
                    return true;
                }
                if (!plugin.getConfig().getBoolean("gui.enabled", true)) {
                    lang.send(sender, "gui-open-denied");
                    return true;
                }
                menus().openRegions(p, 1);
            }
            case "claim" -> claim(sender, args);
            case "list" -> list(sender, args);
            case "info", "i" -> info(sender, args);
            case "flag", "f" -> flag(sender, args);
            case "addmember", "addmem", "am" -> member(sender, args, "addmember", false);
            case "removemember", "remmember", "removemem", "remmem", "rm" -> member(sender, args, "removemember", false);
            case "addowner", "ao" -> member(sender, args, "addowner", true);
            case "removeowner", "remowner", "ro" -> member(sender, args, "removeowner", true);
            case "delete", "del", "rem" -> delete(sender, args);
            case "define", "create", "def", "d" -> define(sender, args, false);
            case "redefine", "update", "move" -> define(sender, args, true);
            case "select", "sel", "s" -> select(sender, args);
            case "setpriority", "priority", "pri" -> setPriority(sender, args);
            case "setparent", "parent", "par" -> setParent(sender, args);
            case "teleport", "tp" -> teleport(sender, args);
            case "setspawn", "ss" -> setSpawn(sender, args);
            case "reload" -> {
                if (!sender.hasPermission("wgc.admin")) {
                    lang.send(sender, "no-permission");
                    return true;
                }
                plugin.reloadAll();
                lang.send(sender, "reload-done");
            }
            default -> {
                lang.send(sender, "help-none");
            }
        }
        return true;
    }

    /* ============================ subcommands ============================ */

    private void help(CommandSender s, int page) {
        if (!s.hasPermission("wgc.help")) {
            plugin.lang().send(s, "no-permission");
            return;
        }
        record Line(String cmd, String desc, String perm) {
        }
        List<Line> lines = new ArrayList<>();
        lines.add(new Line("/wgc", "Open the regions GUI", "wgc.use"));
        lines.add(new Line("/wgc gui", "Open the regions GUI", "wgc.use"));
        lines.add(new Line("/wgc claim [name] [radius]", "Claim the land around you", "wgc.claim"));
        lines.add(new Line("/wgc list [page]", "List your regions", "wgc.list"));
        lines.add(new Line("/wgc info <region>", "Region details", "wgc.info"));
        lines.add(new Line("/wgc flag <region> <flag> [value]", "Set/unset a flag (no value = unset)", "wgc.flag.own"));
        lines.add(new Line("/wgc addmember <region> <player>", "Add a member", "wgc.member.own"));
        lines.add(new Line("/wgc removemember <region> <player>", "Remove a member", "wgc.member.own"));
        lines.add(new Line("/wgc addowner <region> <player>", "Add an owner", "wgc.member.own"));
        lines.add(new Line("/wgc removeowner <region> <player>", "Remove an owner", "wgc.member.own"));
        lines.add(new Line("/wgc delete <region> [confirm]", "Delete a region", "wgc.delete.own"));
        lines.add(new Line("/wgc reload", "Reload the config", "wgc.admin"));

        int per = 8;
        int pages = (lines.size() + per - 1) / per;
        if (page < 1) page = 1;
        if (page > pages) page = pages;
        Lang lang = plugin.lang();
        s.sendMessage(lang.fmt("help-header", page, pages));
        for (int i = (page - 1) * per; i < Math.min(page * per, lines.size()); i++) {
            Line l = lines.get(i);
            if (l.perm().equals("wgc.admin") && !s.hasPermission("wgc.admin")) continue;
            s.sendMessage(lang.fmt("help-line", l.cmd(), l.desc()));
        }
        if (page < pages) {
            s.sendMessage(lang.fmt("help-next", page + 1));
        }
    }

    private void claim(CommandSender sender, String[] args) {
        Lang lang = plugin.lang();
        if (!(sender instanceof Player p)) {
            lang.send(sender, "player-only");
            return;
        }
        if (!p.hasPermission("wgc.claim")) {
            lang.send(p, "no-permission");
            return;
        }
        String name = null;
        int radius = plugin.getConfig().getInt("claim.default-radius", 16);
        if (args.length >= 2) {
            if (args[1].matches("\\d+")) {
                radius = Integer.parseInt(args[1]);
            } else {
                name = args[1];
            }
        }
        if (args.length >= 3) {
            if (args[2].matches("\\d+")) {
                radius = Integer.parseInt(args[2]);
            } else if (name == null) {
                name = args[2];
            }
        }

        StringBuilder outName = new StringBuilder();
        Claims.Result res = claims.claim(p, name, radius, outName);
        switch (res) {
            case OK -> lang.send(p, "claim-success", outName.toString(), radius);
            case OVERLAP -> lang.send(p, "claim-fail-overlap");
            case LIMIT -> lang.send(p, "claim-fail-limit", claims.limitOf(p));
            case RADIUS -> lang.send(p, "claim-fail-radius", plugin.getConfig().getInt("claim.max-radius", 48));
            case VOLUME -> lang.send(p, "claim-fail-volume", plugin.getConfig().getLong("claim.min-volume", 64));
            case MONEY -> lang.send(p, "claim-fail-money", plugin.getConfig().getDouble("claim.cost", 0.0));
            case INVALID_NAME -> lang.send(p, "claim-usage");
            case TAKEN -> lang.send(p, "region-id-taken");
            case INSIDE_REGION -> lang.send(p, "claim-fail-cross-region");
        }
        if (res == Claims.Result.OK) {
            // optional refund hook is handled on delete
        }
    }

    private void list(CommandSender sender, String[] args) {
        Lang lang = plugin.lang();
        if (!sender.hasPermission("wgc.list")) {
            lang.send(sender, "no-permission");
            return;
        }
        int page = args.length > 1 ? parseInt(args[1], 1) : 1;

        // admins see all regions, players only their own
        List<ProtectedRegion> regions;
        if (sender instanceof Player p) {
            boolean admin = sender.hasPermission("wgc.admin");
            regions = admin
                    ? WgBridge.regionsOf(p.getWorld())
                    : WgBridge.regionsOwnedBy(p.getWorld(), p.getUniqueId(), p.getName());
        } else {
            regions = new ArrayList<>();
            for (var world : Bukkit.getWorlds()) {
                regions.addAll(WgBridge.regionsOf(world));
            }
        }

        int per = 8;
        int pages = Math.max(1, (regions.size() + per - 1) / per);
        if (page < 1) page = 1;
        if (page > pages) page = pages;
        sender.sendMessage(lang.fmt("list-header", page, pages));
        if (regions.isEmpty()) {
            sender.sendMessage(lang.fmt("list-empty"));
            return;
        }
        for (int i = (page - 1) * per; i < Math.min(page * per, regions.size()); i++) {
            ProtectedRegion r = regions.get(i);
            var members = r.getMembers() == null ? 0 : r.getMembers().size();
            var owners = r.getOwners() == null ? 0 : r.getOwners().size();
            sender.sendMessage(lang.fmt("list-line", r.getId(), "owners:" + owners + "/members:" + members,
                    owners + members));
        }
        if (page < pages) {
            sender.sendMessage(lang.fmt("list-footer", page + 1));
        }
    }

    private void info(CommandSender sender, String[] args) {
        Lang lang = plugin.lang();
        if (!sender.hasPermission("wgc.info")) {
            lang.send(sender, "no-permission");
            return;
        }
        if (!(sender instanceof Player p)) {
            lang.send(sender, "player-only");
            return;
        }
        var world = p.getWorld();
        ProtectedRegion r;
        if (args.length >= 2) {
            r = WgBridge.region(world, args[1]);
            if (r == null) {
                lang.send(p, "not-found", args[1]);
                return;
            }
        } else {
            List<ProtectedRegion> at = WgBridge.regionsAt(p.getLocation());
            if (at.isEmpty()) {
                lang.send(p, "info-none");
                return;
            }
            r = at.get(0);
        }

        p.sendMessage(lang.fmt("info-header", r.getId(), world.getName()));
        // flags
        boolean any = false;
        for (var entry : FlagCatalog.available()) {
            var f = com.sk89q.worldguard.WorldGuard.getInstance().getFlagRegistry().get(entry.name());
            if (f == null) continue;
            Object v = r.getFlag(f);
            if (v != null) {
                p.sendMessage(lang.fmt("info-flag-line", entry.name(), String.valueOf(v)));
                any = true;
        }
        }
        if (!any) p.sendMessage(lang.fmt("info-none-set"));
        var owners = r.getOwners();
        var members = r.getMembers();
        p.sendMessage(lang.fmt("info-owners", owners == null ? "-" : owners.toUserFriendlyString()));
        p.sendMessage(lang.fmt("info-members", members == null ? "-" : members.toUserFriendlyString()));
        p.sendMessage(lang.fmt("info-volume", WgBridge.volume(r)));
        p.sendMessage(lang.fmt("info-bounds", WgBridge.boundsString(r)));
    }

    private void flag(CommandSender sender, String[] args) {
        Lang lang = plugin.lang();
        if (!(sender instanceof Player p)) {
            lang.send(sender, "player-only");
            return;
        }
        if (!p.hasPermission("wgc.flag.own") && !p.hasPermission("wgc.flag.others") && !p.hasPermission("wgc.admin")) {
            lang.send(p, "no-permission");
            return;
        }
        // /wgc flag <region> <flag> [value...]
        if (args.length < 3) {
            lang.send(p, "member-usage", "flag <region> <flag> [value]");
            return;
        }
        ProtectedRegion r = WgBridge.region(p.getWorld(), args[1]);
        if (r == null) {
            lang.send(p, "not-found", args[1]);
            return;
        }
        boolean admin = p.hasPermission("wgc.admin") || p.hasPermission("wgc.flag.others");
        if (!admin && !WgBridge.isOwner(r, p.getUniqueId(), p.getName())) {
            lang.send(p, "flag-no-permission-region");
            return;
        }
        String flagName = args[2].toLowerCase(Locale.ROOT);
        var registry = com.sk89q.worldguard.WorldGuard.getInstance().getFlagRegistry();
        var f = registry.get(flagName);
        if (f == null) {
            lang.send(p, "flag-unknown", flagName);
            return;
        }
        if (args.length == 3) {
            r.setFlag(f, null);
            lang.send(p, "flag-unset", flagName, r.getId());
            return;
        }
        String value = String.join(" ", java.util.Arrays.copyOfRange(args, 3, args.length));
        try {
            var actor = com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().wrapCommandSender(p);
            var ctx = com.sk89q.worldguard.protection.flags.FlagContext.create()
                    .setSender(actor).setInput(value).build();
            Object parsed = f.parseInput(ctx);
            setFlagUnchecked(r, f, parsed);
            lang.send(p, "flag-set", flagName, value, r.getId());
        } catch (Exception ex) {
            lang.send(p, "flag-bad-value", flagName, value);
        }
    }

    /** Generic-safe flag set via raw map put. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setFlagUnchecked(ProtectedRegion r, com.sk89q.worldguard.protection.flags.Flag f, Object parsed) {
        r.setFlag(f, parsed);
    }

    private void member(CommandSender sender, String[] args, String sub, boolean owner) {
        Lang lang = plugin.lang();
        if (!(sender instanceof Player p)) {
            lang.send(sender, "player-only");
            return;
        }
        boolean admin = p.hasPermission("wgc.admin") || p.hasPermission("wgc.member.others");
        if (!admin && !p.hasPermission("wgc.member.own")) {
            lang.send(p, "no-permission");
            return;
        }
        // /wgc <addmember|removemember|addowner|removeowner> <region> <player>
        if (args.length < 3) {
            lang.send(p, "member-usage", sub);
            return;
        }
        ProtectedRegion r = WgBridge.region(p.getWorld(), args[1]);
        if (r == null) {
            lang.send(p, "not-found", args[1]);
            return;
        }
        if (!admin && !WgBridge.isOwner(r, p.getUniqueId(), p.getName())) {
            lang.send(p, "flag-no-permission-region");
            return;
        }
        String playerName = args[2];
        var domain = owner ? r.getOwners() : r.getMembers();
        if (domain == null) {
            domain = new com.sk89q.worldguard.domains.DefaultDomain();
            if (owner) r.setOwners(domain);
            else r.setMembers(domain);
        }
        // resolve online first (avoids mojang lookups), fall back to name
        Player target = Bukkit.getPlayerExact(playerName);
        boolean removed = sub.startsWith("remove");
        if (target != null) {
            if (removed) domain.removePlayer(target.getUniqueId());
            else domain.addPlayer(target.getUniqueId());
        } else {
            if (removed) domain.removePlayer(playerName);
            else domain.addPlayer(playerName);
        }
        String role = owner ? "owner" : "member";
        String key = removed ? "member-removed" : "member-added";
        lang.send(p, key, playerName, role, r.getId());
    }

    private void delete(CommandSender sender, String[] args) {
        Lang lang = plugin.lang();
        if (!(sender instanceof Player p)) {
            lang.send(sender, "player-only");
            return;
        }
        if (!p.hasPermission("wgc.delete.own") && !p.hasPermission("wgc.delete.others") && !p.hasPermission("wgc.admin")) {
            lang.send(p, "no-permission");
            return;
        }
        if (args.length < 2) {
            lang.send(p, "delete-confirm", "<region>");
            return;
        }
        String regionId = args[1];
        boolean admin = p.hasPermission("wgc.admin") || p.hasPermission("wgc.delete.others");
        if (!admin && !WgBridge.isOwner(WgBridge.region(p.getWorld(), regionId), p.getUniqueId(), p.getName())) {
            lang.send(p, "delete-not-owner");
            return;
        }
        var r = WgBridge.region(p.getWorld(), regionId);
        if (r == null) {
            lang.send(p, "not-found", regionId);
            return;
        }
        // confirm step (unless admin)
        if (!admin && args.length < 3) {
            lang.send(p, "delete-confirm", regionId);
            return;
        }
        var rm = WgBridge.managerBukkit(p.getWorld());
        if (rm != null && WgBridge.delete(rm, r)) {
            lang.send(p, "delete-success", regionId);
            // refund?
            double refund = plugin.getConfig().getDouble("claim.refund", 0.0);
            if (refund > 0 && plugin.vault().hasEconomy()) {
                plugin.vault().economy().depositPlayer(p, refund);
                lang.send(p, "claim-refunded", refund);
            }
        } else {
            lang.send(p, "not-found", regionId);
        }
    }

    /* ==================== new region commands (v1.1) ==================== */

    /** /wgc define <id> and /wgc redefine <id> (from WE selection). */
    private void define(CommandSender sender, String[] args, boolean redefineMode) {
        Lang lang = plugin.lang();
        if (!(sender instanceof Player p)) {
            lang.send(sender, "player-only");
            return;
        }
        String key = redefineMode ? "redefine" : "define";
        if (!p.hasPermission("wgc.admin")
                && !p.hasPermission("wgc." + key + ".own")
                && !p.hasPermission("wgc." + key + ".others")) {
            lang.send(p, "no-permission");
            return;
        }
        if (args.length < 2) {
            lang.send(p, key + "-usage");
            return;
        }
        String id = args[1];
        if (!id.matches(plugin.getConfig().getString("region-id-regex", "^[A-Za-z0-9_-]{1,32}$"))) {
            lang.send(p, "claim-fail-invalid");
            return;
        }
        var world = p.getWorld();
        ProtectedCuboidRegion cub = WgBridge.cuboidFromSelection(p, id);
        if (cub == null) {
            lang.send(p, "define-no-selection");
            return;
        }
        var rm = WgBridge.managerBukkit(world);
        if (rm == null) {
            lang.send(p, "wg-missing");
            return;
        }
        if (redefineMode) {
            ProtectedRegion existing = WgBridge.region(world, id);
            if (existing == null) {
                lang.send(p, "not-found", id);
                return;
            }
            boolean canOthers = p.hasPermission("wgc.admin") || p.hasPermission("wgc.redefine.others");
            if (!canOthers && !WgBridge.isOwner(existing, p.getUniqueId(), p.getName())) {
                lang.send(p, "define-not-owner");
                return;
            }
            if (WgBridge.redefine(rm, existing, cub)) {
                lang.send(p, "redefine-success", id, WgBridge.volume(cub));
            } else {
                lang.send(p, "define-fail");
            }
            return;
        }
        if (WgBridge.exists(world, id)) {
            lang.send(p, "region-id-taken");
            return;
        }
        if (WgBridge.addRegion(rm, cub)) {
            // creator becomes owner automatically
            cub.getOwners().addPlayer(p.getUniqueId());
            lang.send(p, "define-success", id, WgBridge.volume(cub));
        } else {
            lang.send(p, "define-fail");
        }
    }

    /** /wgc select [region] - make a WE selection out of a region. */
    private void select(CommandSender sender, String[] args) {
        Lang lang = plugin.lang();
        if (!(sender instanceof Player p)) {
            lang.send(sender, "player-only");
            return;
        }
        if (!p.hasPermission("wgc.select")) {
            lang.send(p, "no-permission");
            return;
        }
        ProtectedRegion r;
        if (args.length >= 2) {
            r = WgBridge.region(p.getWorld(), args[1]);
            if (r == null) {
                lang.send(p, "not-found", args[1]);
                return;
            }
        } else {
            List<ProtectedRegion> at = WgBridge.regionsAt(p.getLocation());
            if (at.isEmpty()) {
                lang.send(p, "info-none");
                return;
            }
            r = at.get(0);
        }
        if (WgBridge.selectRegion(p, r)) {
            lang.send(p, "select-success", r.getId());
        } else {
            lang.send(p, "select-fail");
        }
    }

    /** /wgc setpriority <region> <value>. */
    private void setPriority(CommandSender sender, String[] args) {
        Lang lang = plugin.lang();
        if (!(sender instanceof Player p)) {
            lang.send(sender, "player-only");
            return;
        }
        if (!p.hasPermission("wgc.admin") && !p.hasPermission("wgc.priority.own") && !p.hasPermission("wgc.priority.others")) {
            lang.send(p, "no-permission");
            return;
        }
        if (args.length < 3) {
            lang.send(p, "priority-usage");
            return;
        }
        ProtectedRegion r = WgBridge.region(p.getWorld(), args[1]);
        if (r == null) {
            lang.send(p, "not-found", args[1]);
            return;
        }
        boolean canOthers = p.hasPermission("wgc.admin") || p.hasPermission("wgc.priority.others");
        if (!canOthers && !WgBridge.isOwner(r, p.getUniqueId(), p.getName())) {
            lang.send(p, "flag-no-permission-region");
            return;
        }
        int val;
        try {
            val = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            lang.send(p, "priority-usage");
            return;
        }
        r.setPriority(val);
        lang.send(p, "priority-success", r.getId(), val);
    }

    /** /wgc setparent <region> [parent]. */
    private void setParent(CommandSender sender, String[] args) {
        Lang lang = plugin.lang();
        if (!(sender instanceof Player p)) {
            lang.send(sender, "player-only");
            return;
        }
        if (!p.hasPermission("wgc.admin") && !p.hasPermission("wgc.parent.own") && !p.hasPermission("wgc.parent.others")) {
            lang.send(p, "no-permission");
            return;
        }
        if (args.length < 2) {
            lang.send(p, "parent-usage");
            return;
        }
        ProtectedRegion r = WgBridge.region(p.getWorld(), args[1]);
        if (r == null) {
            lang.send(p, "not-found", args[1]);
            return;
        }
        boolean canOthers = p.hasPermission("wgc.admin") || p.hasPermission("wgc.parent.others");
        if (!canOthers && !WgBridge.isOwner(r, p.getUniqueId(), p.getName())) {
            lang.send(p, "flag-no-permission-region");
            return;
        }
        if (args.length < 3) {
            r.clearParent();
            lang.send(p, "parent-cleared", r.getId());
            return;
        }
        ProtectedRegion parent = WgBridge.region(p.getWorld(), args[2]);
        if (parent == null) {
            lang.send(p, "not-found", args[2]);
            return;
        }
        if (!WgBridge.canBeParent(r, parent)) {
            lang.send(p, "parent-cycle");
            return;
        }
        try {
            r.setParent(parent);
            lang.send(p, "parent-success", r.getId(), parent.getId());
        } catch (Exception e) {
            lang.send(p, "parent-cycle");
        }
    }

    /** /wgc teleport <region> [spawn]. */
    private void teleport(CommandSender sender, String[] args) {
        Lang lang = plugin.lang();
        if (!(sender instanceof Player p)) {
            lang.send(sender, "player-only");
            return;
        }
        if (!p.hasPermission("wgc.teleport")) {
            lang.send(p, "no-permission");
            return;
        }
        if (args.length < 2) {
            lang.send(p, "teleport-usage");
            return;
        }
        ProtectedRegion r = WgBridge.region(p.getWorld(), args[1]);
        if (r == null) {
            lang.send(p, "not-found", args[1]);
            return;
        }
        boolean useSpawn = args.length >= 3 && args[2].equalsIgnoreCase("spawn");
        Location dest = WgBridge.flagLocation(r, useSpawn ? "spawn" : "teleport", p.getWorld());
        if (dest == null) {
            dest = WgBridge.center(r, p.getWorld());
        }
        if (dest == null) {
            lang.send(p, "teleport-fail");
            return;
        }
        p.teleport(dest);
        lang.send(p, "teleport-success", r.getId());
    }

    /** /wgc setspawn <region> - set the region spawn flag where you stand. */
    private void setSpawn(CommandSender sender, String[] args) {
        Lang lang = plugin.lang();
        if (!(sender instanceof Player p)) {
            lang.send(sender, "player-only");
            return;
        }
        if (!p.hasPermission("wgc.admin") && !p.hasPermission("wgc.setspawn.own") && !p.hasPermission("wgc.setspawn.others")) {
            lang.send(p, "no-permission");
            return;
        }
        if (args.length < 2) {
            lang.send(p, "setspawn-usage");
            return;
        }
        ProtectedRegion r = WgBridge.region(p.getWorld(), args[1]);
        if (r == null) {
            lang.send(p, "not-found", args[1]);
            return;
        }
        boolean canOthers = p.hasPermission("wgc.admin") || p.hasPermission("wgc.setspawn.others");
        if (!canOthers && !WgBridge.isOwner(r, p.getUniqueId(), p.getName())) {
            lang.send(p, "flag-no-permission-region");
            return;
        }
        if (!r.contains(com.sk89q.worldedit.math.BlockVector3.at(
                p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()))) {
            lang.send(p, "setspawn-outside");
            return;
        }
        var f = com.sk89q.worldguard.WorldGuard.getInstance().getFlagRegistry().get("spawn");
        if (f != null) {
            // wrap the Bukkit location into a WE location for the flag
            var weLoc = com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(p.getLocation());
            @SuppressWarnings("unchecked")
            var flag = (com.sk89q.worldguard.protection.flags.LocationFlag) f;
            r.setFlag(flag, weLoc);
        }
        lang.send(p, "setspawn-success", r.getId());
    }

    /** /wgc guide [section] - the in-game WGC handbook. */
    private void guide(CommandSender sender, String[] args) {
        Lang lang = plugin.lang();
        if (!(sender instanceof Player p)) {
            lang.send(sender, "player-only");
            return;
        }
        if (!p.hasPermission("wgc.guide")) {
            lang.send(p, "no-permission");
            return;
        }
        menus().openGuide(p, args.length >= 2 ? args[1] : null);
    }

    /* ============================ tab-complete ============================ */

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        String last = args.length > 0 ? args[args.length - 1].toLowerCase(Locale.ROOT) : "";

        if (args.length == 1) {
            for (String s : SUBS) {
                if (s.startsWith(last) && canSee(sender, s)) out.add(s);
            }
            return out;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "define", "create", "def", "d" -> {
                if (args.length == 2) out.add("[id]");
            }
            case "redefine", "update", "move" -> {
                if (args.length == 2) out.addAll(regionNames(sender, last));
            }
            case "select", "sel", "s" -> {
                if (args.length == 2) out.addAll(regionNames(sender, last));
            }
            case "setpriority", "priority", "pri" -> {
                if (args.length == 2) out.addAll(regionNames(sender, last));
                if (args.length == 3) out.addAll(List.of("0", "1", "5", "10", "100"));
            }
            case "setparent", "parent", "par" -> {
                if (args.length == 2) out.addAll(regionNames(sender, last));
                if (args.length == 3) out.addAll(regionNames(sender, last));
            }
            case "teleport", "tp" -> {
                if (args.length == 2) out.addAll(regionNames(sender, last));
                if (args.length == 3) out.addAll(List.of("spawn"));
            }
            case "setspawn", "ss" -> {
                if (args.length == 2) out.addAll(regionNames(sender, last));
            }
            case "guide", "book", "wiki", "helpme" -> {
                if (args.length == 2) {
                    out.addAll(List.of("commands", "flags", "claims", "permissions", "tips"));
                }
            }
            case "info", "delete", "del", "rem", "addmember", "addmem", "am",
                    "removemember", "remmember", "removemem", "remmem", "rm",
                    "addowner", "ao", "removeowner", "remowner", "ro", "gui" -> {
                if (args.length == 2) out.addAll(regionNames(sender, last));
                if (args.length == 3 && (sub.startsWith("add") || sub.startsWith("remove"))) {
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        if (pl.getName().toLowerCase(Locale.ROOT).startsWith(last)) out.add(pl.getName());
                    }
                }
            }
            case "flag" -> {
                if (args.length == 2) out.addAll(regionNames(sender, last));
                if (args.length == 3) {
                    for (var e : FlagCatalog.available()) {
                        if (e.name().startsWith(last)) out.add(e.name());
                    }
                }
                if (args.length == 4) {
                    String flagName = args[2].toLowerCase(Locale.ROOT);
                    for (String v : FlagCatalog.suggestedValues(flagName)) {
                        if (v.toLowerCase(Locale.ROOT).startsWith(last)) out.add(v);
                    }
                }
            }
            case "claim" -> {
                if (args.length == 2) {
                    out.add("[name]");
                    out.add("[radius]");
                }
            }
            case "list", "help" -> {
                if (args.length == 2) out.add("[page]");
            }
            default -> {
            }
        }
        return out;
    }

    private List<String> regionNames(CommandSender sender, String prefix) {
        List<String> out = new ArrayList<>();
        List<ProtectedRegion> regions;
        if (sender instanceof Player p) {
            boolean admin = sender.hasPermission("wgc.admin");
            regions = admin ? WgBridge.regionsOf(p.getWorld())
                    : WgBridge.regionsOwnedBy(p.getWorld(), p.getUniqueId(), p.getName());
        } else {
            regions = new ArrayList<>();
            for (var world : Bukkit.getWorlds()) {
                regions.addAll(WgBridge.regionsOf(world));
            }
        }
        for (ProtectedRegion r : regions) {
            if (r.getId().startsWith(prefix)) out.add(r.getId());
        }
        return out;
    }

    private boolean canSee(CommandSender sender, String sub) {
        return switch (sub) {
            case "reload" -> sender.hasPermission("wgc.admin");
            default -> true;
        };
    }

    /* ============================ utils ============================ */

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private Menus menus() {
        return new Menus(plugin);
    }
}
