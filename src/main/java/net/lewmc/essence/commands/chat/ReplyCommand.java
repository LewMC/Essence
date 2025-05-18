package net.lewmc.essence.commands.chat;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.CommandUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.PermissionHandler;
import net.lewmc.essence.utils.placeholders.PlaceholderUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ReplyCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the ReplyCommand class.
     * @param plugin References to the main plugin class.
     */
    public ReplyCommand(Essence plugin) {
        this.plugin = plugin;
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
    public boolean onCommand(
        @NotNull CommandSender cs,
        @NotNull Command command,
        @NotNull String s,
        String[] args
    ) {
        if (command.getName().equalsIgnoreCase("reply")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("reply")) { return cmd.disabled(); }

            PermissionHandler permission = new PermissionHandler(this.plugin, cs);
            if (!permission.has("essence.chat.reply")) {
                return permission.not();
            }

            MessageUtil message = new MessageUtil(this.plugin, cs);

            if (args.length > 0) {
                if (this.plugin.msgHistory.containsKey(cs)) {
                    CommandSender p = this.plugin.msgHistory.get(cs);

                    String msg = String.join(" ", Arrays.copyOfRange(args, 0, args.length));

                    msg = new PlaceholderUtil(this.plugin, cs).replaceAll(msg);

                    String[] repl = new String[]{cs.getName(), p.getName(), msg};
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

        return false;
    }
}
