package com.vortexm.wgc.gui;

import com.vortexm.wgc.WorldGuardComplater;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/** Routes clicks in managed inventories to their menu handler. */
public final class GuiListener implements Listener {

    private final WorldGuardComplater plugin;

    public GuiListener(WorldGuardComplater plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        GuiManager.Menu menu = plugin.gui().menuOf(p);
        if (menu == null) return;
        Inventory top = e.getView().getTopInventory();
        if (!top.equals(menu.inventory())) return;

        // clicking inside the managed menu: never vanilla-handle it
        if (e.getClickedInventory() != null && e.getClickedInventory().equals(top)) {
            e.setCancelled(true);
            int raw = e.getRawSlot();
            if (raw < 0 || raw >= top.getSize()) return;
            menu.handler().onClick(p, e);
        } else {
            // shift-clicking from the player inventory would move items in; block it
            if (e.getClick().isShiftClick() || e.getClick().isKeyboardClick()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player p) {
            plugin.gui().forget(p);
        }
    }
}
