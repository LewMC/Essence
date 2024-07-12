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
public class TpacceptCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the TpaCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public TpacceptCommand(Essence plugin) {
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
        MessageUtil message = new MessageUtil(commandSender, this.plugin);
        PermissionHandler permission = new PermissionHandler(commandSender, message);
        LogUtil log = new LogUtil(this.plugin);

        CommandUtil cmd = new CommandUtil(this.plugin);
        if (cmd.console(commandSender)) {
            log.noConsole();
            return true;
        }

        if (command.getName().equalsIgnoreCase("tpaccept")) {
            if (cmd.isDisabled("tpaccept")) {
                return cmd.disabled(message);
            }

            if (permission.has("essence.teleport.request.accept")) {
                TeleportRequestUtil tpru = new TeleportRequestUtil(plugin);
                if (!tpru.acceptRequest(commandSender.getName())) {
                    message.send("teleport", "acceptnone");
                }
                return true;
            } else {
                return permission.not();
            }
        }
        return false;
    }
}