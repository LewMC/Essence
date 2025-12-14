package net.lewmc.essence.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGod extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the GodCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandGod(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string
     */
    @Override
    protected String requiredPermission() {
        return "essence.stats.god";
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
            return this.godOther(new UtilPermission(this.plugin, cs), cs, message, args);
        } else {
            if (!(cs instanceof Player)) {
                message.send("god", "usage");
                return true;
            }

            return this.godSelf(cs, message);
        }
    }

    /**
     * Sets or Removes Godmode from a player
     * @param player - The player to godmode
     * @return boolean - Operation success status
     */
    private boolean godPlayer(Player player) {
        boolean newState = !player.isInvulnerable();
        player.setInvulnerable(newState);
        return newState;
    }

    /**
     * Godmodes the command sender.
     * @param sender CommandSender - The user to god.
     * @param message MessageUtil - The messaging system.
     * @return boolean - If the operation was successful
     */
    private boolean godSelf(CommandSender sender, UtilMessage message) {
        Player player = (Player) sender;
        boolean isNowGod = godPlayer(player);
        message.send("god", isNowGod ? "beenenabled" : "beendisabled");
        return true;
    }

    /**
     * Godmodes another user.
     * @param permission PermissionHandler - The permission system
     * @param sender CommandSender - The user to god.
     * @param message MessageUtil - The messaging system.
     * @param args String[] - List of command arguments.
     * @return boolean - If the operation was successful
     */
    private boolean godOther(UtilPermission permission, CommandSender sender, UtilMessage message, String[] args) {
        if (permission.has("essence.stats.god.other")) {
            String pName = args[0];
            Player p = Bukkit.getPlayer(pName);
            if (p != null) {
                boolean isNowGod = !p.isInvulnerable();
                message.send("god", isNowGod ? "enabled" : "disabled", new String[] { p.getName() });
                if (!(sender instanceof Player)) {
                    message.sendTo(p, "god", isNowGod ? "serverenabled" : "serverdisabled");
                } else {
                    message.sendTo(p, "god", isNowGod ? "enabledby" : "disabledby", new String[] { sender.getName() });
                }
                godPlayer(p);
            } else {
                message.send("generic", "playernotfound");
            }
            return true;
        } else {
            return permission.not();
        }
    }
}
