package net.lewmc.essence.module.teleportation.tp;

import net.lewmc.essence.Essence;
import net.lewmc.essence.global.UtilCommand;
import net.lewmc.essence.global.UtilMessage;
import net.lewmc.essence.global.UtilPermission;
import net.lewmc.foundry.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * /tpaccept command.
 */
public class CommandTpaccept implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the TpaCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandTpaccept(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * @param cs            Information about who sent the command - player or console.
     * @param command       Information about what command was sent.
     * @param s             Command label - not used here.
     * @param args          The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
            @NotNull CommandSender cs,
            @NotNull Command command,
            @NotNull String s,
            String[] args
    ) {
        if (command.getName().equalsIgnoreCase("tpaccept")) {
            UtilCommand cmd = new UtilCommand(this.plugin, cs);
            if (cmd.isDisabled("tpaccept")) { return cmd.disabled(); }
            if (cmd.console(cs)) {
                return new Logger(this.plugin.config).noConsole();
            }

            UtilPermission permission = new UtilPermission(this.plugin, cs);

            if (permission.has("essence.teleport.request.accept")) {
                if (!new UtilTeleportRequest(plugin).acceptRequest(cs.getName())) {
                    new UtilMessage(this.plugin, cs).send("teleport", "acceptnone");
                }
                return true;
            } else {
                return permission.not();
            }
        }
        return false;
    }
}