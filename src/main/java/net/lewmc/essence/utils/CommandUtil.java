package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
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
     * Checks if a command is disabled.
     * @param command String - the command to check.
     * @return Boolean - if the command is enabled.
     */
    public boolean isDisabled(String command) {
        for (String key : this.plugin.disabledCommands) {
            if (Objects.equals(key, command)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Responds to disabled command usage.
     * @return boolean - Verbose mode (false) or not (true)
     */
    public boolean disabled() {
        if (this.plugin.verbose) {
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Attempted to execute disabled command.");
            return false;
        } else {
            return true;
        }
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
