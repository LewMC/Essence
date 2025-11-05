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
 * /bottom command.
 */
public class CommandBottom extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the BottomCommand class.
     *
     * @param plugin References to the main plugin class
     */
    public CommandBottom(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     *
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.teleport.bottom";
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
                msg.send("bottom", "usage");
                return true;
            }
            return this.bottomSelf((Player) cs, msg);
        } else if (args.length == 1) {
            UtilPermission perms = new UtilPermission(this.plugin, cs);
            if (!perms.has("essence.teleport.bottom.other")) {
                return perms.not();
            }
            return this.bottomOther(cs, msg, args[0]);
        } else {
            msg.send("bottom", "usage");
            return true;
        }
    }


    /**
     * Teleport the player to the lowest safe block
     * @param p Player
     * @param msg Message utility
     * @return Success status
     */
    private boolean bottomSelf(Player p, UtilMessage msg) {
        Location safeLocation = findSafeLocation(p.getLocation(), UtilTeleport.Direction.DOWN);

        int waitTime = plugin.config.get("teleportation.bottom.wait") != null ?
                (int) plugin.config.get("teleportation.bottom.wait") : 0;

        if (safeLocation != null) {
            if (p.getLocation().getBlockY() <= safeLocation.getBlockY()) {
                msg.send("bottom", "alreadyatbottom");
                return false;
            }

            new UtilTeleport(this.plugin).doTeleport(
                    p,
                    safeLocation,
                    waitTime
            );

            if (waitTime == 0) {
                msg.send("bottom", "going");
            }
            return true;
        }
        msg.send("bottom", "nosafelocation");
        return false;
    }

    /**
     * Teleport another player to the lowest safe block
     * @param cs Command sender
     * @param msg Message utility
     * @param targetName Target player name
     * @return Success status
     */
    private boolean bottomOther(CommandSender cs, UtilMessage msg, String targetName) {
        Player targetPlayer = Bukkit.getPlayer(targetName);

        if (targetPlayer == null) {
            msg.send("generic", "playernotfound");
            return true;
        }

        Location safeLocation = findSafeLocation(targetPlayer.getLocation(), UtilTeleport.Direction.DOWN);

        int waitTime = plugin.config.get("teleportation.bottom.wait") != null ?
                (int) plugin.config.get("teleportation.bottom.wait") : 0;

        if (safeLocation != null) {
            if (targetPlayer.getLocation().getBlockY() <= safeLocation.getBlockY()) {
                msg.send("bottom", "alreadyatbottom");
                return false;
            }

            new UtilTeleport(this.plugin).doTeleport(
                    targetPlayer,
                    safeLocation,
                    waitTime
            );

            if (waitTime == 0) {
                msg.send("bottom", "goingother", new String[]{targetPlayer.getName()});
                new UtilMessage(this.plugin, targetPlayer).send("bottom", "sentby", new String[]{cs.getName()});
            }
            return true;
        }
        msg.send("bottom", "nosafelocation");
        return false;
    }
}