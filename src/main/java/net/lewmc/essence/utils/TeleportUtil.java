package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class TeleportUtil {
    private final Essence plugin;
    private final LogUtil log;

    public TeleportUtil(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
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

        LocalDateTime lastEvent;
        try {
            lastEvent = LocalDateTime.parse(last);
        } catch (DateTimeException e) {
            this.log.warn("DateTimeException: "+e);
            this.log.warn("Unable to calculate cooldown, the field may be missing or corrupted. Resetting...");
            return true;
        }

        LocalDateTime currentTime = LocalDateTime.now();

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

        LocalDateTime currentTime = LocalDateTime.now();

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

        LocalDateTime lastEvent;
        try {
            lastEvent = LocalDateTime.parse(Objects.requireNonNull(last));
        } catch (DateTimeException e) {
            this.log.warn("DateTimeException: "+e);
            this.log.warn("Unable to calculate cooldown, the field may be missing or corrupted. Resetting...");
            return 0;
        }

        LocalDateTime currentTime = LocalDateTime.now();
        Duration timeElapsed = Duration.between(lastEvent, currentTime);

        return Math.toIntExact(Math.max(0, (long) cooldown - timeElapsed.getSeconds()));
    }

    public void doTeleport(
            Player player,
            World world,
            double X,
            double Y,
            double Z,
            float yaw,
            float pitch
    ) {
        Location loc = new Location(
                world,
                X,
                Y,
                Z,
                yaw,
                pitch
        );

        CommandUtil cu = new CommandUtil(this.plugin);
        if (!cu.isFolia()) {
            player.teleport(loc);
        }
    }

    public void doTeleport(Player player, Location location) {
        CommandUtil cu = new CommandUtil(this.plugin);
        if (!cu.isFolia()) {
            player.teleport(location);
        }
    }
}
