package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.TeleportUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * PlayerMoveEvent class.
 */
public class PlayerMove implements Listener {
    private final Essence plugin;

    /**
     * Constructor for the PlayerMoveEvent class.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public PlayerMove(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for when a player respawns.
     * @param event PlayerMoveEvent - Server thrown event.
     */
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (event.hasChangedBlock()) {
            TeleportUtil tp = new TeleportUtil(this.plugin);
            if (tp.teleportIsValid(event.getPlayer())) {
                tp.setTeleportStatus(event.getPlayer(), false);
                MessageUtil msg = new MessageUtil(this.plugin, event.getPlayer());
                msg.send("teleport", "cancelled");
            }
        }
    }
}
