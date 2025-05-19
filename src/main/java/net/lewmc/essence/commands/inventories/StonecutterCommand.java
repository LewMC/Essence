package net.lewmc.essence.commands.inventories;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.CommandUtil;
import net.lewmc.essence.utils.PermissionHandler;
import net.lewmc.foundry.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /stonecutter command.
 */
public class StonecutterCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the StonecutterCommand class.
     * @param plugin References to the main plugin class.
     */
    public StonecutterCommand(Essence plugin) {
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
                CommandUtil cmd = new CommandUtil(this.plugin, cs);
                if (cmd.isDisabled("stonecutter")) {return cmd.disabled();}

                PermissionHandler permission = new PermissionHandler(this.plugin, cs);
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