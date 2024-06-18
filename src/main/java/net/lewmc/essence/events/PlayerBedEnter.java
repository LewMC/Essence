package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.FileUtil;
import net.lewmc.essence.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class PlayerBedEnter implements Listener {
    private final Essence plugin;

    /**
     * Constructor for the PlayerBedEnter class.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public PlayerBedEnter(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for when a player respawns.
     * @param event PlayerRespawnEvent - Server thrown event.
     */
    @EventHandler
    public void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
        Location bedLocation = event.getBed().getLocation();

        FileUtil playerData = new FileUtil(this.plugin);
        playerData.load(playerData.playerDataFile(event.getPlayer()));

        playerData.set("user.last-sleep-location.world", bedLocation.getWorld().getName());
        playerData.set("user.last-sleep-location.X", bedLocation.getX());
        playerData.set("user.last-sleep-location.Y", bedLocation.getY());
        playerData.set("user.last-sleep-location.Z", bedLocation.getZ());
        playerData.set("user.last-sleep-location.yaw", bedLocation.getYaw());
        playerData.set("user.last-sleep-location.pitch", bedLocation.getPitch());

        playerData.save();

        MessageUtil messageUtil = new MessageUtil(event.getPlayer(), this.plugin);
        messageUtil.PrivateMessage("other", "respawnset");
    }
}
