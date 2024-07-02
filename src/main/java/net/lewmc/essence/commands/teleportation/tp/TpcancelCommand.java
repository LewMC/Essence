package net.lewmc.essence.commands.teleportation.tp;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
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
     * @param commandSender Information about who sent the command - player or console.
     * @param command       Information about what command was sent.
     * @param s             Command label - not used here.
     * @param args          The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            String[] args
    ) {
        MessageUtil msg = new MessageUtil(commandSender, this.plugin);
        PermissionHandler permission = new PermissionHandler(commandSender, msg);
        LogUtil log = new LogUtil(this.plugin);

        CommandUtil cmd = new CommandUtil(this.plugin);
        if (cmd.console(commandSender)) {
            log.noConsole();
            return true;
        }

        if (command.getName().equalsIgnoreCase("tpcancel")) {
            if (cmd.isDisabled("tpcancel")) {
                return cmd.disabled();
            }

            if (permission.has("essence.teleport.request.cancel")) {
                TeleportRequestUtil tpru = new TeleportRequestUtil(this.plugin);
                if (tpru.deleteFromRequester(commandSender.getName())) {
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