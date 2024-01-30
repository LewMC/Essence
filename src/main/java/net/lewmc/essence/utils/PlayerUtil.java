package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerUtil {
    private final Essence plugin;
    private final CommandSender commandSender;

    public PlayerUtil(Essence plugin, CommandSender cs) {
        this.plugin = plugin;
        this.commandSender = cs;
    }
    public boolean setGamemode(CommandSender cs, Player player, GameMode gamemode) {
        PermissionHandler permission = new PermissionHandler(cs, new MessageUtil(this.commandSender, this.plugin));
        MessageUtil message = new MessageUtil(this.commandSender, this.plugin);
        if (permission.has("essence.gamemode."+gamemode.toString().toLowerCase())) {
            if (cs == player) {
                message.PrivateMessage("gamemode", "done", gamemode.toString().toLowerCase());
                player.setGameMode(gamemode);
                return true;
            } else {
                if (permission.has("essence.gamemode.other")) {
                    message.PrivateMessage("gamemode", "doneother", player.getName(), gamemode.toString().toLowerCase());
                    message.SendTo(player, "gamemode", "doneby", gamemode.toString().toLowerCase(), cs.getName());
                    player.setGameMode(gamemode);
                    return true;
                } else {
                    return permission.not();
                }
            }
        } else {
            return permission.not();
        }
    }
}
