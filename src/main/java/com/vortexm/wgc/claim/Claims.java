package com.vortexm.wgc.claim;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldedit.math.BlockVector3;
import com.vortexm.wgc.WorldGuardComplater;
import com.vortexm.wgc.util.WgBridge;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

/**
 * Self-serve claiming: /wgc claim [name] [radius]
 * Creates a cuboid region centered on the player with validation:
 * overlap, limit, min volume, cost (Vault), inside-region check.
 */
public final class Claims {

    private final WorldGuardComplater plugin;

    public Claims(WorldGuardComplater plugin) {
        this.plugin = plugin;
    }

    public enum Result {
        OK, OVERLAP, LIMIT, RADIUS, VOLUME, MONEY, INVALID_NAME, INSIDE_REGION, TAKEN
    }

    public Result claim(Player p, String name, int radius, StringBuilder outName) {
        FileConfiguration cfg = plugin.getConfig();
        World w = p.getWorld();
        Location loc = p.getLocation();

        // 1. validate name (or generate one)
        if (name == null || name.isEmpty()) {
            if (cfg.getBoolean("claim.auto-name", true)) {
                int n = 1;
                while (WgBridge.exists(w, cfg.getString("claim.prefix", "claim") + p.getName() + "_" + n)) {
                    n++;
                }
                name = cfg.getString("claim.prefix", "claim") + p.getName() + "_" + n;
            } else {
                return Result.INVALID_NAME;
            }
        }
        name = name.toLowerCase(Locale.ROOT);
        if (!name.matches("[a-z0-9_-]{1,32}")) {
            return Result.INVALID_NAME;
        }
        if (WgBridge.exists(w, name)) {
            return Result.TAKEN;
        }
        outName.append(name);

        // 2. validate radius
        int maxRadius = cfg.getInt("claim.max-radius", 48);
        if (radius < 1 || radius > maxRadius) {
            return Result.RADIUS;
        }

        // 3. limit check
        if (!p.hasPermission("wgc.limit.bypass")) {
            int limit = limitOf(p);
            long owned = countOwned(p);
            if (owned >= limit) {
                return Result.LIMIT;
            }
        }

        // 4. build cuboid
        int y1, y2;
        int height = cfg.getInt("claim.height", -1);
        if (height <= 0) {
            y1 = w.getMinHeight();
            y2 = w.getMaxHeight() - 1;
        } else {
            int y = loc.getBlockY();
            y1 = Math.max(w.getMinHeight(), y - height / 2);
            y2 = Math.min(w.getMaxHeight() - 1, y + height / 2);
        }
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(
                name,
                BlockVector3.at(loc.getBlockX() - radius, y1, loc.getBlockZ() - radius),
                BlockVector3.at(loc.getBlockX() + radius, y2, loc.getBlockZ() + radius));

        // 5. min volume
        long vol = WgBridge.volume(region);
        long minVol = cfg.getLong("claim.min-volume", 64);
        if (minVol > 0 && vol < minVol) {
            return Result.VOLUME;
        }

        // 6. already inside another region?
        if (cfg.getBoolean("claim.inside-region-deny", true)) {
            List<ProtectedRegion> at = WgBridge.regionsAt(loc);
            // any existing region at the player's location (own claims included) blocks claiming
            if (!at.isEmpty()) {
                return Result.INSIDE_REGION;
            }
        }

        // 7. money check (Vault)
        double cost = cfg.getDouble("claim.cost", 0.0);
        if (cost > 0 && !p.hasPermission("wgc.free")
                && plugin.vault() != null && plugin.vault().economy() != null) {
            if (!plugin.vault().economy().has(p, cost)) {
                return Result.MONEY;
            }
        }

        // 8. WorldGuard overlap test (against ALL regions, incl. other plugins')
        RegionManager rm = WgBridge.managerBukkit(w);
        ApplicableRegionSet overlapping = rm.getApplicableRegions(region);
        if (!overlapping.getRegions().isEmpty() && !cfg.getBoolean("claim.allow-overlap", false)) {
            return Result.OVERLAP;
        }

        // 9. charge + add + set owner
        if (cost > 0 && plugin.vault() != null && plugin.vault().economy() != null) {
            var resp = plugin.vault().economy().withdrawPlayer(p, cost);
            if (resp == null || !resp.transactionSuccess()) {
                return Result.MONEY;
            }
        }
        rm.addRegion(region);
        var owners = region.getOwners();
        owners.addPlayer(p.getUniqueId());
        return Result.OK;
    }

    public int limitOf(Player p) {
        FileConfiguration cfg = plugin.getConfig();
        int def = cfg.getInt("claim.default-limit", 3);
        // highest wgc.limit.<n> wins
        int best = -1;
        for (int n = 1; n <= 1000; n++) {
            if (p.hasPermission("wgc.limit." + n)) best = n;
        }
        return best >= 0 ? best : def;
    }

    public long countOwned(Player p) {
        return WgBridge.regionsOwnedBy(p.getWorld(), p.getUniqueId(), p.getName()).size();
    }
}
