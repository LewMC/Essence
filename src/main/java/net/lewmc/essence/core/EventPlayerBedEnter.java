package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

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

        TypePlayer player = this.plugin.players.get(event.getPlayer().getUniqueId());
        if (player == null) {
            this.plugin.log.warn("Player data not loaded for " + event.getPlayer().getName());
            return;
        }

        player.lastLocation.world = bedLocation.getWorld().getName();
        player.lastLocation.x = bedLocation.getX();
        player.lastLocation.y = bedLocation.getY();
        player.lastLocation.z = bedLocation.getZ();
        player.lastLocation.yaw = bedLocation.getYaw();
        player.lastLocation.pitch = bedLocation.getPitch();
        player.lastLocation.isBed = true;

        this.plugin.players.put(event.getPlayer().getUniqueId(), player);

        UtilMessage messageUtil = new UtilMessage(this.plugin, event.getPlayer());
        messageUtil.send("other", "respawnset");
    }
}
