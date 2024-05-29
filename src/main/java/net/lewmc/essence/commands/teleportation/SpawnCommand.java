package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the SpawnCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public SpawnCommand(Essence plugin) {
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
        MessageUtil message = new MessageUtil(commandSender, this.plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("spawn")) {
            if (permission.has("essence.spawn")) {

                int waitTime = plugin.getConfig().getInt("teleportation.spawn.wait");
                TeleportUtil teleUtil = new TeleportUtil(this.plugin);

                if (!teleUtil.cooldownSurpassed(player, "spawn")) {
                    message.PrivateMessage("teleport", "tryagain", String.valueOf(teleUtil.cooldownRemaining(player, "spawn")));
                    return true;
                }

                Location loc = player.getLocation();

                String spawnName;

                if (args.length == 1) {
                    if (permission.has("essence.spawn.other")) {
                        spawnName = args[0];
                    } else {
                        message.PrivateMessage("spawn", "worldnoperms");
                        return true;
                    }
                } else {
                    spawnName = loc.getWorld().getName();
                }

                DataUtil config = new DataUtil(this.plugin, message);
                config.load("data/spawns.yml");

                ConfigurationSection cs = config.getSection("spawn." + spawnName);

                Location teleportLocation;

                if (cs == null) {
                    if (Bukkit.getServer().getWorld(spawnName) != null) {
                        teleportLocation = new Location(
                                Bukkit.getServer().getWorld(spawnName),
                                Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getX(),
                                Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getY(),
                                Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getZ(),
                                Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getYaw(),
                                Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getPitch()
                        );
                    } else {
                        message.PrivateMessage("spawn", "notexist");
                        return true;
                    }
                } else {
                    LocationUtil locationUtil = new LocationUtil(this.plugin, message);
                    locationUtil.UpdateLastLocation(player);

                    teleportLocation = new Location(
                            Bukkit.getServer().getWorld(spawnName),
                            cs.getDouble("X"),
                            cs.getDouble("Y"),
                            cs.getDouble("Z"),
                            (float) cs.getDouble("yaw"),
                            (float) cs.getDouble("pitch")
                    );
                }

                if (waitTime > 0) {
                    message.PrivateMessage("teleport", "wait", String.valueOf(waitTime));
                }

                teleUtil.setCooldown(player, "spawn");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        TeleportUtil tp = new TeleportUtil(plugin);
                        tp.doTeleport(player, teleportLocation);

                        config.close();

                        message.PrivateMessage("spawn", "teleporting");
                    }
                }.runTaskLater(plugin, waitTime * 20L);

            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}