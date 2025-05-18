package net.lewmc.essence.commands.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.CommandUtil;
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

        if (command.getName().equalsIgnoreCase("feed")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("feed")) { return cmd.disabled(); }

            MessageUtil message = new MessageUtil(this.plugin, cs);

            PermissionHandler permission = new PermissionHandler(this.plugin, cs);

            if (args.length > 0) {
                return this.feedOther(permission, cs, message, args);
            } else {
                if (!(cs instanceof Player)) {
                    message.send("feed","usage");
                    return true;
                } else {
                    return this.feedSelf(permission, (Player) cs, message);
                }
            }
        }

        return true;
    }

    /**
     * Feeds the command sender.
     * @param perms PermisionHandler - The permission system.
     * @param p Player - The user to feed.
     * @param msg MessageUtil - The messaging system.
     * @return boolean - If the operation was successful
     */
    private boolean feedSelf(PermissionHandler perms, Player p, MessageUtil msg) {
        if (perms.has("essence.stats.feed")) {
            p.setFoodLevel(20);
            msg.send("feed", "beenfed");
            return true;
        } else {
            return perms.not();
        }
    }

    /**
     * Feeds another user.
     * @param perms PermisionHandler - The permission system.
     * @param cs CommandSender - The user to feed.
     * @param msg MessageUtil - The messaging system.
     * @param args String[] - List of command arguments.
     * @return boolean - If the operation was successful
     */
    private boolean feedOther(PermissionHandler perms, CommandSender cs, MessageUtil msg, String[] args) {
        if (perms.has("essence.stats.feed.other")) {
            String pName = args[0];
            Player p = Bukkit.getPlayer(pName);
            if (p != null) {
                msg.send("feed", "fed", new String[] { p.getName() });
                if (!(cs instanceof Player)) {
                    msg.sendTo(p, "feed", "serverfed");
                } else {
                    msg.sendTo(p, "feed", "fedby", new String[] { cs.getName() });
                }
                p.setFoodLevel(20);
            } else {
                msg.send("generic", "playernotfound");
            }
            return true;
        } else {
            return perms.not();
        }
    }
}