package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TeleportUtil {
    private final Essence plugin;

    public TeleportUtil(Essence plugin) {
        this.plugin = plugin;
    }

    public boolean cooldownSurpassed(Player player, String type) {
        int cooldown = this.plugin.getConfig().getInt("teleportation."+type+".cooldown");
        if (cooldown < 0) { return true; }

        DataUtil data = new DataUtil(this.plugin, new MessageUtil(player, this.plugin));
        data.load(data.playerDataFile(player));

        ConfigurationSection cs = data.getSection("cooldown");
        if (cs == null) { return true; }
        String last = cs.getString(type);

        if (last == null) { return true; }

        data.close();

        LocalTime lastEvent = LocalTime.parse(last, DateTimeFormatter.ISO_LOCAL_TIME);
        LocalTime currentTime = LocalTime.now();

        Duration timeElapsed = Duration.between(lastEvent, currentTime);

        return timeElapsed.getSeconds() >= cooldown;
    }

    public void setCooldown(Player player, String type) {
        DataUtil data = new DataUtil(this.plugin, new MessageUtil(player, this.plugin));
        data.load(data.playerDataFile(player));

        ConfigurationSection cs = data.getSection("cooldown");
        if (cs == null) {
            data.createSection("cooldown");
            cs = data.getSection("cooldown");
        }

        LocalTime currentTime = LocalTime.now();

        cs.set(type, currentTime.toString());

        data.save();

    }

    public int cooldownRemaining(Player player, String type) {
        int cooldown = this.plugin.getConfig().getInt("teleportation."+type + ".cooldown");

        if (cooldown <= 0) {
            return 0;
        }

        DataUtil data = new DataUtil(this.plugin, new MessageUtil(player, this.plugin));
        data.load(data.playerDataFile(player));

        ConfigurationSection cs = data.getSection("cooldown");
        if (cs == null) { return 0; }
        String last = cs.getString(type);

        data.close();

        LocalTime lastEvent = LocalTime.parse(last, DateTimeFormatter.ISO_LOCAL_TIME);

        LocalTime currentTime = LocalTime.now();
        Duration timeElapsed = Duration.between(lastEvent, currentTime);

        return Math.toIntExact(Math.max(0, (long) cooldown - timeElapsed.getSeconds()));
    }
}
