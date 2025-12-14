package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import net.lewmc.essence.teleportation.tp.UtilTeleport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

        UUID uuid = event.getPlayer().getUniqueId();

        UtilPlayer up = new UtilPlayer(plugin);
        boolean alwaysSpawn = (boolean) plugin.config.get("teleportation.spawn.always-spawn");

        if (!alwaysSpawn && up.getPlayer(uuid, UtilPlayer.KEYS.LAST_SLEEP_WORLD) != null) {
            String worldName = up.getPlayer(uuid, UtilPlayer.KEYS.LAST_SLEEP_WORLD).toString();
            World world = Bukkit.getWorld(worldName);

            if (world != null) {
                event.setRespawnLocation(new Location(
                        world,
                        (double) up.getPlayer(uuid, UtilPlayer.KEYS.LAST_SLEEP_X),
                        (double) up.getPlayer(uuid, UtilPlayer.KEYS.LAST_SLEEP_Y),
                        (double) up.getPlayer(uuid, UtilPlayer.KEYS.LAST_SLEEP_Z),
                        ((Float) up.getPlayer(uuid, UtilPlayer.KEYS.LAST_SLEEP_YAW)),
                        ((Float) up.getPlayer(uuid, UtilPlayer.KEYS.LAST_SLEEP_PITCH))
                ));
                return;
            }
        }

        new UtilTeleport(this.plugin).sendToSpawn(event.getPlayer(), plugin.config.get("teleportation.spawn.main-spawn-world").toString());
    }
}
