# SimpleLogToDiscord
SimpleLogToDiscord is a powerful all-in-one utility that seamlessly integrates your Minecraft server with your Discord community. Going beyond basic chat logging, it provides server administrators with real-time monitoring and a comprehensive suite of tools for community engagement. All of this is displayed through a clean, highly customizable Discord embed.

## Core Features
- **Real-time Event Logging**
> Instantly get notified in Discord about crucial server and player activities.

- **Highly Customizable Embeds**
> Control the look and feel of every message with an easy-to-use config.yml.

- **Two-Way Communication**
> Allow your Discord and in-game communities to chat with each other.

- **Secure Remote Console**
> Execute server commands directly from Discord, protected by a role-based permission system.

- **Automatic Server Health Monitoring**
> Capture and send critical server warnings (e.g., "Can't keep up!") to a dedicated channel.

- **Lightweight and Reliable**
> Designed to be efficient and stable, ensuring minimal impact on server performance.

## Detailed Functionality
### Comprehensive Event Logging
Stay informed about everything happening on your server. The plugin automatically logs the following events to designated Discord channels:

- **Server Status**
> Notifications for server start and stop.

- **Player Activity**
> Player joins and leaves.

- **In-Game Chat**
> Every player message is relayed to Discord.

- **Player Deaths**
> Detailed death messages including the reason, player name, and coordinates.

- **Commands & Actions**
> Logs commands used by players and changes to their game mode.

### Interactive Discord Bridge
This is more than just a logger. The plugin facilitates true two-way interaction:

- **Discord-to-Minecraft Chat**
> Messages sent in your designated Discord chat channel will be broadcast to all players in-game, prefixed with [Discord].

- **Remote Console Commands**
> By assigning an "Admin Role" in the config, trusted users can execute any console command directly from Discord using /console <command>. This is a powerful tool for remote server management.

- **Proactive Server Monitoring**
> Don't wait for players to report lag or issues. SimpleLogToDiscord automatically captures any [WARN] level messages from your server console—such as the critical "Can't keep up! Is the server overloaded?" message—and sends them directly to a warn channel in Discord, allowing you to address performance issues immediately.

### Complete Customization
Tailor every aspect of the plugin to match your server's theme and needs through the config.yml file. You can:

- Set a specific destination channel for each type of log (chat, death, warn, etc.).

- Enable or disable any embed message individually.

- Customize the color, title, and description text for every single notification.

- Enable or disable the Discord-to-Minecraft chat bridge.
