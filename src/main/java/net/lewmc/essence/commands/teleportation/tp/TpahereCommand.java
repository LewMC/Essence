package net.lewmc.essence.commands.teleportation.tp;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.PermissionHandler;
import net.lewmc.essence.utils.TeleportRequestUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /tpa command.
 */
public class TpahereCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the TpahereCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public TpahereCommand(Essence plugin) {
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

        if (command.getName().equalsIgnoreCase("tpahere")) {
            if (permission.has("essence.teleport.request.here")) {
                if (args.length == 0) {
                    message.PrivateMessage("teleport", "userrequired");
                } else {
                    Player playerToRequest = this.plugin.getServer().getPlayer(args[0]);
                    if (playerToRequest == null) {
                        message.PrivateMessage("generic", "playernotfound");
                        return true;
                    }

                    if (playerToRequest.getName().equals(commandSender.getName())) {
                        message.PrivateMessage("generic", "cantyourself");
                        return true;
                    }

                    TeleportRequestUtil tpru = new TeleportRequestUtil(this.plugin);
                    tpru.createRequest(commandSender.getName(), playerToRequest.getName(), true);

                    message.PrivateMessage("teleport", "requestsent");
                    message.SendTo(playerToRequest, "teleport", "requestedhere", commandSender.getName());
                    message.SendTo(playerToRequest, "teleport", "acceptdeny");

                }
                return true;
            } else {
                return permission.not();
            }
        } else {
            return false;
        }
    }
}