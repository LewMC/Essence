package net.lewmc.essence.teleportation.tp;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * /tpaccept command.
 */
public class CommandTpaccept extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the TpacceptCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandTpaccept(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.teleport.request.accept";
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
        if (!new UtilTeleportRequest(plugin).acceptRequest(cs.getName())) {
            new UtilMessage(this.plugin, cs).send("teleport", "acceptnone");
        }
        return true;
    }
}