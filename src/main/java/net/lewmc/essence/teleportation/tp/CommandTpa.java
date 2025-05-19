package net.lewmc.essence.teleportation.tp;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /tpa command.
 */
public class CommandTpa implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the TpacceptCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandTpa(Essence plugin) {
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
        if (command.getName().equalsIgnoreCase("tpa")) {
            UtilCommand cmd = new UtilCommand(this.plugin, cs);
            if (cmd.isDisabled("tpa")) { return cmd.disabled(); }
            if (cmd.console(cs)) { return new Logger(this.plugin.config).noConsole(); }

            UtilPermission perms = new UtilPermission(this.plugin, cs);

            if (perms.has("essence.teleport.request.send")) {
                UtilMessage msg = new UtilMessage(this.plugin, cs);
                if (args.length == 0) {
                    msg.send("teleport", "userrequired");
                } else {
                    Player playerToRequest = this.plugin.getServer().getPlayer(args[0]);
                    if (playerToRequest == null) {
                        msg.send("generic", "playernotfound");
                        return true;
                    }

                    if (playerToRequest.getName().equals(cs.getName())) {
                        msg.send("generic", "cantyourself");
                        return true;
                    }

                    Files playerData = new Files(this.plugin.config, this.plugin);
                    playerData.load(playerData.playerDataFile(playerToRequest));
                    if (!playerData.getBoolean("user.accepting-teleport-requests")) {
                        msg.send("teleport", "requestsdisabled", new String[] { playerToRequest.getName() });
                        return true;
                    }
                    playerData.close();

                    UtilTeleportRequest tpru = new UtilTeleportRequest(this.plugin);
                    tpru.createRequest(cs.getName(), playerToRequest.getName(), false);

                    msg.send("teleport", "requestsent");
                    msg.sendTo(playerToRequest, "teleport", "requested", new String[] { cs.getName() });
                    msg.sendTo(playerToRequest, "teleport", "acceptdeny");

                }
                return true;
            } else {
                return perms.not();
            }
        } else {
            return false;
        }
    }
}