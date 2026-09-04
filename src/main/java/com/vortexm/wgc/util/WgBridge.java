package com.vortexm.wgc.util;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * WorldGuard + WorldEdit API bridge - all WG/WE interactions live here so the
 * rest of the plugin stays clean. Author: Vortex_Miner1 (VortexM), MIT.
 */
public final class WgBridge {

    private WgBridge() {
    }

    /** RegionManager for a WorldEdit world (null if none). */
    public static RegionManager manager(com.sk89q.worldedit.world.World world) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        return container == null ? null : container.get(world);
    }

    /** RegionManager for a Bukkit world. */
    public static RegionManager managerBukkit(org.bukkit.World bukkitWorld) {
        return manager(BukkitAdapter.adapt(bukkitWorld));
    }

    public static ProtectedRegion region(org.bukkit.World bukkitWorld, String id) {
        RegionManager rm = managerBukkit(bukkitWorld);
        return rm == null ? null : rm.getRegion(id.toLowerCase(Locale.ROOT));
    }

    public static boolean exists(org.bukkit.World bukkitWorld, String id) {
        return region(bukkitWorld, id) != null;
    }

    public static boolean delete(RegionManager rm, ProtectedRegion region) {
        return !rm.removeRegion(region.getId()).isEmpty() || rm.getRegion(region.getId()) == null;
    }

    public static List<ProtectedRegion> regionsOf(org.bukkit.World bukkitWorld) {
        RegionManager rm = managerBukkit(bukkitWorld);
        if (rm == null) return Collections.emptyList();
        List<ProtectedRegion> out = new ArrayList<>(rm.getRegions().values());
        out.sort(Comparator.comparing(ProtectedRegion::getId));
        return out;
    }

    /** Regions the player is an owner or member of, sorted by id. */
    public static List<ProtectedRegion> regionsOwnedBy(org.bukkit.World bukkitWorld, UUID uuid, String name) {
        List<ProtectedRegion> out = new ArrayList<>();
        for (ProtectedRegion r : regionsOf(bukkitWorld)) {
            if (isOwnerOrMember(r, uuid, name)) out.add(r);
        }
        out.sort(Comparator.comparing(ProtectedRegion::getId));
        return out;
    }

    public static boolean isOwnerOrMember(ProtectedRegion r, UUID uuid, String name) {
        DefaultDomain owners = r.getOwners();
        DefaultDomain members = r.getMembers();
        boolean o = owners != null && (owners.contains(uuid) || (name != null && owners.contains(name)));
        boolean m = members != null && (members.contains(uuid) || (name != null && members.contains(name)));
        return o || m;
    }

    public static boolean isOwner(ProtectedRegion r, UUID uuid, String name) {
        DefaultDomain owners = r.getOwners();
        return owners != null && (owners.contains(uuid) || (name != null && owners.contains(name)));
    }

    /* ======================= WorldEdit selection ======================= */

    /**
     * Create a cuboid region from the player's current WE selection (does NOT
     * register it). Returns null when the selection is missing or not cuboid.
     */
    public static ProtectedCuboidRegion cuboidFromSelection(Player p, String id) {
        WorldEditPlugin we = (WorldEditPlugin) org.bukkit.Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (we == null) return null;
        try {
            LocalSession session = we.getSession(p);
            Region sel = session.getSelection(BukkitAdapter.adapt(p.getWorld()));
            BlockVector3 min = sel.getMinimumPoint();
            BlockVector3 max = sel.getMaximumPoint();
            return new ProtectedCuboidRegion(id.toLowerCase(Locale.ROOT), min, max);
        } catch (Exception e) {
            return null;
        }
    }

    /** Make a WE cuboid selection out of a region (for /wgc select). */
    public static boolean selectRegion(Player p, ProtectedRegion r) {
        WorldEditPlugin we = (WorldEditPlugin) org.bukkit.Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (we == null) return false;
        try {
            LocalSession session = we.getSession(p);
            if (!(r instanceof ProtectedCuboidRegion cub)) return false;
            CuboidRegionSelector sel = new CuboidRegionSelector(BukkitAdapter.adapt(p.getWorld()),
                    cub.getMinimumPoint(), cub.getMaximumPoint());
            session.setRegionSelector(BukkitAdapter.adapt(p.getWorld()), sel);
            sel.learnChanges();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /* ======================= region mutation ======================= */

    /**
     * Redefine: replace the physical area of an existing region with the new
     * cuboid. WG has no in-place volume setter, so we swap the region in the
     * manager while preserving flags, domains, priority and parent.
     */
    public static boolean redefine(RegionManager rm, ProtectedRegion existing, ProtectedCuboidRegion replacement) {
        try {
            replacement.setPriority(existing.getPriority());
            replacement.setOwners(existing.getOwners());
            replacement.setMembers(existing.getMembers());
            replacement.setFlags(existing.getFlags());
            ProtectedRegion parent = existing.getParent();
            if (parent != null) {
                try {
                    replacement.setParent(parent);
                } catch (Exception ignored) {
                }
            }
            rm.removeRegion(existing.getId());
            rm.addRegion(replacement);
            return rm.getRegion(replacement.getId()) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /** Registers (or re-registers) a region; returns false on overlap error. */
    public static boolean addRegion(RegionManager rm, ProtectedRegion region) {
        try {
            rm.addRegion(region);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int volume(ProtectedRegion r) {
        try {
            return r.volume();
        } catch (Exception e) {
            return 0;
        }
    }

    public static String boundsString(ProtectedRegion r) {
        BlockVector3 min = r.getMinimumPoint();
        BlockVector3 max = r.getMaximumPoint();
        if (min == null || max == null) return "global";
        return min.getX() + ", " + min.getY() + ", " + min.getZ() + " -> " + max.getX() + ", " + max.getY() + ", " + max.getZ();
    }

    /** The region's teleport/spawn location from its flags, if set. */
    public static Location flagLocation(ProtectedRegion r, String flagName, org.bukkit.World world) {
        Flag<?> f = WorldGuard.getInstance().getFlagRegistry().get(flagName);
        if (f == null) return null;
        Object v = r.getFlag(f);
        if (v instanceof com.sk89q.worldedit.util.Location loc) {
            return BukkitAdapter.adapt(loc);
        }
        return null;
    }

    /** Center of a region as a Bukkit location (safe y = max). */
    public static Location center(ProtectedRegion r, org.bukkit.World world) {
        BlockVector3 min = r.getMinimumPoint();
        BlockVector3 max = r.getMaximumPoint();
        if (min == null || max == null) return null;
        return new Location(world,
                (min.getX() + max.getX()) / 2.0 + 0.5,
                max.getY() + 1.0,
                (min.getZ() + max.getZ()) / 2.0 + 0.5);
    }

    /** Highest-priority region at a location (excluding the global region). */
    public static ProtectedRegion topRegionAt(org.bukkit.Location loc) {
        RegionManager rm = managerBukkit(loc.getWorld());
        if (rm == null) return null;
        List<ProtectedRegion> at = regionsAt(loc);
        ProtectedRegion best = null;
        for (ProtectedRegion r : at) {
            if (r.getId().equals("__global__")) continue;
            if (best == null || r.getPriority() > best.getPriority()) best = r;
        }
        return best;
    }

    /** All non-global regions at a location, highest priority first. */
    public static List<ProtectedRegion> regionsAt(org.bukkit.Location loc) {
        RegionManager rm = managerBukkit(loc.getWorld());
        if (rm == null) return Collections.emptyList();
        List<ProtectedRegion> at = new ArrayList<>(rm.getApplicableRegions(
                BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())).getRegions());
        at.sort(Comparator.comparingInt(ProtectedRegion::getPriority).reversed());
        at.removeIf(r -> r.getId().equals("__global__"));
        return at;
    }

    /** Can the given parent be assigned to the region without cycles? */
    public static boolean canBeParent(ProtectedRegion child, ProtectedRegion candidate) {
        if (candidate == null || child == null) return false;
        if (child.getId().equals(candidate.getId())) return false;
        ProtectedRegion p = candidate;
        while (p != null) {
            if (p.getId().equals(child.getId())) return false; // would create a cycle
            p = p.getParent();
        }
        return true;
    }
}
