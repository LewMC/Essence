package net.lewmc.essence.commands.teleportation.warp;

import net.lewmc.essence.utils.*;
import net.lewmc.essence.Essence;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class WarpCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the WarpCommand class.
     * @param plugin References to the main plugin class.
     */
    public WarpCommand(Essence plugin) {
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
        PermissionHandler permission = new PermissionHandler(commandSender, message);
        TeleportUtil teleUtil = new TeleportUtil(this.plugin);

        if (command.getName().equalsIgnoreCase("warp")) {
            if (permission.has("essence.warp.use")) {
                int waitTime = plugin.getConfig().getInt("teleportation.warp.wait");
                if (args.length > 0) {
                    if (!teleUtil.cooldownSurpassed(player, "warp")) {
                        message.send("teleport", "tryagain", new String[] { String.valueOf(teleUtil.cooldownRemaining(player, "warp")) });
                        return true;
                    }

                    FileUtil config = new FileUtil(this.plugin);
                    config.load("data/warps.yml");

                    if (config.get("warps." + args[0].toLowerCase()) == null) {
                        message.send("warp", "notfound", new String[] { args[0].toLowerCase() });
                        return true;
                    }

                    if (config.getString("warps." + args[0].toLowerCase()+".world") == null) {
                        config.close();
                        message.send("generic", "exception");
                        this.log.warn("Player "+player+" attempted to warp to "+args[0].toLowerCase()+" but couldn't due to an error.");
                        this.log.warn("Error: world is null, please check configuration file.");
                        return true;
                    }
                    LocationUtil locationUtil = new LocationUtil(this.plugin);
                    locationUtil.UpdateLastLocation(player);

                    teleUtil.setCooldown(player, "warp");

                    if (Bukkit.getServer().getWorld(config.getString("warps." + args[0].toLowerCase()+".world")) == null) {
                        WorldCreator creator = new WorldCreator(config.getString("warps." + args[0].toLowerCase()+".world"));
                        creator.createWorld();
                    }

                    teleUtil.doTeleport(
                            player,
                            Bukkit.getServer().getWorld(Objects.requireNonNull(config.getString("warps." + args[0].toLowerCase()+".world"))),
                            config.getDouble("warps." + args[0].toLowerCase()+".X"),
                            config.getDouble("warps." + args[0].toLowerCase()+".Y"),
                            config.getDouble("warps." + args[0].toLowerCase()+".Z"),
                            (float) config.getDouble("warps." + args[0].toLowerCase()+".yaw"),
                            (float) config.getDouble("warps." + args[0].toLowerCase()+".pitch"),
                            waitTime
                    );

                    config.close();
                    message.send("warp", "teleporting", new String[] { args[0], waitTime+"" });

                    return true;
                } else {
                    message.send("warp", "usage");
                }
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}