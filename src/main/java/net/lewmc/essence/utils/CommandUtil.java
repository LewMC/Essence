package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * The command utility.
 */
public class CommandUtil {
    private final Essence plugin;

    /**
     * The constructor for the CommandUtil.
     * @param plugin Reference to the main Essence class.
     */
    public CommandUtil(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks if a command is enabled.
     * @param command String - the command to check.
     * @return Boolean - if the command is enabled.
     */
    public boolean isEnabled(String command) {
        List<String> disabledCommands = this.plugin.getConfig().getStringList("disabled-commands");

        for (String key : disabledCommands) {
            if (Objects.equals(key, command)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the server is compatible with PaperMC.
     * @return boolean - If the server is paper compatible.
     */
    public boolean isPaperCompatible() {
        File pwd = new File("config/paper-world-defaults.yml");
        File g = new File("config/paper-global.yml");

        return pwd.exists() && g.exists();
    }

    /**
     * Checks if the commandSender is the console.
     * @param commandSender CommandSender
     * @return true if the command sender is the console.
     */
    public boolean console(CommandSender commandSender) {
        return !(commandSender instanceof Player);
    }
}
