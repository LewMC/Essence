package net.lewmc.essence.teleportation.spawn;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilLocation;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.teleportation.tp.UtilTeleport;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandSpawn implements CommandExecutor {
    private final Essence plugin;
    private final Logger log;

    /**
     * Constructor for the SpawnCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandSpawn(Essence plugin) {
        this.plugin = plugin;
        this.log = new Logger(plugin.config);
    }

    /**
     * @param cs Information about who sent the command - player or console.
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
        if (command.getName().equalsIgnoreCase("spawn")) {
            UtilCommand cmd = new UtilCommand(this.plugin, cs);
            if (cmd.isDisabled("spawn")) { return cmd.disabled(); }

            if (!(cs instanceof Player p)) { return this.log.noConsole(); }

            UtilPermission perms = new UtilPermission(this.plugin, cs);
            if (perms.has("essence.spawn")) {

                int waitTime = plugin.getConfig().getInt("teleportation.spawn.wait");
                UtilTeleport teleUtil = new UtilTeleport(this.plugin);

                UtilMessage msg = new UtilMessage(this.plugin, cs);

                if (!teleUtil.cooldownSurpassed(p, "spawn")) {
                    msg.send("teleport", "tryagain", new String[] { String.valueOf(teleUtil.cooldownRemaining(p, "spawn")) });
                    return true;
                }

                Location loc = p.getLocation();

                String spawnName;

                if (args.length == 1) {
                    if (perms.has("essence.spawn.other")) {
                        spawnName = args[0];
                    } else {
                        msg.send("spawn", "worldnoperms");
                        return true;
                    }
                } else {
                    spawnName = loc.getWorld().getName();
                }

                Files spawnData = new Files(this.plugin.config, this.plugin);
                spawnData.load("data/spawns.yml");

                Location teleportLocation;

                if (Bukkit.getServer().getWorld(spawnName) == null) {
                    this.log.severe("Unable to locate world in universe.");
                    this.log.severe("Details: {\"error\": \"WORLD_IS_NULL\", \"caught\": \"SpawnCommand.java\", \"submitted\": \""+spawnName+"\", \"found\": \"null\"}.");
                }

                if (spawnData.get("spawn."+spawnName) == null) {
                    if (this.plugin.verbose) {
                        this.log.warn("Spawn not implicitly set for world '"+spawnName+"', grabbing vanilla spawnpoint.");
                    }
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
                        msg.send("spawn", "notexist");
                        return true;
                    }
                } else {
                    if (this.plugin.verbose) {
                        this.log.info("Spawn implicitly set for world '"+spawnName+"'.");
                    }
                    UtilLocation locationUtil = new UtilLocation(this.plugin);
                    locationUtil.UpdateLastLocation(p);

                    if (Bukkit.getServer().getWorld(spawnName) == null) {
                        WorldCreator creator = new WorldCreator(spawnName);
                        creator.createWorld();
                    }

                    teleportLocation = new Location(
                            Bukkit.getServer().getWorld(spawnName),
                            spawnData.getDouble("spawn."+spawnName+".X"),
                            spawnData.getDouble("spawn."+spawnName+".Y"),
                            spawnData.getDouble("spawn."+spawnName+".Z"),
                            (float) spawnData.getDouble("spawn."+spawnName+".yaw"),
                            (float) spawnData.getDouble("spawn."+spawnName+".pitch")
                    );
                }

                teleUtil.setCooldown(p, "spawn");

                msg.send("spawn", "teleporting", new String[] { String.valueOf(waitTime) });

                teleUtil.doTeleport(p, teleportLocation, waitTime);

                spawnData.close();

            } else {
                return perms.not();
            }
            return true;
        }

        return false;
    }
}