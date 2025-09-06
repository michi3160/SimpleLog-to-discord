package com.yourdomain.simplelogtodiscord;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.util.Map;

public class LogAppender extends AbstractAppender {

    private static DiscordManager discordManager;

    public LogAppender() {
        super("DiscordWarnAppender", null,
              PatternLayout.createDefaultLayout(), false, null);
    }

    public static void setDiscordManager(DiscordManager manager) {
        discordManager = manager;
    }

    @Override
    public void append(LogEvent event) {
        if (discordManager != null && discordManager.isBotRunning() && event.getLevel() == Level.WARN) {
            String message = event.getMessage().getFormattedMessage();
            // To avoid feedback loops from JDA's own warnings
            if (message.contains("net.dv8tion.jda")) {
                return;
            }
            discordManager.sendEmbed("warn", "warn", Map.of("%message%", message));
        }
    }
}