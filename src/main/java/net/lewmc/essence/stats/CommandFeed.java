package net.lewmc.essence.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class CommandFeed extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the FeedCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandFeed(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.stats.feed";
    }

    /**
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage message = new UtilMessage(this.plugin, cs);

        if (args.length > 0) {
            return this.feedOther(new UtilPermission(this.plugin, cs), cs, message, args);
        } else {
            if (!(cs instanceof Player)) {
                message.send("feed","usage");
                return true;
            } else {
                return this.feedSelf((Player) cs, message);
            }
        }
    }

    /**
     * Feeds the command sender.
     * @param p Player - The user to feed.
     * @param msg MessageUtil - The messaging system.
     * @return boolean - If the operation was successful
     */
    private boolean feedSelf(Player p, UtilMessage msg) {
        p.setFoodLevel(20);
        msg.send("feed", "beenfed");
        return true;
    }

    /**
     * Feeds another user.
     * @param perms PermisionHandler - The permission system.
     * @param cs CommandSender - The user to feed.
     * @param msg MessageUtil - The messaging system.
     * @param args String[] - List of command arguments.
     * @return boolean - If the operation was successful
     */
    private boolean feedOther(UtilPermission perms, CommandSender cs, UtilMessage msg, String[] args) {
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