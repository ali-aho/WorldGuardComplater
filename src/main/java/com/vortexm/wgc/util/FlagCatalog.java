package com.vortexm.wgc.util;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Curated flag catalog for tab-complete, GUI and validation.
 * Only flags registered by WorldGuard are exposed (checked at runtime).
 */
public final class FlagCatalog {

    /** A player-friendly flag entry. */
    public record Entry(String name, String type, String description, boolean stateFlag) {
    }

    private static final List<Entry> CATALOG = new ArrayList<>();

    static {
        // name, type, description, state?
        add("passthrough", "state", "Let the region not protect its area", true);
        add("build", "state", "Block place/break master switch", true);
        add("block-break", "state", "Whether blocks can be broken", true);
        add("block-place", "state", "Whether blocks can be placed", true);
        add("use", "state", "Doors, levers, buttons", true);
        add("interact", "state", "Using blocks/entities", true);
        add("chest-access", "state", "Opening inventories", true);
        add("pvp", "state", "Player versus player combat", true);
        add("damage-animals", "state", "Harming cows, sheep...", true);
        add("tnt", "state", "TNT detonation and damage", true);
        add("creeper-explosion", "state", "Creeper explosion damage", true);
        add("other-explosion", "state", "Other explosion damage", true);
        add("ghast-fireball", "state", "Ghast fireball damage", true);
        add("wither-damage", "state", "Wither damage", true);
        add("enderdragon-block-damage", "state", "Ender dragon block damage", true);
        add("enderman-grief", "state", "Endermen take/place blocks", true);
        add("ravager-grief", "state", "Ravagers break blocks", true);
        add("entity-painting-destroy", "state", "Mobs destroy paintings", true);
        add("entity-item-frame-destroy", "state", "Mobs destroy item frames", true);
        add("fire-spread", "state", "Fire spreading", true);
        add("lava-fire", "state", "Lava starting fires", true);
        add("lava-flow", "state", "Lava flowing", true);
        add("water-flow", "state", "Water flowing", true);
        add("lightning", "state", "Lightning strikes", true);
        add("snow-fall", "state", "Snow forming", true);
        add("snow-melt", "state", "Snow melting", true);
        add("ice-form", "state", "Ice forming", true);
        add("ice-melt", "state", "Ice melting", true);
        add("frosted-ice-form", "state", "Frost Walker ice", true);
        add("frosted-ice-melt", "state", "Frosted ice melting", true);
        add("mushroom-growth", "state", "Mushroom growth", true);
        add("leaf-decay", "state", "Leaf decay", true);
        add("grass-growth", "state", "Grass growth", true);
        add("mycelium-spread", "state", "Mycelium spread", true);
        add("vine-growth", "state", "Vine/kelp growth", true);
        add("crop-growth", "state", "Crop growth", true);
        add("soil-dry", "state", "Farmland drying", true);
        add("coral-fade", "state", "Coral dying", true);
        add("copper-fade", "state", "Copper oxidizing", true);
        add("rock-growth", "state", "Dripstone growth", true);
        add("sculk-growth", "state", "Sculk growth", true);
        add("snowman-trails", "state", "Snow golem snow trails", true);
        add("mob-damage", "state", "Mobs hurting players", true);
        add("mob-spawning", "state", "Mob spawning", true);
        add("entry", "state", "Players entering the region", true);
        add("exit", "state", "Players exiting the region", true);
        add("exit-via-teleport", "state", "Exiting via teleport", true);
        add("enderpearl", "state", "Ender pearl use", true);
        add("chorus-fruit-teleport", "state", "Chorus fruit teleport", true);
        add("item-pickup", "state", "Picking up items", true);
        add("item-drop", "state", "Dropping items", true);
        add("exp-drops", "state", "XP drops", true);
        add("invincible", "state", "Players are invincible", true);
        add("fall-damage", "state", "Fall damage", true);
        add("pistons", "state", "Pistons working", true);
        add("vehicle-place", "state", "Placing boats/minecarts", true);
        add("vehicle-destroy", "state", "Breaking vehicles", true);
        add("ride", "state", "Mounting vehicles/animals", true);
        add("lighter", "state", "Flint and steel use", true);
        add("block-trampling", "state", "Trampling farmland/eggs", true);
        add("item-frame-rotation", "state", "Rotating items in frames", true);
        add("firework-damage", "state", "Firework damage", true);
        add("use-anvil", "state", "Using anvils", true);
        add("use-dripleaf", "state", "Using dripleaf", true);
        add("potion-splash", "state", "Splash potions", true);
        add("send-chat", "state", "Sending chat", true);
        add("receive-chat", "state", "Receiving chat", true);
        add("notify-enter", "boolean", "Notify on player enter", false);
        add("notify-leave", "boolean", "Notify on player leave", false);
        add("exit-override", "boolean", "Always allow exit", false);
        add("greeting", "string", "Chat message on enter", false);
        add("greeting-title", "string", "Title on enter", false);
        add("farewell", "string", "Chat message on leave", false);
        add("farewell-title", "string", "Title on leave", false);
        add("entry-deny-message", "string", "Denied entry message", false);
        add("exit-deny-message", "string", "Denied exit message", false);
        add("deny-message", "string", "General deny message", false);
        add("teleport-message", "string", "Teleport message", false);
        add("game-mode", "gamemode", "Gamemode in region", false);
        add("time-lock", "string", "Locked time of day", false);
        add("weather-lock", "weather", "Locked weather", false);
        add("heal-amount", "integer", "Heal rate (half hearts)", false);
        add("heal-delay", "integer", "Seconds between heals", false);
        add("heal-min-health", "double", "Min health cap", false);
        add("heal-max-health", "double", "Max health cap", false);
        add("feed-amount", "integer", "Feed rate", false);
        add("feed-delay", "integer", "Seconds between feeds", false);
        add("feed-min-hunger", "integer", "Min hunger cap", false);
        add("feed-max-hunger", "integer", "Max hunger cap", false);
        add("blocked-cmds", "set", "Blocked commands", false);
        add("allowed-cmds", "set", "Command whitelist", false);
        add("deny-spawn", "set", "Entity types that cannot spawn", false);
        add("spawn", "location", "Region spawn location", false);
        add("teleport", "location", "Region teleport location", false);
    }

