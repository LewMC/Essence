package net.lewmc.essence.teleportation.tp;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * /tpdeny command.
 */
public class CommandTpdeny extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the TpdenyCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandTpdeny(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permissions.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.teleport.request.deny";
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
        new UtilTeleportRequest(this.plugin).deleteFromRequested(cs.getName());
        new UtilMessage(this.plugin,cs).send("teleport","canceldone");
        return true;
    }
}