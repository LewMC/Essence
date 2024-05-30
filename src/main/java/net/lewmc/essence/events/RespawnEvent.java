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

public class RespawnEvent implements Listener {
    private final Essence plugin;

    public RespawnEvent(Essence plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        MessageUtil message = new MessageUtil(event.getPlayer(), this.plugin);

        FileUtil config = new FileUtil(this.plugin);
        config.load("config.yml");
        String spawnName = config.getString("teleportation.spawn.main-spawn-world");
        config.close();

        FileUtil spawns = new FileUtil(this.plugin);
        spawns.load("data/spawns.yml");

        if (spawns.get("spawn."+spawnName) != null) {
            LogUtil log = new LogUtil(this.plugin);
            if (Bukkit.getServer().getWorld(spawnName) != null) {
                TeleportUtil tp = new TeleportUtil(plugin);
                tp.doTeleport(
                        event.getPlayer(),
                        Bukkit.getServer().getWorld(spawnName),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getX(),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getY(),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getZ(),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getYaw(),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getPitch()
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
                    (float) spawns.getDouble("spawn."+spawnName+".pitch")
            );
        }

        config.close();
    }
}
