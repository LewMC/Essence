package net.lewmc.essence.global;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.Files;
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
     */
    @EventHandler
    public void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
        Location bedLocation = event.getBed().getLocation();

        Files playerData = new Files(this.plugin.config, this.plugin);
        playerData.load(playerData.playerDataFile(event.getPlayer()));

        playerData.set("user.last-sleep-location.world", bedLocation.getWorld().getName());
        playerData.set("user.last-sleep-location.X", bedLocation.getX());
        playerData.set("user.last-sleep-location.Y", bedLocation.getY());
        playerData.set("user.last-sleep-location.Z", bedLocation.getZ());
        playerData.set("user.last-sleep-location.yaw", bedLocation.getYaw());
        playerData.set("user.last-sleep-location.pitch", bedLocation.getPitch());

        playerData.save();

        UtilMessage messageUtil = new UtilMessage(this.plugin, event.getPlayer());
        messageUtil.send("other", "respawnset");
    }
}
