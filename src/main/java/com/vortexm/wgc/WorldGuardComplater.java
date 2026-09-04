package com.vortexm.wgc;

import com.vortexm.wgc.command.WgcCommand;
import com.vortexm.wgc.gui.GuiListener;
import com.vortexm.wgc.gui.GuiManager;
import com.vortexm.wgc.util.Lang;
import com.vortexm.wgc.util.VaultHook;
import com.vortexm.wgc.util.WgBridge;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * WorldGuard Complater - WorldGuard, but completer.
 * Better tab-complete, GUI, help, claiming and more.
 *
 * Author: Vortex_Miner1 (VortexM) - MIT License
 */
public final class WorldGuardComplater extends JavaPlugin {

    private static WorldGuardComplater instance;

    private Lang lang;
    private GuiManager gui;
    private VaultHook vault;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        this.lang = new Lang(this);
        this.lang.load();

        // WorldGuard is required - the whole plugin builds on top of it.
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            getLogger().severe("WorldGuard was not found! WorldGuard Complater needs WorldGuard 7.x to work. Disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.gui = new GuiManager(this);

        // Vault (optional)
        this.vault = new VaultHook(this);
        this.vault.setup();

        WgcCommand cmd = new WgcCommand(this);
        PluginCommand pluginCommand = getCommand("wgc");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(cmd);
            pluginCommand.setTabCompleter(cmd);
        }

        Bukkit.getPluginManager().registerEvents(new GuiListener(this), this);

        getLogger().info("WorldGuard Complater " + getDescription().getVersion() + " enabled. VortexM!");
    }

    @Override
    public void onDisable() {
        getLogger().info("WorldGuard Complater disabled.");
    }

    public void reloadAll() {
        reloadConfig();
        lang.load();
        vault.setup();
    }

    public static WorldGuardComplater get() {
        return instance;
    }

    public Lang lang() {
        return lang;
    }

    public GuiManager gui() {
        return gui;
    }

    public VaultHook vault() {
        return vault;
    }
}
