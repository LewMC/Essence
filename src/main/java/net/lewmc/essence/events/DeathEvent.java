package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.LocationUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * DeathEvent class.
 */
public class DeathEvent implements Listener {
    private final Essence plugin;

    /**
     * Constructor for the DeathEvent class.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public DeathEvent(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for when a player dies.
     * @param event PlayerDeathEvent - Server thrown event.
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        LocationUtil locationUtil = new LocationUtil(this.plugin);
        locationUtil.UpdateLastLocation(event.getPlayer());
    }
}
