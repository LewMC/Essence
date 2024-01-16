package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.MessageHandler;
import net.lewmc.essence.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.EnumSet;

public class JoinEvent implements Listener {
    private Essence plugin;

    public JoinEvent(Essence plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.reloadConfig();
        if (plugin.getConfig().getBoolean("motd.enabled")) {
            event.getPlayer().sendMessage(plugin.getConfig().getString("motd.message"));
        }
    }
}