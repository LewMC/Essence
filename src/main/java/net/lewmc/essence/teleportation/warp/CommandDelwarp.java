package net.lewmc.essence.teleportation.warp;

import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.Essence;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandDelwarp implements CommandExecutor {
    private final Essence plugin;
    private final Logger log;

    /**
     * Constructor for the DelwarpCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandDelwarp(Essence plugin) {
        this.plugin = plugin;
        this.log = new Logger(plugin.config);
    }

    /**
     * @param cs            Information about who sent the command - player or console.
     * @param command       Information about what command was sent.
     * @param s             Command label - not used here.
     * @param args          The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
        @NotNull CommandSender cs,
        @NotNull Command command,
        @NotNull String s,
        String[] args
    ) {

        if (command.getName().equalsIgnoreCase("delwarp")) {
            UtilCommand cmd = new UtilCommand(this.plugin, cs);
            if (cmd.isDisabled("delwarp")) { return cmd.disabled(); }

            if (!(cs instanceof Player)) { return this.log.noConsole(); }
            UtilPermission permission = new UtilPermission(this.plugin, cs);

            if (permission.has("essence.warp.delete")) {
                UtilMessage msg = new UtilMessage(this.plugin, cs);

                if (args.length == 0) {
                    msg.send("warp", "delusage");
                    return true;
                }
                Files config = new Files(this.plugin.config, this.plugin);
                config.load("data/warps.yml");

                String warpName = args[0].toLowerCase();

                if (config.get("warps."+warpName) == null) {
                    config.close();
                    msg.send("warp", "notfound", new String[] { warpName });
                    return true;
                }

                if (config.remove("warps."+warpName)) {
                    msg.send("warp", "deleted", new String[] { warpName });
                } else {
                    msg.send("generic", "exception");
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
