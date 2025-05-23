package net.lewmc.essence.chat;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPlaceholder;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CommandReply extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the ReplyCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandReply(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.chat.reply";
    }

    /**
     * /reply command handler.
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("reply")) { return cmd.disabled(); }


        UtilMessage message = new UtilMessage(this.plugin, cs);

        if (args.length > 0) {
            if (this.plugin.msgHistory.containsKey(cs)) {
                CommandSender p = this.plugin.msgHistory.get(cs);

                String msg = String.join(" ", Arrays.copyOfRange(args, 0, args.length));

                String[] repl = new String[]{cs.getName(), p.getName(), new UtilPlaceholder(this.plugin, cs).replaceAll(msg)};
                message.send("msg", "send", repl);
                message.sendTo(p, "msg", "send", repl);

                if (this.plugin.msgHistory.containsKey(p)) {
                    this.plugin.msgHistory.replace(p, cs);
                } else {
                    this.plugin.msgHistory.put(p, cs);
                }
            } else {
                message.send("reply", "none");
            }

            return true;
        } else {
            message.send("reply","usage");
        }

        return true;
    }
}