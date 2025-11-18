package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.util.UUID;

public class EventPlayerBedEnter implements Listener {
    private final Essence plugin;

    /**
     * Constructor for the PlayerBedEnter class.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public EventPlayerBedEnter(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for when a player respawns.
     * @param event PlayerRespawnEvent - Server thrown event.
     * @since 1.11.0
     */
    @EventHandler
    public void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
        Location bedLocation = event.getBed().getLocation();

        UUID pid = event.getPlayer().getUniqueId();

        UtilPlayer up = new UtilPlayer(this.plugin);
        up.setPlayer(pid, UtilPlayer.KEYS.LAST_LOCATION_WORLD, bedLocation.getWorld().getName());
        up.setPlayer(pid, UtilPlayer.KEYS.LAST_LOCATION_X, bedLocation.getX());
        up.setPlayer(pid, UtilPlayer.KEYS.LAST_LOCATION_Y, bedLocation.getY());
        up.setPlayer(pid, UtilPlayer.KEYS.LAST_LOCATION_Z, bedLocation.getZ());
        up.setPlayer(pid, UtilPlayer.KEYS.LAST_LOCATION_YAW, bedLocation.getYaw());
        up.setPlayer(pid, UtilPlayer.KEYS.LAST_LOCATION_PITCH, bedLocation.getPitch());
        up.setPlayer(pid, UtilPlayer.KEYS.LAST_LOCATION_IS_BED, true);

        UtilMessage messageUtil = new UtilMessage(this.plugin, event.getPlayer());
        messageUtil.send("other", "respawnset");
    }
}
