package com.yourdomain.simplelogtodiscord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection; // ← この行が追加されました

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;

public class DiscordManager extends ListenerAdapter {

    private final SimpleLogToDiscord plugin;
    private JDA jda;

    public DiscordManager(SimpleLogToDiscord plugin) {
        this.plugin = plugin;
    }

    public void startBot(String token, boolean sendStartMessage) {
        try {
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                    .addEventListeners(this) // Listen for Discord events
                    .build().awaitReady();
            if (sendStartMessage) {
                sendServerStatusMessage(true);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to start the Discord bot.", e);
        }
    }

    public void stopBot() {
        if (jda != null) {
            jda.shutdown();
            jda = null;
        }
    }

    public boolean isBotRunning() {
        return jda != null && jda.getStatus() == JDA.Status.CONNECTED;
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String chatChannelId = plugin.getConfig().getString("channels.chat", "");
        String commandChannelId = plugin.getConfig().getString("channels.command", "");

        // --- Discord -> Minecraft Chat Bridge ---
        if (plugin.getConfig().getBoolean("bridge-discord-to-minecraft") &&
            event.getChannel().getId().equals(chatChannelId)) {
            String message = String.format("§9[Discord] §r<%s> %s",
                event.getAuthor().getName(),
                event.getMessage().getContentDisplay());
            // Run on main server thread
            Bukkit.getScheduler().runTask(plugin, () -> Bukkit.broadcastMessage(message));
        }

        // --- Discord -> Console Command Execution ---
        if (event.getChannel().getId().equals(commandChannelId)) {
            String content = event.getMessage().getContentRaw();
            if (content.startsWith("/console ")) {
                String adminRoleId = plugin.getConfig().getString("discord-admin-role-id", "");
                if (adminRoleId.isEmpty()) return;

                Member member = event.getMember();
                if (member == null) return;
                
                boolean hasRole = false;
                for (Role role : member.getRoles()) {
                    if (role.getId().equals(adminRoleId)) {
                        hasRole = true;
                        break;
                    }
                }

                if (hasRole) {
                    String command = content.substring(9);
                    plugin.getLogger().info("Executing command from Discord by " + event.getAuthor().getName() + ": " + command);
                    // Run on main server thread
                    Bukkit.getScheduler().runTask(plugin, () ->
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
                }
            }
        }
    }

    public void sendEmbed(String channelPath, String embedPath, Map<String, String> placeholders) {
        if (!isBotRunning()) return;

        ConfigurationSection embedConfig = plugin.getConfig().getConfigurationSection("embeds." + embedPath);
        if (embedConfig == null || !embedConfig.getBoolean("enabled", false)) return;

        String channelId = plugin.getConfig().getString("channels." + channelPath);
        if (channelId == null || channelId.isEmpty()) return;

        try {
            TextChannel channel = jda.getTextChannelById(channelId);
            if (channel == null) {
                plugin.getLogger().warning("Channel ID '" + channelId + "' for '" + channelPath + "' not found.");
                return;
            }

            EmbedBuilder embed = new EmbedBuilder();
            String timestamp = new SimpleDateFormat("[HH:mm:ss] ").format(new Date());

            try { embed.setColor(Color.decode(embedConfig.getString("color", "#FFFFFF"))); }
            catch (NumberFormatException e) { embed.setColor(Color.WHITE); }
            
            String title = replacePlaceholders(embedConfig.getString("title", ""), placeholders);
            if (!title.isEmpty()) embed.setTitle(timestamp + title);
            
            String description = replacePlaceholders(embedConfig.getString("description", ""), placeholders);
            if (!description.isEmpty()) {
                // If title is empty (like in chat), add timestamp to description
                embed.setDescription(title.isEmpty() ? timestamp + description : description);
            }

            String author = replacePlaceholders(embedConfig.getString("author", ""), placeholders);
            if (!author.isEmpty() && placeholders.containsKey("%player%")) {
                String playerName = placeholders.get("%player%");
                embed.setAuthor(author, null, "https://cravatar.eu/helmavatar/" + playerName + "/64.png");
            } else if (!author.isEmpty()) {
                embed.setAuthor(author);
            }

            channel.sendMessageEmbeds(embed.build()).queue();

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while sending an embed to Discord.", e);
        }
    }
    
    private String replacePlaceholders(String text, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text;
    }
    
    public void sendServerStatusMessage(boolean isStarting) {
        String embedPath = isStarting ? "server-start" : "server-stop";
        sendEmbed("server-status", embedPath, Map.of());
    }
}