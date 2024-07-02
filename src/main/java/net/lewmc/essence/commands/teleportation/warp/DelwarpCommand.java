package net.lewmc.essence.commands.teleportation.warp;

import net.lewmc.essence.utils.*;
import net.lewmc.essence.Essence;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DelwarpCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the DelwarpCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public DelwarpCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
    }

    /**
     * @param commandSender Information about who sent the command - player or console.
     * @param command       Information about what command was sent.
     * @param s             Command label - not used here.
     * @param args          The command's arguments.
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

        if (command.getName().equalsIgnoreCase("delwarp")) {
            CommandUtil cmd = new CommandUtil(this.plugin);
            if (cmd.isDisabled("delwarp")) {
                return cmd.disabled();
            }

            if (permission.has("essence.warp.delete")) {
                if (args.length == 0) {
                    message.send("warp", "delusage");
                    return true;
                }
                FileUtil config = new FileUtil(this.plugin);
                config.load("data/warps.yml");

                String warpName = args[0].toLowerCase();

                if (config.get("warps."+warpName) == null) {
                    config.close();
                    message.send("warp", "notfound", new String[] { warpName });
                    return true;
                }

                if (config.remove("warps."+warpName)) {
                    message.send("warp", "deleted", new String[] { warpName });
                } else {
                    message.send("generic", "exception");
                }

                config.save();
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}
