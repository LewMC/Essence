package net.lewmc.essence.commands.chat;

import net.lewmc.essence.utils.CommandUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.placeholders.PlaceholderUtil;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BroadcastCommand extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the BroadcastCommand class.
     * @param plugin References to the main plugin class.
     */
    public BroadcastCommand(Essence plugin) {
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
        CommandUtil cmd = new CommandUtil(this.plugin, cs);
        if (cmd.isDisabled("broadcast")) { return cmd.disabled(); }

        MessageUtil msg = new MessageUtil(this.plugin, cs);

        if (args.length > 0) {
            StringBuilder broadcastMessage = new StringBuilder();
            for (String arg : args) { broadcastMessage.append(arg).append(" "); }
            msg.broadcast(new PlaceholderUtil(this.plugin, cs).replaceAll(broadcastMessage.toString()));
        } else {
            msg.send("broadcast","usage");
        }

        return true;
    }
}