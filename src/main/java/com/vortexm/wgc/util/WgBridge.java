package com.vortexm.wgc.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * WorldGuard API bridge - all WorldGuard interactions live here so the rest
 * of the plugin stays clean. Author: Vortex_Miner1 (VortexM), MIT.
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

    /** Regions containing the location, highest priority first. */
    public static List<ProtectedRegion> regionsAt(Location loc) {
        RegionManager rm = managerBukkit(loc.getWorld());
        if (rm == null) return Collections.emptyList();
        BlockVector3 pos = BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        List<ProtectedRegion> out = new ArrayList<>(rm.getApplicableRegions(pos).getRegions());
        out.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));
        return out;
    }

    public static String boundsString(ProtectedRegion r) {
        var min = r.getMinimumPoint();
        var max = r.getMaximumPoint();
        if (min == null || max == null) return "global";
        return min.getBlockX() + ", " + min.getBlockY() + ", " + min.getBlockZ()
                + " -> " + max.getBlockX() + ", " + max.getBlockY() + ", " + max.getBlockZ();
    }

    public static long volume(ProtectedRegion r) {
        var min = r.getMinimumPoint();
        var max = r.getMaximumPoint();
        if (min == null || max == null) return -1;
        return (long) (max.getBlockX() - min.getBlockX() + 1)
                * (max.getBlockY() - min.getBlockY() + 1)
                * (max.getBlockZ() - min.getBlockZ() + 1);
    }

    public static Flag<?> flag(String name) {
        return WorldGuard.getInstance().getFlagRegistry().get(name.toLowerCase(Locale.ROOT));
    }

    /** Value of a flag on a region, or null. */
    public static String flagString(ProtectedRegion r, String flagName) {
        Flag<?> f = flag(flagName);
        if (f == null) return null;
        Object v = r.getFlag(f);
        return v == null ? null : String.valueOf(v);
    }
}
