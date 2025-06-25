package net.lewmc.essence.chat;

import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilPlaceholder;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * /broadcast command.
 */
public class CommandBroadcast extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the BroadcastCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandBroadcast(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.chat.broadcast";
    }

    /**
     * /broadcast command handler.
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("broadcast")) { return cmd.disabled(); }

        UtilMessage msg = new UtilMessage(this.plugin, cs);

        if (args.length > 0) {
            StringBuilder broadcastMessage = new StringBuilder();
            for (String arg : args) { broadcastMessage.append(arg).append(" "); }
            msg.broadcast(new UtilPlaceholder(this.plugin, cs).replaceAll(broadcastMessage.toString()));
        } else {
            msg.send("broadcast","usage");
        }

        return true;
    }
}