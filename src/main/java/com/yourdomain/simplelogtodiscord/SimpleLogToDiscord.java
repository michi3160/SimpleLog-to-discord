package com.yourdomain.simplelogtodiscord;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.List;

public class SimpleLogToDiscord extends JavaPlugin implements CommandExecutor, TabCompleter {

    private DiscordManager discordManager;
    private LogAppender logAppender;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String token = getConfig().getString("bot-token");
        if (token == null || token.isEmpty() || token.equals("YOUR_BOT_TOKEN_HERE")) {
            getLogger().severe("Discord Bot Token is not set in config.yml! Disabling the plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        discordManager = new DiscordManager(this);
        discordManager.startBot(token, true);

        // Start listening to console logs
        logAppender = new LogAppender();
        LogAppender.setDiscordManager(discordManager);
        logAppender.start();
        ((Logger) LogManager.getRootLogger()).addAppender(logAppender);

        getServer().getPluginManager().registerEvents(new MinecraftEventListeners(discordManager), this);
        getCommand("simplelogtodiscord").setExecutor(this);
        getCommand("simplelogtodiscord").setTabCompleter(this);

        getLogger().info("SimpleLogToDiscord has been enabled.");
    }

    @Override
    public void onDisable() {
        if (discordManager != null && discordManager.isBotRunning()) {
            discordManager.sendServerStatusMessage(false);
            discordManager.stopBot();
        }
        // Stop listening to console logs to prevent memory leaks
        if (logAppender != null) {
            ((Logger) LogManager.getRootLogger()).removeAppender(logAppender);
            logAppender.stop();
        }
        getLogger().info("SimpleLogToDiscord has been disabled.");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("simplelogtodiscord.reload")) {
                sender.sendMessage("§cYou do not have permission to execute this command.");
                return true;
            }
            reloadConfig();
            if (discordManager != null) {
                discordManager.stopBot();
                String newToken = getConfig().getString("bot-token");
                discordManager.startBot(newToken, false);
            }
            sender.sendMessage("§aSimpleLogToDiscord configuration has been reloaded.");
            return true;
        }
        sender.sendMessage("§cUsage: /" + label + " reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("simplelogtodiscord.reload")) {
            if ("reload".startsWith(args[0].toLowerCase())) {
                return Collections.singletonList("reload");
            }
        }
        return Collections.emptyList();
    }
}