package com.vortexm.wgc.util;

import com.vortexm.wgc.WorldGuardComplater;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/** Loads i18n/en.yml or i18n/fa.yml and formats messages. */
public final class Lang {

    private final WorldGuardComplater plugin;
    private YamlConfiguration messages;
    private String prefix = "&8[&3WGC&8] &7";

    public Lang(WorldGuardComplater plugin) {
        this.plugin = plugin;
    }

    public void load() {
        String code = plugin.getConfig().getString("language", "en").toLowerCase();
        if (!code.equals("en") && !code.equals("fa")) code = "en";

        // Export built-in language files so servers can edit them on disk.
        for (String name : new String[]{"i18n/en.yml", "i18n/fa.yml"}) {
            try {
                if (plugin.getResource(name) != null && !new File(plugin.getDataFolder(), name).exists()) {
                    plugin.saveResource(name, false);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }

        File file = new File(plugin.getDataFolder(), "i18n/" + code + ".yml");
        if (!file.exists()) {
            file = new File(plugin.getDataFolder(), "i18n/en.yml");
        }
        messages = YamlConfiguration.loadConfiguration(file);
        if (messages.getKeys(false).isEmpty() && plugin.getResource("i18n/" + code + ".yml") != null) {
            // fall back to jar copy
            var is = plugin.getResource("i18n/" + code + ".yml");
            messages = YamlConfiguration.loadConfiguration(new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8));
        }
        String cfgPrefix = plugin.getConfig().getString("prefix");
        prefix = cfgPrefix != null ? cfgPrefix : messages.getString("prefix", "&8[&3WGC&8] &7");
    }

    /** Raw message with color translation and {0},{1}... placeholder filling. */
    public String fmt(String key, Object... repl) {
        String s = messages != null ? messages.getString(key, key) : key;
        for (int i = 0; i < repl.length; i++) {
            s = s.replace("{" + i + "}", String.valueOf(repl[i]));
        }
        return color(s);
    }

    /** Like fmt but prefixed (for chat lines). */
    public String msg(String key, Object... repl) {
        return color(prefix) + fmt(key, repl);
    }

    public void send(CommandSender to, String key, Object... repl) {
        to.sendMessage(msg(key, repl));
    }

    public String prefix() {
        return color(prefix);
    }

    public static String color(String s) {
        return s == null ? "" : ChatColor.translateAlternateColorCodes('&', s);
    }
}