    private static void add(String name, String type, String desc, boolean state) {
        CATALOG.add(new Entry(name, type, desc, state));
    }

    /** All catalog entries that actually exist in this WorldGuard's registry. */
    public static List<Entry> available() {
        List<Entry> out = new ArrayList<>();
        for (Entry e : CATALOG) {
            Flag<?> f = WorldGuard.getInstance().getFlagRegistry().get(e.name());
            if (f != null) out.add(e);
        }
        return out;
    }

    public static boolean isKnown(String name) {
        return WorldGuard.getInstance().getFlagRegistry().get(name.toLowerCase()) != null;
    }

    /** Suggested values for tab-complete of a flag value. */
    public static List<String> suggestedValues(String flagName) {
        List<String> out = new ArrayList<>();
        Flag<?> f = WorldGuard.getInstance().getFlagRegistry().get(flagName.toLowerCase());
        if (f instanceof StateFlag) {
            out.add("allow");
            out.add("deny");
            out.add("none");
        } else if (f != null) {
            switch (flagName.toLowerCase()) {
                case "weather-lock" -> {
                    out.add("clear");
                    out.add("rain");
                }
                case "game-mode" -> {
                    out.add("survival");
                    out.add("creative");
                    out.add("adventure");
                }
                case "notify-enter", "notify-leave", "exit-override" -> {
                    out.add("true");
                    out.add("false");
                }
                default -> out.add("<value>");
            }
        }
        return out;
    }

    /** Map of name -> StateFlag for GUI toggling. */
    public static Map<String, StateFlag> stateFlags() {
        Map<String, StateFlag> out = new LinkedHashMap<>();
        var registry = WorldGuard.getInstance().getFlagRegistry();
        for (Entry e : CATALOG) {
            if (e.stateFlag()) {
                Flag<?> f = registry.get(e.name());
                if (f instanceof StateFlag sf) out.put(e.name(), sf);
            }
        }
        return out;
    }

    public static List<Entry> catalog() {
        return Collections.unmodifiableList(CATALOG);
    }
}
