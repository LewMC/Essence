package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.Bukkit;

public class LogUtil {
    private final Essence plugin;
    public LogUtil(Essence plugin) {
        this.plugin = plugin;
    }
    public void info(String message) {
        Bukkit.getLogger().info("[" + this.plugin.getConfig().get("console-prefix") + "] " + message);
    }
    public void warn(String message) {
        Bukkit.getLogger().warning("[" + this.plugin.getConfig().get("console-prefix") + "] " + message);
    }
    public void severe(String message) {
        Bukkit.getLogger().severe("[" + this.plugin.getConfig().get("console-prefix") + "] " + message);
    }

    public void noConsole() {
        this.info("Sorry, you need to be an in-game player to use this command.");
    }
}
