package com.vortexm.wgc.gui;

import com.vortexm.wgc.WorldGuardComplater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * GUI manager: one map of open inventories keyed by player, with a
 * per-inventory click handler. Menus register themselves here.
 */
public final class GuiManager {

    /** Handler for a click inside a managed GUI. */
    public interface ClickHandler {
        void onClick(Player p, InventoryClickEvent e);
    }

    /** A managed menu instance. */
    public record Menu(Inventory inventory, ClickHandler handler) {
    }

    private final WorldGuardComplater plugin;
    private final Map<UUID, Menu> open = new HashMap<>();

    public GuiManager(WorldGuardComplater plugin) {
        this.plugin = plugin;
    }

    public void open(Player p, Inventory inv, ClickHandler handler) {
        open.put(p.getUniqueId(), new Menu(inv, handler));
        // Defer the actual open by one tick: opening an inventory while a click
        // event is being processed causes client desync and event reentrancy.
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!p.isOnline()) {
                open.remove(p.getUniqueId());
                return;
            }
            p.openInventory(inv);
        });
    }

    /**
     * Forget the menu ONLY if the closed inventory is still the registered one.
     * When we swap menus (list -> panel -> flags...) the old inventory's close
     * event fires while the new menu is already registered - a plain forget()
     * would unregister the NEW menu and leave its items grabbable.
     */
    public void forgetIf(Player p, Inventory closed) {
        Menu m = open.get(p.getUniqueId());
        if (m != null && m.inventory().equals(closed)) {
            open.remove(p.getUniqueId());
        }
    }

    public boolean isManaged(Inventory inv) {
        for (Menu m : open.values()) {
            if (m.inventory().equals(inv)) return true;
        }
        return false;
    }

    public Menu menuOf(Player p) {
        return open.get(p.getUniqueId());
    }

    public void forget(Player p) {
        open.remove(p.getUniqueId());
    }

    public void closeAll() {
        for (UUID id : open.keySet()) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) p.closeInventory();
        }
        open.clear();
    }

    public WorldGuardComplater plugin() {
        return plugin;
    }
}
