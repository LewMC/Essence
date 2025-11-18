package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import net.lewmc.essence.teleportation.UtilLocation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * DeathEvent class.
 */
public class EventDeath implements Listener {
    private final Essence plugin;

    /**
     * Constructor for the DeathEvent class.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public EventDeath(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for when a player dies.
     * @param event PlayerDeathEvent - Server thrown event.
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        UtilLocation locationUtil = new UtilLocation(this.plugin);
        locationUtil.UpdateLastLocation(event.getPlayer());
    }
}
