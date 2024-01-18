package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.DataUtil;
import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.PermissionHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class WarpsCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the WarpsCommand class.
     * @param plugin References to the main plugin class.
     */
    public WarpsCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
    }

    /**
     * @param commandSender Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
        @NotNull CommandSender commandSender,
        @NotNull Command command,
        @NotNull String s,
        String[] args
    ) {
        if (!(commandSender instanceof Player)) {
            this.log.noConsole();
            return true;
        }
        MessageUtil message = new MessageUtil(commandSender, plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(player, message);

        if (command.getName().equalsIgnoreCase("warps")) {
            if (permission.has("essence.warp.list")) {
                DataUtil dataUtil = new DataUtil(this.plugin, message);
                dataUtil.load("data/warps.yml");

                Set<String> keys = dataUtil.getKeys("warps");

                if (keys == null) {
                    message.PrivateMessage("There are no warps set.", false);
                    return true;
                }

                StringBuilder setWarps = new StringBuilder("Warps: ");
                int i = 0;

                for (String key : keys) {
                    if (i == 0) {
                        setWarps.append(key);
                    } else {
                        setWarps.append(", ").append(key);
                    }
                    i++;
                }
                message.PrivateMessage(setWarps.toString(), false);
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}