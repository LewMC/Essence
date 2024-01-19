package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class LocationUtil {
    private final Essence plugin;
    private final MessageUtil message;

    public LocationUtil(Essence plugin, MessageUtil message) {
        this.plugin = plugin;
        this.message = message;
    }

    public void UpdateLastLocation(Player player) {

        DataUtil data = new DataUtil(this.plugin, this.message);
        data.load(data.playerDataFile(player));

        if (!data.sectionExists("last-location")) {
            data.createSection("last-location");
        }
        ConfigurationSection cs = data.getSection("last-location");
        cs.set("world", player.getLocation().getWorld().getName());
        cs.set("X", player.getLocation().getX());
        cs.set("Y", player.getLocation().getY());
        cs.set("Z", player.getLocation().getZ());
        cs.set("yaw", player.getLocation().getYaw());
        cs.set("pitch", player.getLocation().getPitch());

        data.save();
    }
}
