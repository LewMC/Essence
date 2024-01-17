package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.DataUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {
    private final Essence plugin;

    public JoinEvent(Essence plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.reloadConfig();
        if (plugin.getConfig().getBoolean("motd.enabled")) {
            if (plugin.getConfig().getString("motd.message") != null) {
                event.getPlayer().sendMessage(plugin.getConfig().getString("motd.message"));
            }
        }

        LogUtil log = new LogUtil(this.plugin);

        DataUtil data = new DataUtil(plugin, new MessageUtil(event.getPlayer(), plugin));
        String playerDataFile = data.playerDataFile(event.getPlayer());

        if (!data.fileExists(playerDataFile)) {
            log.info("Player data does not exist, creating file...");
            if (data.createFile(playerDataFile)) {
                log.info("Created player data!");
            } else {
                log.warn("Unable to create player data!");
            }

            data.load(playerDataFile);
            data.createSection("economy");
            ConfigurationSection economy = data.getSection("economy");
            economy.set("balance", plugin.getConfig().getDouble("economy.start-money"));
            economy.set("accepting-payments", true);
            data.save();
        }
    }
}