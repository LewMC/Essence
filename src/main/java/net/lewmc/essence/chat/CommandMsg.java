package net.lewmc.essence.chat;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPlaceholder;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CommandMsg extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the MsgCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandMsg(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.chat.msg";
    }

    /**
     * /msg command handler.
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("msg")) { return cmd.disabled(); }

        UtilMessage message = new UtilMessage(plugin, cs);

        if (args.length > 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if ((p.getName().toLowerCase()).equalsIgnoreCase(args[0])) {
                    String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                    msg = new UtilPlaceholder(this.plugin, cs).replaceAll(msg);

                    String[] repl = new String[] {cs.getName(), p.getName(), msg};

                    message.send("msg", "send", repl);
                    message.sendTo(p, "msg", "send", repl);

                    if (this.plugin.msgHistory.containsKey(p)) {
                        this.plugin.msgHistory.replace(p, cs);
                    } else {
                        this.plugin.msgHistory.put(p, cs);
                    }

                    return true;
                }
            }
            message.send("generic", "playernotfound");
        } else {
            message.send("msg","usage");
        }

        return true;
    }
}
