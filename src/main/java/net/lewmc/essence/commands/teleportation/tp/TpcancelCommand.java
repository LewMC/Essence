package net.lewmc.essence.commands.teleportation.tp;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import net.lewmc.foundry.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * /tpaccept command.
 */
public class TpcancelCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the TpcancelCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public TpcancelCommand(Essence plugin) {
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
        if (command.getName().equalsIgnoreCase("tpcancel")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("tpcancel")) { return cmd.disabled(); }
            if (cmd.console(cs)) { new Logger(this.plugin.config).noConsole(); return true; }

            PermissionHandler permission = new PermissionHandler(this.plugin, cs);

            if (permission.has("essence.teleport.request.cancel")) {
                MessageUtil msg = new MessageUtil(this.plugin, cs);
                if (new TeleportRequestUtil(this.plugin).deleteFromRequester(cs.getName())) {
                    msg.send("teleport","canceldone");
                } else {
                    msg.send("teleport","cancelnone");
                }
                return true;
            } else {
                return permission.not();
            }
        }
        return false;
    }
}