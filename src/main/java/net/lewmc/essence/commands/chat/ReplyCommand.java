package net.lewmc.essence.commands.chat;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.PermissionHandler;
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
     * @param commandSender Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
        @NotNull CommandSender commandSender,
        @NotNull Command command,
        @NotNull String s,
        String[] args
    ) {
        if (command.getName().equalsIgnoreCase("reply")) {
            MessageUtil message = new MessageUtil(commandSender, plugin);
            PermissionHandler permission = new PermissionHandler(commandSender, message);
            if (!permission.has("essence.chat.reply")) {
                return permission.not();
            }

            if (args.length > 0) {
                if (this.plugin.msgHistory.containsKey(commandSender)) {
                    CommandSender p = this.plugin.msgHistory.get(commandSender);

                    String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    String[] repl = new String[]{commandSender.getName(), p.getName(), msg};
                    message.send("msg", "send", repl);
                    message.sendTo(p, "msg", "send", repl);

                    if (this.plugin.msgHistory.containsKey(p)) {
                        this.plugin.msgHistory.replace(p, commandSender);
                    } else {
                        this.plugin.msgHistory.put(p, commandSender);
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
