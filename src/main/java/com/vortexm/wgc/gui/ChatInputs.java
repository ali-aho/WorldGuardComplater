package com.vortexm.wgc.gui;

import com.vortexm.wgc.WorldGuardComplater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Chat input for GUI actions: when a menu asks for a value (string flag,
 * number, region name...), the player types it in chat instead of a sign GUI.
 *
 * Type "cancel" (configurable) to abort. Bypasses command-blocked plugins by
 * using the lowest-priority MONITOR-style handler that cancels the event so
 * the message never reaches chat.
 */
public final class ChatInputs implements Listener {

    /** A pending chat prompt. */
    public record Prompt(String promptKey, String[] promptArgs, Consumer<String> onValue) {
    }

    private final WorldGuardComplater plugin;
    private final Map<UUID, Prompt> pending = new ConcurrentHashMap<>();

    public ChatInputs(WorldGuardComplater plugin) {
        this.plugin = plugin;
    }

    /** Ask the player for input; cancels any previous pending prompt. */
    public void ask(Player p, String promptKey, String[] promptArgs, Consumer<String> onValue) {
        pending.put(p.getUniqueId(), new Prompt(promptKey, promptArgs, onValue));
        p.closeInventory();
        var lang = plugin.lang();
        p.sendMessage(lang.prefix() + lang.fmt(promptKey, (Object[]) promptArgs));
        p.sendMessage(lang.prefix() + lang.fmt("chat-input-cancel-hint"));
    }

    public boolean isPending(Player p) {
        return pending.containsKey(p.getUniqueId());
    }

    public void clear(Player p) {
        pending.remove(p.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Prompt prompt = pending.remove(e.getPlayer().getUniqueId());
        if (prompt == null) {
            return;
        }
        e.setCancelled(true); // input never shows in public chat
        String message = e.getMessage();
        Bukkit.getScheduler().runTask(plugin, () -> {
            var lang = plugin.lang();
            String cancelWord = plugin.getConfig().getString("chat-input.cancel-word", "cancel");
            if (message.equalsIgnoreCase(cancelWord)) {
                e.getPlayer().sendMessage(lang.prefix() + lang.fmt("chat-input-cancelled"));
                return;
            }
            if (message.isEmpty()) {
                e.getPlayer().sendMessage(lang.prefix() + lang.fmt("chat-input-empty"));
                return;
            }
            prompt.onValue().accept(message);
        });
    }

    /** Drop prompts for players that leave (called from the main listener). */
    public void handleQuit(UUID id) {
        pending.remove(id);
    }
}
