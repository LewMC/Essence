package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.DataUtil;
import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
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

        DataUtil config = new DataUtil(this.plugin, message);
        config.load("config.yml");
        ConfigurationSection configCS = config.getSection("teleportation");
        String spawnName = configCS.get("spawn.main-spawn-world").toString();
        config.close();

        config.load("data/spawns.yml");

        ConfigurationSection cs = config.getSection("spawn." + spawnName);

        if (cs == null) {
            LogUtil log = new LogUtil(this.plugin);
            if (Bukkit.getServer().getWorld(spawnName) != null) {
                event.setRespawnLocation(new Location(
                        Bukkit.getServer().getWorld(spawnName),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getX(),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getY(),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getZ(),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getYaw(),
                        Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getPitch()
                ));
            } else {
                message.PrivateMessage("spawn", "notexist");
                log.info("Failed to respawn player - world '"+Bukkit.getServer().getWorld(spawnName)+"' does not exist.");
            }
        } else {
            event.setRespawnLocation(new Location(
                    Bukkit.getServer().getWorld(spawnName),
                    cs.getDouble("X"),
                    cs.getDouble("Y"),
                    cs.getDouble("Z"),
                    (float) cs.getDouble("yaw"),
                    (float) cs.getDouble("pitch")
            ));
        }

        config.close();
    }
}
