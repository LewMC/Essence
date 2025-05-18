package net.lewmc.essence.commands.teleportation.tp;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
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
        if (command.getName().equalsIgnoreCase("tpahere")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("tpahere")) { return cmd.disabled(); }
            if (cmd.console(cs)) { return new LogUtil(this.plugin).noConsole(); }

            PermissionHandler perms = new PermissionHandler(this.plugin, cs);

            if (perms.has("essence.teleport.request.here")) {
                MessageUtil msg = new MessageUtil(this.plugin, cs);
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

                    FileUtil playerData = new FileUtil(this.plugin);
                    playerData.load(playerData.playerDataFile(playerToRequest));
                    if (!playerData.getBoolean("user.accepting-teleport-requests")) {
                        msg.send("teleport", "requestsdisabled", new String[] { playerToRequest.getName() });
                        return true;
                    }
                    playerData.close();

                    TeleportRequestUtil tpru = new TeleportRequestUtil(this.plugin);
                    tpru.createRequest(cs.getName(), playerToRequest.getName(), true);

                    msg.send("teleport", "requestsent");
                    msg.sendTo(playerToRequest, "teleport", "requestedhere", new String[] { cs.getName() });
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