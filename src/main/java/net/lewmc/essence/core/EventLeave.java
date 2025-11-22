package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import net.lewmc.essence.teleportation.UtilLocation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * LeaveEvent class.
 */
public class EventLeave implements Listener {
    private final Essence plugin;

    /**
     * Constructor for the LeaveEvent class.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public EventLeave(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for when a player dies.
     * @param event PlayerQuitEvent - Server thrown event.
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        UtilLocation locationUtil = new UtilLocation(this.plugin);
        locationUtil.UpdateLastLocation(event.getPlayer());

        UtilPlaceholder tag = new UtilPlaceholder(this.plugin, event.getPlayer());
        if (this.plugin.config.get("chat.broadcasts.leave") instanceof String) {
            event.setQuitMessage(tag.replaceAll((String) this.plugin.config.get("chat.broadcasts.leave")));
        }

        UtilPlayer up = new UtilPlayer(this.plugin);
        if (!up.savePlayer(event.getPlayer().getUniqueId())) {
            this.plugin.log.severe("Unable to save player data.");
            this.plugin.log.warn("It wasn't possible to save "+event.getPlayer().getName()+"'s player data.");
            this.plugin.log.warn("The player data may be stale/outdated.");
        }
        up.unloadPlayer(event.getPlayer().getUniqueId());
    }
}
