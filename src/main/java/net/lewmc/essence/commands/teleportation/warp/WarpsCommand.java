package net.lewmc.essence.commands.teleportation.warp;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
        @NotNull CommandSender cs,
        @NotNull Command command,
        @NotNull String s,
        String[] args
    ) {

        if (command.getName().equalsIgnoreCase("warps")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("warps")) { return cmd.disabled(); }

            PermissionHandler perms = new PermissionHandler(this.plugin, cs);

            if (perms.has("essence.warp.list")) {
                MessageUtil msg = new MessageUtil(this.plugin, cs);

                FileUtil data = new FileUtil(this.plugin);
                data.load("/data/warps.yml");

                Set<String> keys = data.getKeys("warps", false);

                if (keys == null || Objects.equals(keys.toString(), "[]")) {
                    data.close();
                    msg.send("warp", "noneset");
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
                msg.send("warp", "list", new String[] { warps.toString() });
            } else {
                return perms.not();
            }
            return true;
        }

        return false;
    }
}