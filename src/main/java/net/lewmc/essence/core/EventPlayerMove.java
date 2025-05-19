package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import net.lewmc.essence.teleportation.tp.UtilTeleport;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * PlayerMoveEvent class.
 */
public class EventPlayerMove implements Listener {
    private final Essence plugin;

    /**
     * Constructor for the PlayerMoveEvent class.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public EventPlayerMove(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for when a player respawns.
     * @param event PlayerMoveEvent - Server thrown event.
     */
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (event.hasChangedBlock()) {
            UtilTeleport tp = new UtilTeleport(this.plugin);
            if (tp.teleportIsValid(event.getPlayer())) {
                tp.setTeleportStatus(event.getPlayer(), false);
                UtilMessage msg = new UtilMessage(this.plugin, event.getPlayer());
                msg.send("teleport", "cancelled");
            }
        }
    }
}
