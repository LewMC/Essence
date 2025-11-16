package net.lewmc.essence.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandExtinguish extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the ExtinguishCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandExtinguish(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string
     */
    @Override
    protected String requiredPermission() {
        return "essence.stats.extinguish";
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
            return this.extinguishOther(new UtilPermission(this.plugin, cs), cs, message, args);
        } else {
            if (!(cs instanceof Player)) {
                message.send("extinguish", "usage");
                return true;
            }

            return this.extinguishSelf(cs, message);
        }
    }

    /**
     * Removes fire from a player
     * @param player - The player to extinguish
     */
    private void extinguishPlayer(Player player) {
        player.setFireTicks(0);
    }

    /**
     * Extinguishes the command sender.
     * @param sender CommandSender - The user to extinguish.
     * @param message MessageUtil - The messaging system.
     * @return boolean - If the operation was successful
     */
    private boolean extinguishSelf(CommandSender sender, UtilMessage message) {
        Player player = (Player) sender;
        extinguishPlayer(player);
        message.send("extinguish", "beenextinguished");
        return true;
    }

    /**
     * Extinguishes another user.
     * @param permission PermissionHandler - The permission system
     * @param sender CommandSender - The user to extinguish.
     * @param message MessageUtil - The messaging system.
     * @param args Sting[] - List of command arguments.
     * @return boolean - If the operation was successful
     */
    private boolean extinguishOther(UtilPermission permission, CommandSender sender, UtilMessage message, String[] args) {
        if (permission.has("essence.stats.extinguish.other")) {
            String pName = args[0];
            Player p = Bukkit.getPlayer(pName);
            if (p != null) {
                message.send("extinguish", "extinguished", new String[] { p.getName() });
                if (!(sender instanceof Player)) {
                    message.sendTo(p, "extinguish", "serverextinguished");
                } else {
                    message.sendTo(p, "extinguish", "extinguishedby", new String[] { sender.getName() });
                }
                extinguishPlayer(p);
            } else {
                message.send("generic", "playernotfound");
            }
            return true;
        } else {
            return permission.not();
        }
    }
}
