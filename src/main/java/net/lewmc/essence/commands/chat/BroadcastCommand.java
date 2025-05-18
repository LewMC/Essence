package net.lewmc.essence.commands.chat;

import net.lewmc.essence.utils.CommandUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.PermissionHandler;
import net.lewmc.essence.utils.placeholders.PlaceholderUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
        if (command.getName().equalsIgnoreCase("broadcast")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("broadcast")) { return cmd.disabled(); }

            PermissionHandler permission = new PermissionHandler(this.plugin, cs);
            if (!permission.has("essence.chat.broadcast")) { return permission.not(); }

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

        return false;
    }
}
