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
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("warps")) {
            if (permission.has("essence.warp.list")) {
                DataUtil dataUtil = new DataUtil(this.plugin, message);
                dataUtil.load("/data/warps.yml");

                Set<String> keys = dataUtil.getKeys("warps");

                if (keys == null) {
                    dataUtil.close();
                    message.PrivateMessage("warp", "noneset");
                    return true;
                }

                StringBuilder warps = new StringBuilder();
                int i = 0;

                for (String key : keys) {
                    this.log.info(key);
                    if (i == 0) {
                        warps.append(key);
                    } else {
                        warps.append(", ").append(key);
                    }
                    i++;
                }
                dataUtil.close();
                message.PrivateMessage("warp", "list", warps.toString());
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}