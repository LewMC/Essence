package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.LocationUtil;
import net.lewmc.essence.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathEvent implements Listener {
    private final Essence plugin;

    public DeathEvent(Essence plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        MessageUtil message = new MessageUtil((CommandSender)event.getPlayer(), this.plugin);
        LocationUtil locationUtil = new LocationUtil(this.plugin);
        locationUtil.UpdateLastLocation(event.getPlayer());
    }
}
