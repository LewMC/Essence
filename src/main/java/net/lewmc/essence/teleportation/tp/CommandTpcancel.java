package net.lewmc.essence.teleportation.tp;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * /tpaccept command.
 */
public class CommandTpcancel extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the TpcancelCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandTpcancel(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.teleport.request.cancel";
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
        if (new UtilTeleportRequest(this.plugin).deleteFromRequester(cs.getName())) {
            msg.send("teleport", "canceldone");
        } else {
            msg.send("teleport", "cancelnone");
        }
        return true;

    }
}