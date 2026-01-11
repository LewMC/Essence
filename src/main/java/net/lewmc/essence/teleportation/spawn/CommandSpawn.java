package net.lewmc.essence.teleportation.spawn;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.teleportation.tp.UtilTeleport;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class CommandSpawn extends FoundryPlayerCommand {
    private final Essence plugin;
    private final Logger log;

    /**
     * Constructor for the SpawnCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandSpawn(Essence plugin) {
        this.plugin = plugin;
        this.log = new Logger(plugin.foundryConfig);
    }

    /**
     * The required permission.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.spawn";
    }

    /**
     * @param cs       Information about who sent the command - player or console.
     * @param command  Information about what command was sent.
     * @param s        Command label - not used here.
     * @param args     The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        int waitTime = (int) plugin.config.get("teleportation.spawn.wait");
        UtilTeleport teleUtil = new UtilTeleport(this.plugin);

        UtilMessage msg = new UtilMessage(this.plugin, cs);

        Player p = (Player) cs;

        if (!teleUtil.cooldownSurpassed(p, "spawn")) {
            msg.send("teleport", "tryagain", new String[]{String.valueOf(teleUtil.cooldownRemaining(p, "spawn"))});
            return true;
        }

        if (args.length == 0 && (boolean) plugin.config.get("teleportation.spawn.global-spawn.enabled")) {
            new UtilTeleport(this.plugin).sendToSpawn(p, plugin.config.get("teleportation.spawn.global-spawn.world").toString(), true);
            return true;
        }

        Location loc = p.getLocation();

        String spawnName;

        if (args.length != 0) {
            if (new UtilPermission(this.plugin, cs).has("essence.world.teleport")) {
                if (Objects.equals(args[0], "tp")) {
                    if (args.length >= 2) {
                        spawnName = args[1];
                    } else {
                        spawnName = loc.getWorld().getName();
                    }
                } else {
                    spawnName = args[0];
                }
            } else {
                msg.send("spawn", "worldnoperms");
                return true;
            }
        } else {
            spawnName = loc.getWorld().getName();
        }

        World world = Bukkit.getServer().getWorld(spawnName);
        if (world == null) {
            this.log.severe("Unable to locate world in universe.");
            this.log.severe("Details: {\"error\": \"WORLD_IS_NULL\", \"caught\": \"SpawnCommand.java\", \"submitted\": \"" + spawnName + "\", \"found\": \"null\"}.");
            msg.send("spawn", "notexist", new String[]{ spawnName });
            return true;
        }

        Files spawnData = new Files(this.plugin.foundryConfig, this.plugin);
        spawnData.load("data/worlds.yml");
        UUID uid = world.getUID();


        Location teleportLocation;

        if (spawnData.get("world." + uid + ".spawn") == null) {
            if (this.plugin.verbose) {
                this.log.warn("Spawn not implicitly set for world '" + spawnName + "', grabbing vanilla spawnpoint.");
            }
            if (Bukkit.getServer().getWorld(spawnName) != null) {
                teleportLocation = new Location(
                        Bukkit.getServer().getWorld(spawnName),
                        Objects.requireNonNull(Bukkit.getServer().getWorld(spawnName)).getSpawnLocation().getX(),
                        Objects.requireNonNull(Bukkit.getServer().getWorld(spawnName)).getSpawnLocation().getY(),
                        Objects.requireNonNull(Bukkit.getServer().getWorld(spawnName)).getSpawnLocation().getZ(),
                        Objects.requireNonNull(Bukkit.getServer().getWorld(spawnName)).getSpawnLocation().getYaw(),
                        Objects.requireNonNull(Bukkit.getServer().getWorld(spawnName)).getSpawnLocation().getPitch()
                );
            } else {
                msg.send("spawn", "notexist");
                return true;
            }
        } else {
            if (this.plugin.verbose) {
                this.log.info("Spawn implicitly set for world '" + spawnName + "'.");
            }

            if (Bukkit.getServer().getWorld(spawnName) == null) {
                msg.send("generic", "exception");
                log.warn("Player " + p + " attempted to teleport to spawn " + spawnName + " but couldn't due to an error.");
                log.warn("Error: world is null, please check configuration file.");
            }

            teleportLocation = new Location(
                    Bukkit.getServer().getWorld(spawnName),
                    spawnData.getDouble("world." + uid + ".spawn.x"),
                    spawnData.getDouble("world." + uid + ".spawn.y"),
                    spawnData.getDouble("world." + uid + ".spawn.z"),
                    (float) spawnData.getDouble("world." + uid + ".spawn.yaw"),
                    (float) spawnData.getDouble("world." + uid + ".spawn.pitch")
            );
        }

        teleUtil.setCooldown(p, "spawn");

        if (waitTime > 0) {
            msg.send("spawn", "teleportingin", new String[]{String.valueOf(waitTime)});
        } else {
            msg.send("spawn", "teleportingnow");
        }

        teleUtil.doTeleport(p, teleportLocation, waitTime, true);

        spawnData.close();

        return true;
    }
}