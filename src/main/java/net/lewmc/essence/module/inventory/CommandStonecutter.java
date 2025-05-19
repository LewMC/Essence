package net.lewmc.essence.module.inventory;

import net.lewmc.essence.Essence;
import net.lewmc.essence.global.UtilCommand;
import net.lewmc.essence.global.UtilPermission;
import net.lewmc.foundry.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /stonecutter command.
 */
public class CommandStonecutter implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the StonecutterCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandStonecutter(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
        @NotNull CommandSender cs,
        @NotNull Command command,
        @NotNull String s,
        String[] args
    ) {
        if (command.getName().equalsIgnoreCase("stonecutter")) {
            if (cs instanceof Player p) {
                UtilCommand cmd = new UtilCommand(this.plugin, cs);
                if (cmd.isDisabled("stonecutter")) {return cmd.disabled();}

                UtilPermission permission = new UtilPermission(this.plugin, cs);
                if (permission.has("essence.inventory.stonecutter")) {
                    p.openStonecutter(null, true);
                    return true;
                } else {
                    return permission.not();
                }
            } else {
                return new Logger(this.plugin.config).noConsole();
            }
        }

        return false;
    }
}