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

        // any click while a managed menu is on screen: never let vanilla move items
        e.setCancelled(true);
        if (e.getAction() != org.bukkit.event.inventory.InventoryAction.NOTHING
                && e.getView().getBottomInventory().equals(e.getClickedInventory())) {
            return; // clicked own inventory - blocked, no menu action
        }
        int raw = e.getRawSlot();
        if (raw < 0 || raw >= top.getSize()) return;
        menu.handler().onClick(p, e);
    }

    @EventHandler
    public void onDrag(org.bukkit.event.inventory.InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        GuiManager.Menu menu = plugin.gui().menuOf(p);
        if (menu == null) return;
        // block drags that touch the managed inventory
        Inventory top = e.getView().getTopInventory();
        for (int raw : e.getRawSlots()) {
            if (raw < top.getSize()) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player p) {
            plugin.gui().forgetIf(p, e.getInventory());
        }
    }

    @EventHandler
    public void onQuit(org.bukkit.event.player.PlayerQuitEvent e) {
        plugin.chatInputs().handleQuit(e.getPlayer().getUniqueId());
    }
}
