package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import net.lewmc.essence.teleportation.tp.UtilTeleport;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.UUID;

/**
 * RespawnEvent class.
 */
public class EventRespawn implements Listener {
    private final Essence plugin;

    /**
     * Constructor for the RespawnEvent class.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public EventRespawn(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for when a player respawns.
     * @param event PlayerRespawnEvent - Server thrown event.
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Logger log = new Logger(this.plugin.foundryConfig);
        UtilMessage message = new UtilMessage(this.plugin, event.getPlayer());

        Files config = new Files(this.plugin.foundryConfig, this.plugin);
        config.load("config.yml");
        String spawnName = config.getString("teleportation.spawn.main-spawn-world");
        boolean alwaysSpawn = config.getBoolean("teleportation.spawn.always-spawn");
        config.close();

        UUID epID = event.getPlayer().getUniqueId();

        UtilPlayer up = new UtilPlayer(this.plugin);
        if ((up.getPlayer(epID, UtilPlayer.KEYS.LAST_SLEEP_WORLD) != null) && !alwaysSpawn) {
            UtilTeleport tp = new UtilTeleport(plugin);
            World world = Bukkit.getServer().getWorld((String)up.getPlayer(epID, UtilPlayer.KEYS.LAST_SLEEP_WORLD));
            if (world != null) {
                tp.doTeleport(
                        event.getPlayer(),
                        Bukkit.getServer().getWorld(up.getPlayer(epID, UtilPlayer.KEYS.LAST_SLEEP_WORLD).toString()),
                        (Double) up.getPlayer(epID, UtilPlayer.KEYS.LAST_SLEEP_X),
                        (Double) up.getPlayer(epID, UtilPlayer.KEYS.LAST_SLEEP_Y),
                        (Double) up.getPlayer(epID, UtilPlayer.KEYS.LAST_SLEEP_Z),
                        (Float) up.getPlayer(epID, UtilPlayer.KEYS.LAST_SLEEP_YAW),
                        (Float) up.getPlayer(epID, UtilPlayer.KEYS.LAST_SLEEP_PITCH),
                        0,
                        false
                );

                return;
            }
        }

        Files spawns = new Files(this.plugin.foundryConfig, this.plugin);
        spawns.load("data/spawns.yml");

        if (spawns.get("spawn."+spawnName) == null) {
            if (
                Bukkit.getServer().getWorld(spawnName).getSpawnLocation() != null &&
                Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getY() >= 10
            ) {
                UtilTeleport tp = new UtilTeleport(plugin);
                tp.doTeleport(
                        event.getPlayer(),
                        Bukkit.getServer().getWorld(spawnName),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getX(),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getY(),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getZ(),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getYaw(),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getPitch(),
                        0,
                        false
                );
            } else {
                message.send("spawn", "notexist");
                log.info("Failed to respawn player - world '"+Bukkit.getServer().getWorld(spawnName)+"' does not exist.");
            }
        } else {
            UtilTeleport tp = new UtilTeleport(plugin);

            World world = Bukkit.getServer().getWorld(spawnName);
            
            if (world == null) {
                message.send("spawn", "notexist");
                this.plugin.log.severe("World "+spawnName+" not found.");
            } else {
                tp.doTeleport(
                        event.getPlayer(),
                        world,
                        spawns.getDouble("spawn." + spawnName + ".X"),
                        spawns.getDouble("spawn." + spawnName + ".Y"),
                        spawns.getDouble("spawn." + spawnName + ".Z"),
                        (float) spawns.getDouble("spawn." + spawnName + ".yaw"),
                        (float) spawns.getDouble("spawn." + spawnName + ".pitch"),
                        0,
                        false
                );
            }
        }

        spawns.close();
    }
}
