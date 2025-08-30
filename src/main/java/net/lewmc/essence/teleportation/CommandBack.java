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
     * 传送玩家自己回到上一个位置
     * @param p 玩家
     * @param msg 消息工具
     * @return 是否成功
     */
    private boolean backSelf(Player p, UtilMessage msg) {
        Files playerData = new Files(this.plugin.foundryConfig, this.plugin);
        playerData.load(playerData.playerDataFile(p));

        if (playerData.get("last-location") == null) {
            msg.send("back", "cant");
            playerData.close();
            return true;
        }

        new UtilTeleport(this.plugin).doTeleport(
                p,
                Bukkit.getServer().getWorld(Objects.requireNonNull(playerData.getString("last-location.world"))),
                playerData.getDouble("last-location.X"),
                playerData.getDouble("last-location.Y"),
                playerData.getDouble("last-location.Z"),
                (float) playerData.getDouble("last-location.yaw"),
                (float) playerData.getDouble("last-location.pitch"),
                0
        );

        playerData.close();
        msg.send("back", "going");
        return true;
    }

    /**
     * 传送其他玩家回到上一个位置
     * @param cs 命令发送者
     * @param msg 消息工具
     * @param targetName 目标玩家名称
     * @return 是否成功
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

        new UtilTeleport(this.plugin).doTeleport(
                targetPlayer,
                Bukkit.getServer().getWorld(Objects.requireNonNull(playerData.getString("last-location.world"))),
                playerData.getDouble("last-location.X"),
                playerData.getDouble("last-location.Y"),
                playerData.getDouble("last-location.Z"),
                (float) playerData.getDouble("last-location.yaw"),
                (float) playerData.getDouble("last-location.pitch"),
                0
        );

        playerData.close();
        
        // 通知命令发送者和目标玩家
        msg.send("back", "goingother", new String[]{targetPlayer.getName()});
        new UtilMessage(this.plugin, targetPlayer).send("back", "sentby", new String[]{cs.getName()});
        
        return true;
    }
}