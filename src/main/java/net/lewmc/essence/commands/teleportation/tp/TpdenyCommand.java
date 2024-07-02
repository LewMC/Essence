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
public class TpdenyCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the TpdenyCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public TpdenyCommand(Essence plugin) {
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

        if (command.getName().equalsIgnoreCase("tpdeny")) {
            if (permission.has("essence.teleport.request.deny")) {
                TeleportRequestUtil tpru = new TeleportRequestUtil(this.plugin);
                tpru.deleteFromRequested(commandSender.getName());
                msg.send("teleport","canceldone");
                return true;
            } else {
                return permission.not();
            }
        }
        return false;
    }
}