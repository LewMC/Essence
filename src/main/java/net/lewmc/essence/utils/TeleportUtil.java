package net.lewmc.essence.utils;

import com.tcoded.folialib.FoliaLib;
import net.lewmc.essence.Essence;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Essence's teleportation utility.
 */
public class TeleportUtil {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Used to communicate the type of teleportation to commands where this may be ambiguous.
     */
    public enum Type {
        INVALID, TO_PLAYER, TO_COORD, PLAYER_TO_PLAYER, PLAYER_TO_COORD
    }

    /**
     * Constructor for the TeleportUtil class.
     * @param plugin Reference to the main Essence class.
     */
    public TeleportUtil(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
    }

    /**
     * Checks if the cooldown has surpassed for a specific type of teleportation.
     * @param player Player - The player to query
     * @param type String - The type of teleportation.
     * @return boolean - If the cooldown has surpassed or not.
     */
    public boolean cooldownSurpassed(Player player, String type) {
        int cooldown = this.plugin.getConfig().getInt("teleportation."+type+".cooldown");
        if (cooldown < 0) { return true; }

        FileUtil data = new FileUtil(this.plugin);
        data.load(data.playerDataFile(player));

        if (data.get("cooldown."+type) == null) { return true; }
        String last = data.getString("cooldown."+type);

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

    /**
     * Sets the cooldown for a specific type of teleportation.
     * @param player Player - The player to set the cooldown for.
     * @param type String - The type of cooldown.
     */
    public void setCooldown(Player player, String type) {
        FileUtil data = new FileUtil(this.plugin);
        data.load(data.playerDataFile(player));

        LocalDateTime currentTime = LocalDateTime.now();

        data.set("cooldown."+type, currentTime.toString());

        data.save();

    }

    /**
     * Gets the amount of time remaining on a specific type of cooldown.
     * @param player Player - The player to set the cooldown for.
     * @param type String - The type of cooldown.
     * @return int - The amount of time remaining.
     */
    public int cooldownRemaining(Player player, String type) {
        int cooldown = this.plugin.getConfig().getInt("teleportation."+type + ".cooldown");

        if (cooldown <= 0) {
            return 0;
        }

        FileUtil data = new FileUtil(this.plugin);
        data.load(data.playerDataFile(player));

        if (data.getString("cooldown."+type) == null) { return 0; }
        String last = data.getString("cooldown."+type);

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

    /**
     * Creates a location to teleport a player, then teleports them.
     * @param player Player - The player to teleport.
     * @param world World - The world to teleport them to.
     * @param X double - The X coordinate.
     * @param Y double - The Y coordinate.
     * @param Z double - The Z coordinate.
     * @param yaw float - The yaw.
     * @param pitch float - The pitch
     * @param delay int - The time to wait before teleporting.
     */
    public void doTeleport(
            Player player,
            World world,
            double X,
            double Y,
            double Z,
            float yaw,
            float pitch,
            int delay
    ) {
        Location loc = new Location(
                world,
                X,
                Y,
                Z,
                yaw,
                pitch
        );

        this.doTeleport(player, loc, delay);
    }

    /**
     * Teleports a player.
     * @param player Player - The player to teleport.
     * @param location Location - The location to teleport them to,
     * @param delay int - The amount of time to wait before teleporting.
     */
    public void doTeleport(Player player, Location location, int delay) {
        FoliaLib flib = new FoliaLib(this.plugin);
        if (location.getWorld() == null) {
            MessageUtil message = new MessageUtil(player, this.plugin);
            message.PrivateMessage("generic","exception");
            this.log.severe("Unable to locate world in universe.");
            this.log.severe("Details: {\"error\": \"WORLD_IS_NULL\", \"caught\": \"TeleportUtil.java\", \"submitted\": \"null\", \"found\": \"null\"}.");
            return;
        }

        if (flib.isFolia()) {
            flib.getImpl().runAtEntityLater(player, () -> player.teleportAsync(location), delay * 20L);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.teleport(location);
                }
            }.runTaskLater(plugin, delay * 20L);
        }
    }

    /**
     * Determines which type of teleportation is taking place.
     * @param args String[] - arguments from a command.
     * @return Type - The teleportation type (instanceof TeleportUtil.Type)
     */
    public Type getTeleportType(String[] args) {
        if (args.length == 1) {
            return Type.TO_PLAYER;
        }

        if (args.length == 2) {
            return Type.PLAYER_TO_PLAYER;
        }

        if (args.length == 3) {
            return Type.TO_COORD;
        }

        if (args.length == 4) {
            return Type.PLAYER_TO_COORD;
        }

        return Type.INVALID;
    }
}
