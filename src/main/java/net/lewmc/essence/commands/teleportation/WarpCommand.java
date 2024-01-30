package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.utils.*;
import net.lewmc.essence.Essence;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

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
                        message.PrivateMessage("teleport", "tryagain", String.valueOf(teleUtil.cooldownRemaining(player, "warp")));
                        return true;
                    }

                    DataUtil config = new DataUtil(this.plugin, message);
                    config.load("data/warps.yml");

                    if (config.getSection("warps." + args[0].toLowerCase()) == null) {
                        message.PrivateMessage("warp", "notfound", args[0].toLowerCase());
                        return true;
                    }

                    ConfigurationSection cs = config.getSection("warps." + args[0].toLowerCase());

                    if (cs.getString("world") == null) {
                        config.close();
                        message.PrivateMessage("generic", "exception");
                        this.log.warn("Player "+player+" attempted to warp to "+args[0].toLowerCase()+" but couldn't due to an error.");
                        this.log.warn("Error: world is null, please check configuration file.");
                        return true;
                    }
                    LocationUtil locationUtil = new LocationUtil(this.plugin, message);
                    locationUtil.UpdateLastLocation(player);

                    if (waitTime > 0) {
                        message.PrivateMessage("teleport", "wait", String.valueOf(waitTime));
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {

                            Location loc = new Location(
                                    Bukkit.getServer().getWorld(cs.getString("world")),
                                    cs.getDouble("X"),
                                    cs.getDouble("Y"),
                                    cs.getDouble("Z"),
                                    (float) cs.getDouble("yaw"),
                                    (float) cs.getDouble("pitch")
                            );

                            player.teleport(loc);
                            config.close();

                            teleUtil.setCooldown(player, "warp");

                            message.PrivateMessage("warp", "teleporting", args[0].toLowerCase());
                        }
                    }.runTaskLater(plugin, waitTime * 20L);

                    return true;
                } else {
                    message.PrivateMessage("warp", "usage");
                }
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}