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
public class TpaCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the TpacceptCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public TpaCommand(Essence plugin) {
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

        if (command.getName().equalsIgnoreCase("tpa")) {
            if (permission.has("essence.teleport.request.send")) {
                if (args.length == 0) {
                    message.send("teleport", "userrequired");
                } else {
                    Player playerToRequest = this.plugin.getServer().getPlayer(args[0]);
                    if (playerToRequest == null) {
                        message.send("generic", "playernotfound");
                        return true;
                    }

                    if (playerToRequest.getName().equals(commandSender.getName())) {
                        message.send("generic", "cantyourself");
                        return true;
                    }

                    FileUtil playerData = new FileUtil(this.plugin);
                    playerData.load(playerData.playerDataFile(playerToRequest));
                    if (!playerData.getBoolean("user.accepting-teleport-requests")) {
                        message.send("teleport", "requestsdisabled", new String[] { playerToRequest.getName() });
                        return true;
                    }
                    playerData.close();

                    TeleportRequestUtil tpru = new TeleportRequestUtil(this.plugin);
                    tpru.createRequest(commandSender.getName(), playerToRequest.getName(), false);

                    message.send("teleport", "requestsent");
                    message.sendTo(playerToRequest, "teleport", "requested", new String[] { commandSender.getName() });
                    message.sendTo(playerToRequest, "teleport", "acceptdeny");

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