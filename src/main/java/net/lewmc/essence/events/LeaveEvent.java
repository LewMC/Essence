package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.LocationUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.TagUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * LeaveEvent class.
 */
public class LeaveEvent implements Listener {
    private final Essence plugin;

    /**
     * Constructor for the LeaveEvent class.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public LeaveEvent(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for when a player dies.
     * @param event PlayerQuitEvent - Server thrown event.
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        LocationUtil locationUtil = new LocationUtil(this.plugin);
        locationUtil.UpdateLastLocation(event.getPlayer());

        TagUtil tag = new TagUtil(this.plugin, event.getPlayer());
        event.setQuitMessage(tag.doReplacement(this.plugin.getConfig().getString("broadcasts.leave")));
    }
}
