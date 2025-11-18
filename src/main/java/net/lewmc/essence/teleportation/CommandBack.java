package net.lewmc.essence.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.core.UtilPlayer;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /back command.
 */
public class CommandBack extends FoundryCommand {
    private final Essence plugin;
    private final UtilPlayer up;

    /**
     * Constructor for the BackCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandBack(Essence plugin) {
        this.plugin = plugin;
        this.up = new UtilPlayer(plugin);
    }

    /**
     * The required permission.
     *
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.teleport.back";
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
                msg.send("back", "usage");
                return true;
            }
            this.backSelf((Player) cs, msg);
        } else if (args.length == 1) {
            UtilPermission perms = new UtilPermission(this.plugin, cs);
            if (!perms.has("essence.teleport.back.other")) {
                return perms.not();
            }
            this.backOther(cs, msg, args[0]);
        } else {
            msg.send("back", "usage");
        }
        return true;
    }

    /**
     * Teleport the player back to their last location
     * @param p Player
     * @param msg Message utility
     */
    private void backSelf(Player p, UtilMessage msg) {
        if (up.getPlayer(p.getUniqueId(), UtilPlayer.KEYS.LAST_LOCATION_WORLD) == null) {
            msg.send("back", "cant");
            return;
        }

        int waitTime = plugin.config.get("teleportation.back.wait") != null ? (int) plugin.config.get("teleportation.back.wait") : 0;
        
        if (Bukkit.getServer().getWorld(up.getPlayer(p.getUniqueId(), UtilPlayer.KEYS.LAST_LOCATION_WORLD).toString()) == null) {
            msg.send("back", "cant");
            return;
        }

        new UtilLocation(this.plugin).sendBack(p, waitTime);

        if (waitTime == 0) {
            msg.send("back", "going");
        }
    }

    /**
     * Teleport another player back to their last location
     * @param cs Command sender
     * @param msg Message utility
     * @param targetName Target player name
     */
    private void backOther(CommandSender cs, UtilMessage msg, String targetName) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        
        if (targetPlayer == null) {
            msg.send("generic", "playernotfound");
            return;
        }

        int waitTime = plugin.config.get("teleportation.back.wait") != null ? (int) plugin.config.get("teleportation.back.wait") : 0;

        if (Bukkit.getServer().getWorld(up.getPlayer(targetPlayer.getUniqueId(), UtilPlayer.KEYS.LAST_LOCATION_WORLD).toString()) == null) {
            msg.send("back", "cantother", new String[]{targetPlayer.getName()});
            return;
        }
        
        new UtilLocation(this.plugin).sendBack(targetPlayer, waitTime);

        if (waitTime == 0) {
            msg.send("back", "goingother", new String[]{targetPlayer.getName()});
            new UtilMessage(this.plugin, targetPlayer).send("back", "sentby", new String[]{cs.getName()});
        }
    }
}