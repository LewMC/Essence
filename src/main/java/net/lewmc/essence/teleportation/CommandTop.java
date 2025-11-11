package net.lewmc.essence.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.teleportation.tp.UtilTeleport;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.lewmc.essence.teleportation.tp.UtilTeleport.findSafeLocation;

/**
 * /top command.
 */
public class CommandTop extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the TopCommand class.
     *
     * @param plugin References to the main plugin class
     */
    public CommandTop(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     *
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.teleport.top";
    }

    /**
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean  true/false - was the command accepted and processed or not?
     */

    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage msg = new UtilMessage(this.plugin, cs);

        if (args.length == 0) {
            if (!(cs instanceof Player)) {
                msg.send("top", "usage");
                return true;
            }
            return this.topSelf((Player) cs, msg);
        } else if (args.length == 1) {
            UtilPermission perms = new UtilPermission(this.plugin, cs);
            if (!perms.has("essence.teleport.top.other")) {
                return perms.not();
            }
            return this.topOther(cs, msg, args[0]);
        } else {
            msg.send("top", "usage");
            return true;
        }
    }

    /**
     * Handles the teleportation to the highest safe block for a player
     * @param player - The player to teleport
     * @param msg - Message utility
     * @param sender - The command sender
     * @param isSelf - Whether this is a self-teleport (affects messages)
     * @return true
     */
    private boolean top(Player player, UtilMessage msg, CommandSender sender, boolean isSelf) {
        Location safeLocation = findSafeLocation(player.getLocation(), UtilTeleport.Direction.UP);
        int waitTime = plugin.config.get("teleportation.top.wait") != null ?
                (int) plugin.config.get("teleportation.top.wait") : 0;

        if (safeLocation == null) {
            msg.send("top", "nosafelocation");
            return true;
        }

        if (player.getLocation().getBlockY() >= safeLocation.getBlockY()) {
            if (isSelf) {
                msg.send("top", "alreadyattop");
            } else {
                msg.send("top", "alreadyattopother", new String[]{player.getName()});
            }
            return true;
        }

        new UtilTeleport(this.plugin).doTeleport(player, safeLocation, waitTime);

        if (waitTime == 0) {
            if (isSelf) {
                msg.send("top", "going");
            } else {
                msg.send("top", "goingother", new String[]{player.getName()});
                new UtilMessage(this.plugin, player).send("top", "sentby", new String[]{sender.getName()});
            }
        }
        return true;
    }

    /**
     * Teleport the player to the highest safe block
     * @param p Player
     * @param msg Message utility
     * @return Success status
     */
    private boolean topSelf(Player p, UtilMessage msg) {
        return top(p, msg, p, true);
    }

    /**
     * Teleport another player to the highest safe block
     * @param cs Command sender
     * @param msg Message utility
     * @param targetName Target player name
     * @return Success status
     */
    private boolean topOther(CommandSender cs, UtilMessage msg, String targetName) {
        Player targetPlayer = Bukkit.getPlayer(targetName);

        if (targetPlayer == null) {
            msg.send("generic", "playernotfound");
            return true;
        }
        return top(targetPlayer, msg, cs, false);
    }
}