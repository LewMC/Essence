package net.lewmc.essence.teleportation.tp;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /tpahere command.
 */
public class CommandTpahere extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the TpahereCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandTpahere(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permissions.
     * @return String - The permissions string
     */
    @Override
    protected String requiredPermission() {
        return "essence.teleport.request.here";
    }

    /**
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean  true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
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

            new UtilTeleportRequest(this.plugin).createRequest(cs.getName(), playerToRequest.getName(), true);

            msg.send("teleport", "requestsent");
            msg.sendTo(playerToRequest, "teleport", "requestedhere", new String[] { cs.getName() });
            msg.sendTo(playerToRequest, "teleport", "acceptdeny");
        }
        return true;
    }
}