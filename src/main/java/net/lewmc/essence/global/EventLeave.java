package net.lewmc.essence.global;

import net.lewmc.essence.Essence;
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
        event.setQuitMessage(tag.replaceAll(this.plugin.getConfig().getString("broadcasts.leave")));
    }
}
