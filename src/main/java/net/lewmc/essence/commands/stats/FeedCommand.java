package net.lewmc.essence.commands.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.PermissionHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class FeedCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the FeedCommand class.
     * @param plugin References to the main plugin class.
     */
    public FeedCommand(Essence plugin) {
        this.plugin = plugin;
    }

    /**
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
        @NotNull String s, String[] args
    ) {
        MessageUtil message = new MessageUtil(commandSender, this.plugin);
        if (!(commandSender instanceof Player) && args.length == 0) {
            message.send("feed","usage");
            return true;
        }
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("feed")) {
            if (args.length > 0) {
                return this.feedOther(permission, commandSender, message, args);
            } else {
               return this.feedSelf(permission, commandSender, message);
            }
        }

        return true;
    }

    /**
     * Feeds the command sender.
     * @param permission PermisionHandler - The permission system.
     * @param sender CommandSender - The user to feed.
     * @param message MessageUtil - The messaging system.
     * @return boolean - If the operation was successful
     */
    private boolean feedSelf(PermissionHandler permission, CommandSender sender, MessageUtil message) {
        if (permission.has("essence.stats.feed")) {
            Player player = (Player) sender;
            player.setFoodLevel(20);
            message.send("feed", "beenfed");
            return true;
        } else {
            return permission.not();
        }
    }

    /**
     * Feeds another user.
     * @param permission PermisionHandler - The permission system.
     * @param sender CommandSender - The user to feed.
     * @param message MessageUtil - The messaging system.
     * @param args String[] - List of command arguments.
     * @return boolean - If the operation was successful
     */
    private boolean feedOther(PermissionHandler permission, CommandSender sender, MessageUtil message, String[] args) {
        if (permission.has("essence.stats.feed.other")) {
            String pName = args[0];
            Player p = Bukkit.getPlayer(pName);
            if (p != null) {
                message.send("feed", "fed", new String[] { p.getName() });
                if (!(sender instanceof Player)) {
                    message.sendTo(p, "feed", "serverfed");
                } else {
                    message.sendTo(p, "feed", "fedby", new String[] { sender.getName() });
                }
                p.setFoodLevel(20);
            } else {
                message.send("generic", "playernotfound");
            }
            return true;
        } else {
            return permission.not();
        }
    }
}