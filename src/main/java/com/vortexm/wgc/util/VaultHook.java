package com.vortexm.wgc.util;

import com.vortexm.wgc.WorldGuardComplater;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Optional Vault economy hook. If Vault or an economy plugin is absent,
 * everything degrades to free claims.
 */
public final class VaultHook {

    private final WorldGuardComplater plugin;
    private Economy economy;

    public VaultHook(WorldGuardComplater claim) {
        this.plugin = claim;
    }

    /** Try to hook the economy; safe to call multiple times. */
    public void setup() {
        economy = null;
        if (!plugin.getConfig().getBoolean("economy.enabled", true)) return;
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) return;
        if (plugin.getServer().getPluginManager().getPlugin("WorldGuard") == null) return;
        RegisteredServiceProvider<Economy> rsp =
                plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        }
    }

    public boolean hasEconomy() {
        return economy != null;
    }

    public Economy economy() {
        return economy;
    }
}
