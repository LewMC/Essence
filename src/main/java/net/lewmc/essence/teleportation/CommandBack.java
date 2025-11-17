package net.lewmc.essence.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.TypePlayer;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.teleportation.tp.UtilTeleport;
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

    /**
     * Constructor for the BackCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandBack(Essence plugin) {
        this.plugin = plugin;
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
            return this.backSelf((Player) cs, msg);
        } else if (args.length == 1) {
            UtilPermission perms = new UtilPermission(this.plugin, cs);
            if (!perms.has("essence.teleport.back.other")) {
                return perms.not();
            }
            return this.backOther(cs, msg, args[0]);
        } else {
            msg.send("back", "usage");
            return true;
        }
    }

    /**
     * Teleport the player back to their last location
     * @param p Player
     * @param msg Message utility
     * @return Success status
     */
    private boolean backSelf(Player p, UtilMessage msg) {
        TypePlayer player = this.plugin.players.get(p.getUniqueId());
        if (player == null || player.lastLocation.world == null) {
            msg.send("back", "cant");
            return true;
        }

        int waitTime = plugin.config.get("teleportation.back.wait") != null ?
                (int) plugin.config.get("teleportation.back.wait") : 0;
        
        if (Bukkit.getServer().getWorld(player.lastLocation.world) == null) {
            msg.send("back", "cant");
            return true;
        }
        
        new UtilTeleport(this.plugin).doTeleport(
                p,
                Bukkit.getServer().getWorld(player.lastLocation.world),
                player.lastLocation.x,
                player.lastLocation.y,
                player.lastLocation.z,
                player.lastLocation.yaw,
                player.lastLocation.pitch,
                waitTime,
                true
        );

        if (waitTime == 0) {
            msg.send("back", "going");
        }
        return true;
    }

    /**
     * Teleport another player back to their last location
     * @param cs Command sender
     * @param msg Message utility
     * @param targetName Target player name
     * @return Success status
     */
    private boolean backOther(CommandSender cs, UtilMessage msg, String targetName) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        
        if (targetPlayer == null) {
            msg.send("generic", "playernotfound");
            return true;
        }

        int waitTime = plugin.config.get("teleportation.back.wait") != null ?
                (int) plugin.config.get("teleportation.back.wait") : 0;

        TypePlayer player = this.plugin.players.get(targetPlayer.getUniqueId());
        if (player == null || player.lastLocation.world == null || Bukkit.getServer().getWorld(player.lastLocation.world) == null) {
            msg.send("back", "cantother", new String[]{targetPlayer.getName()});
            return true;
        }
        
        new UtilTeleport(this.plugin).doTeleport(
                targetPlayer,
                Bukkit.getServer().getWorld(player.lastLocation.world),
                player.lastLocation.x,
                player.lastLocation.y,
                player.lastLocation.z,
                player.lastLocation.yaw,
                player.lastLocation.pitch,
                waitTime,
                true
        );

        if (waitTime == 0) {
            msg.send("back", "goingother", new String[]{targetPlayer.getName()});
            new UtilMessage(this.plugin, targetPlayer).send("back", "sentby", new String[]{cs.getName()});
        }
        
        return true;
    }
}