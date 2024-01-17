package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.DataUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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

        DataUtil data = new DataUtil(plugin, new MessageUtil(event.getPlayer(), plugin));
        data.load("/data/player/"+event.getPlayer().getUniqueId()+".yml");

        LogUtil log = new LogUtil(this.plugin);
    }
}