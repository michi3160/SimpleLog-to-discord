package com.yourdomain.simplelogtodiscord;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.Map;

public class MinecraftEventListeners implements Listener {

    private final DiscordManager discordManager;

    public MinecraftEventListeners(DiscordManager discordManager) {
        this.discordManager = discordManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Map<String, String> placeholders = Map.of("%player%", event.getPlayer().getName());
        discordManager.sendEmbed("join-left", "player-join", placeholders);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Map<String, String> placeholders = Map.of("%player%", event.getPlayer().getName());
        discordManager.sendEmbed("join-left", "player-left", placeholders);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        Map<String, String> placeholders = Map.of(
            "%player%", event.getPlayer().getName(),
            "%message%", event.getMessage()
        );
        discordManager.sendEmbed("chat", "chat", placeholders);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) return;
        Map<String, String> placeholders = Map.of(
            "%player%", event.getPlayer().getName(),
            "%command%", event.getMessage()
        );
        discordManager.sendEmbed("command", "command", placeholders);
    }
}