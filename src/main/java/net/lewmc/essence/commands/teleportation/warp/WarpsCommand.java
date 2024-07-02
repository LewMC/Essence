package net.lewmc.essence.commands.teleportation.warp;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.FileUtil;
import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.PermissionHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

public class WarpsCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the WarpsCommand class.
     * @param plugin References to the main plugin class.
     */
    public WarpsCommand(Essence plugin) {
        this.plugin = plugin;
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
            LogUtil log = new LogUtil(this.plugin);
            log.noConsole();
            return true;
        }
        MessageUtil message = new MessageUtil(commandSender, plugin);
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("warps")) {
            if (permission.has("essence.warp.list")) {
                FileUtil data = new FileUtil(this.plugin);
                data.load("/data/warps.yml");

                Set<String> keys = data.getKeys("warps", false);

                if (keys == null || Objects.equals(keys.toString(), "[]")) {
                    data.close();
                    message.send("warp", "noneset");
                    return true;
                }

                StringBuilder warps = new StringBuilder();
                int i = 0;

                for (String key : keys) {
                    if (i == 0) {
                        warps.append(key);
                    } else {
                        warps.append(", ").append(key);
                    }
                    i++;
                }
                data.close();
                message.send("warp", "list", new String[] { warps.toString() });
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}