package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.FileUtil;
import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.TeleportUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * RespawnEvent class.
 */
public class RespawnEvent implements Listener {
    private final Essence plugin;

    /**
     * Constructor for the RespawnEvent class.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public RespawnEvent(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for when a player respawns.
     * @param event PlayerRespawnEvent - Server thrown event.
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        MessageUtil message = new MessageUtil(event.getPlayer(), this.plugin);

        FileUtil config = new FileUtil(this.plugin);
        config.load("config.yml");
        String spawnName = config.getString("teleportation.spawn.main-spawn-world");
        boolean alwaysSpawn = config.getBoolean("teleportation.spawn.always-spawn");
        config.close();

        FileUtil playerData = new FileUtil(this.plugin);
        playerData.load(config.playerDataFile(event.getPlayer()));

        if ((playerData.getString("user.last-sleep-location") != null) && !alwaysSpawn) {
            TeleportUtil tp = new TeleportUtil(plugin);
            tp.doTeleport(
                    event.getPlayer(),
                    Bukkit.getServer().getWorld("user.last-sleep-location.world"),
                    playerData.getDouble("user.last-sleep-location.X"),
                    playerData.getDouble("user.last-sleep-location.Y"),
                    playerData.getDouble("user.last-sleep-location.Z"),
                    (float) playerData.getDouble("user.last-sleep-location.yaw"),
                    (float) playerData.getDouble("user.last-sleep-location.pitch"),
                    0
            );

            config.close();

            return;
        }

        config.close();

        FileUtil spawns = new FileUtil(this.plugin);
        spawns.load("data/spawns.yml");

        if (spawns.get("spawn."+spawnName) != null) {
            LogUtil log = new LogUtil(this.plugin);
            if (
                Bukkit.getServer().getWorld(spawnName).getSpawnLocation() != null &&
                Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getY() >= 10
            ) {
                TeleportUtil tp = new TeleportUtil(plugin);
                tp.doTeleport(
                        event.getPlayer(),
                        Bukkit.getServer().getWorld(spawnName),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getX(),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getY(),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getZ(),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getYaw(),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getPitch(),
                        0
                );
            } else {
                message.PrivateMessage("spawn", "notexist");
                log.info("Failed to respawn player - world '"+Bukkit.getServer().getWorld(spawnName)+"' does not exist.");
            }
        } else {
            TeleportUtil tp = new TeleportUtil(plugin);
            tp.doTeleport(
                    event.getPlayer(),
                    Bukkit.getServer().getWorld(spawnName),
                    spawns.getDouble("spawn."+spawnName+".X"),
                    spawns.getDouble("spawn."+spawnName+".Y"),
                    spawns.getDouble("spawn."+spawnName+".Z"),
                    (float) spawns.getDouble("spawn."+spawnName+".yaw"),
                    (float) spawns.getDouble("spawn."+spawnName+".pitch"),
                    0
            );
        }

        spawns.close();
    }
}
