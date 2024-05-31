package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class CommandUtil {
    private final Essence plugin;

    public CommandUtil(Essence plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled(String command) {
        List<String> disabledCommands = this.plugin.getConfig().getStringList("disabled-commands");

        for (String key : disabledCommands) {
            if (Objects.equals(key, command)) {
                return false;
            }
        }

        return true;
    }

    public boolean isPaperCompatible() {
        File pwd = new File("config/paper-world-defaults.yml");
        File g = new File("config/paper-global.yml");

        return pwd.exists() && g.exists();
    }

    public boolean isFolia() {
        return (
                this.plugin.getServer().getName().equals("Folia") ||
                this.plugin.getServer().getName().equals("Luminol") ||
                this.plugin.getServer().getName().equals("LightningLuminol")
        );
    }
}
