package net.lewmc.essence.commands.chat;

import net.lewmc.essence.utils.CommandUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.PermissionHandler;
import net.lewmc.essence.utils.placeholders.PlaceholderUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BroadcastCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the BroadcastCommand class.
     * @param plugin References to the main plugin class.
     */
    public BroadcastCommand(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * /broadcast command handler.
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
        if (command.getName().equalsIgnoreCase("broadcast")) {
            MessageUtil message = new MessageUtil(commandSender, plugin);

            CommandUtil cmd = new CommandUtil(this.plugin);
            if (cmd.isDisabled("broadcast")) {
                return cmd.disabled(message);
            }

            PermissionHandler permission = new PermissionHandler(commandSender, message);
            if (!permission.has("essence.chat.broadcast")) {
                permission.not();
                return true;
            }

            if (args.length > 0) {
                StringBuilder broadcastMessage = new StringBuilder();
                for (String arg : args) {
                    broadcastMessage.append(arg).append(" ");
                }
                String finalBroadcastMessage = new PlaceholderUtil(this.plugin, commandSender).replaceAll(broadcastMessage.toString());
                message.broadcast(finalBroadcastMessage);
            } else {
                message.send("broadcast","usage");
            }

            return true;
        }

        return false;
    }
}
