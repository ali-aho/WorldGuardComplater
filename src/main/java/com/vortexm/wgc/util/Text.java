package com.vortexm.wgc.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/** Small GUI/chat helpers. */
public final class Text {

    private Text() {
    }

    public static ItemStack item(Material m, String name, List<String> lore) {
        ItemStack is = new ItemStack(m);
        ItemMeta meta = is.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Lang.color(name));
            if (lore != null) {
                List<String> colored = new ArrayList<>();
                for (String l : lore) colored.add(Lang.color(l));
                meta.setLore(colored);
            }
            is.setItemMeta(meta);
        }
        return is;
    }

    public static String human(double d) {
        if (d == Math.floor(d)) return String.valueOf((long) d);
        return String.valueOf(d);
    }
}
