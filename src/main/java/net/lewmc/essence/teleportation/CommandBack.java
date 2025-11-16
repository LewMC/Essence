package net.lewmc.essence.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.teleportation.tp.UtilTeleport;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

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
            // /back - 传送自己回到上一个位置
            if (!(cs instanceof Player)) {
                msg.send("back", "usage");
                return true;
            }
            return this.backSelf((Player) cs, msg);
        } else if (args.length == 1) {
            // /back <player> - 传送其他玩家回到上一个位置
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
        Files playerData = new Files(this.plugin.foundryConfig, this.plugin);
        playerData.load(playerData.playerDataFile(p));

        if (playerData.get("last-location") == null) {
            msg.send("back", "cant");
            playerData.close();
            return true;
        }

        int waitTime = plugin.config.get("teleportation.back.wait") != null ?
                (int) plugin.config.get("teleportation.back.wait") : 0;
        
        // 检查目标世界是否存在
        String worldName = playerData.getString("last-location.world");
        if (worldName == null || Bukkit.getServer().getWorld(worldName) == null) {
            msg.send("back", "cant");
            playerData.close();
            return true;
        }
        
        new UtilTeleport(this.plugin).doTeleport(
                p,
                Bukkit.getServer().getWorld(worldName),
                playerData.getDouble("last-location.X"),
                playerData.getDouble("last-location.Y"),
                playerData.getDouble("last-location.Z"),
                (float) playerData.getDouble("last-location.yaw"),
                (float) playerData.getDouble("last-location.pitch"),
                waitTime
        );

        playerData.close();
        
        // 只有在延迟为0时才立即显示消息，否则让UtilTeleport处理
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

        Files playerData = new Files(this.plugin.foundryConfig, this.plugin);
        playerData.load(playerData.playerDataFile(targetPlayer));

        if (playerData.get("last-location") == null) {
            msg.send("back", "cantother", new String[]{targetPlayer.getName()});
            playerData.close();
            return true;
        }

        int waitTime = plugin.config.get("teleportation.back.wait") != null ?
                (int) plugin.config.get("teleportation.back.wait") : 0;
        
        // 检查目标世界是否存在
        String worldName = playerData.getString("last-location.world");
        if (worldName == null || Bukkit.getServer().getWorld(worldName) == null) {
            msg.send("back", "cantother", new String[]{targetPlayer.getName()});
            playerData.close();
            return true;
        }
        
        new UtilTeleport(this.plugin).doTeleport(
                targetPlayer,
                Bukkit.getServer().getWorld(worldName),
                playerData.getDouble("last-location.X"),
                playerData.getDouble("last-location.Y"),
                playerData.getDouble("last-location.Z"),
                (float) playerData.getDouble("last-location.yaw"),
                (float) playerData.getDouble("last-location.pitch"),
                waitTime
        );

        playerData.close();
        
        // 只有在延迟为0时才立即显示消息，否则让UtilTeleport处理
        if (waitTime == 0) {
            // 通知命令发送者和目标玩家
            msg.send("back", "goingother", new String[]{targetPlayer.getName()});
            new UtilMessage(this.plugin, targetPlayer).send("back", "sentby", new String[]{cs.getName()});
        }
        
        return true;
    }
}