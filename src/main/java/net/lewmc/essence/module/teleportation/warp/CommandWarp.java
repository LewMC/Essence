package net.lewmc.essence.module.teleportation.warp;

import net.lewmc.essence.global.UtilCommand;
import net.lewmc.essence.global.UtilLocation;
import net.lewmc.essence.global.UtilMessage;
import net.lewmc.essence.global.UtilPermission;
import net.lewmc.essence.module.teleportation.tp.UtilTeleport;
import net.lewmc.essence.Essence;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CommandWarp implements CommandExecutor {
    private final Essence plugin;
    private final Logger log;

    /**
     * Constructor for the WarpCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandWarp(Essence plugin) {
        this.plugin = plugin;
        this.log = new Logger(plugin.config);
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
        if (command.getName().equalsIgnoreCase("warp")) {
            UtilCommand cmd = new UtilCommand(this.plugin, cs);
            if (cmd.isDisabled("warp")) { return cmd.disabled(); }

            if (!(cs instanceof Player p)) { return this.log.noConsole(); }

            UtilPermission permission = new UtilPermission(this.plugin, cs);

            if (permission.has("essence.warp.use")) {
                int waitTime = plugin.getConfig().getInt("teleportation.warp.wait");
                UtilMessage msg = new UtilMessage(this.plugin, cs);

                if (args.length > 0) {
                    UtilTeleport teleUtil = new UtilTeleport(this.plugin);

                    if (!teleUtil.cooldownSurpassed(p, "warp")) {
                        msg.send("teleport", "tryagain", new String[] { String.valueOf(teleUtil.cooldownRemaining(p, "warp")) });
                        return true;
                    }

                    Files config = new Files(this.plugin.config, this.plugin);
                    config.load("data/warps.yml");

                    if (config.get("warps." + args[0].toLowerCase()) == null) {
                        msg.send("warp", "notfound", new String[] { args[0].toLowerCase() });
                        return true;
                    }

                    if (config.getString("warps." + args[0].toLowerCase()+".world") == null) {
                        config.close();
                        msg.send("generic", "exception");
                        this.log.warn("Player "+p+" attempted to warp to "+args[0].toLowerCase()+" but couldn't due to an error.");
                        this.log.warn("Error: world is null, please check configuration file.");
                        return true;
                    }
                    UtilLocation locationUtil = new UtilLocation(this.plugin);
                    locationUtil.UpdateLastLocation(p);

                    teleUtil.setCooldown(p, "warp");

                    if (Bukkit.getServer().getWorld(config.getString("warps." + args[0].toLowerCase()+".world")) == null) {
                        WorldCreator creator = new WorldCreator(config.getString("warps." + args[0].toLowerCase()+".world"));
                        creator.createWorld();
                    }

                    msg.send("warp", "teleporting", new String[] { args[0], waitTime+"" });

                    teleUtil.doTeleport(
                            p,
                            Bukkit.getServer().getWorld(Objects.requireNonNull(config.getString("warps." + args[0].toLowerCase()+".world"))),
                            config.getDouble("warps." + args[0].toLowerCase()+".X"),
                            config.getDouble("warps." + args[0].toLowerCase()+".Y"),
                            config.getDouble("warps." + args[0].toLowerCase()+".Z"),
                            (float) config.getDouble("warps." + args[0].toLowerCase()+".yaw"),
                            (float) config.getDouble("warps." + args[0].toLowerCase()+".pitch"),
                            waitTime
                    );

                    config.close();

                    return true;
                } else {
                    msg.send("warp", "usage");
                }
            } else {
                return permission.not();
            }
            return true;
        }

        return false;
    }
}