package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import io.papermc.paper.threadedregions.scheduler.RegionScheduler;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

/**
 * Essence's teleportation utility.
 */
public class TeleportUtil {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Used to communicate the type of teleportation to commands where this may be
     * ambiguous.
     */
    public enum Type {
        INVALID, TO_PLAYER, TO_COORD, PLAYER_TO_PLAYER, PLAYER_TO_COORD
    }

    /**
     * Constructor for the TeleportUtil class.
     * 
     * @param plugin Reference to the main Essence class.
     */
    public TeleportUtil(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
    }

    /**
     * Checks if the cooldown has surpassed for a specific type of teleportation.
     * 
     * @param player Player - The player to query
     * @param type   String - The type of teleportation.
     * @return boolean - If the cooldown has surpassed or not.
     */
    public boolean cooldownSurpassed(Player player, String type) {
        this.log.info("Checking cooldown for player: " + player.getName() + " for type: " + type);
        int cooldown = this.plugin.getConfig().getInt("teleportation." + type + ".cooldown");
        this.log.info("Cooldown duration: " + cooldown + " seconds");

        if (cooldown < 0) {
            this.log.info("Cooldown is negative; bypassing.");
            return true;
        }

        FileUtil data = new FileUtil(this.plugin);
        data.load(data.playerDataFile(player));

        if (data.get("cooldown." + type) == null) {
            this.log.info("No cooldown record found; allowing teleport.");
            return true;
        }
        String last = data.getString("cooldown." + type);

        if (last == null) {
            this.log.info("Last cooldown timestamp is null; allowing teleport.");
            return true;
        }

        data.close();

        LocalDateTime lastEvent;
        try {
            lastEvent = LocalDateTime.parse(last);
        } catch (DateTimeException e) {
            this.log.warn("DateTimeException: " + e);
            this.log.warn("Unable to calculate cooldown, the field may be missing or corrupted. Resetting...");
            return true;
        }

        LocalDateTime currentTime = LocalDateTime.now();
        Duration timeElapsed = Duration.between(lastEvent, currentTime);
        boolean hasSurpassed = timeElapsed.getSeconds() >= cooldown;
        this.log.info("Time elapsed since last event: " + timeElapsed.getSeconds() + " seconds; cooldown surpassed: "
                + hasSurpassed);
        return hasSurpassed;
    }

    /**
     * Sets the cooldown for a specific type of teleportation.
     * 
     * @param player Player - The player to set the cooldown for.
     * @param type   String - The type of cooldown.
     */
    public void setCooldown(Player player, String type) {
        this.log.info("Setting cooldown for player: " + player.getName() + " for type: " + type);
        FileUtil data = new FileUtil(this.plugin);
        data.load(data.playerDataFile(player));

        LocalDateTime currentTime = LocalDateTime.now();
        data.set("cooldown." + type, currentTime.toString());

        data.save();
        this.log.info("Cooldown set to: " + currentTime.toString());
    }

    /**
     * Gets the amount of time remaining on a specific type of cooldown.
     * 
     * @param player Player - The player to set the cooldown for.
     * @param type   String - The type of cooldown.
     * @return int - The amount of time remaining.
     */
    public int cooldownRemaining(Player player, String type) {
        this.log.info("Calculating remaining cooldown for player: " + player.getName() + " for type: " + type);
        int cooldown = this.plugin.getConfig().getInt("teleportation." + type + ".cooldown");

        if (cooldown <= 0) {
            this.log.info("Cooldown is non-positive; returning 0.");
            return 0;
        }

        FileUtil data = new FileUtil(this.plugin);
        data.load(data.playerDataFile(player));

        if (data.getString("cooldown." + type) == null) {
            this.log.info("No cooldown record found; returning 0.");
            return 0;
        }
        String last = data.getString("cooldown." + type);
        data.close();

        LocalDateTime lastEvent;
        try {
            lastEvent = LocalDateTime.parse(Objects.requireNonNull(last));
        } catch (DateTimeException e) {
            this.log.warn("DateTimeException: " + e);
            this.log.warn("Unable to calculate cooldown, the field may be missing or corrupted.");
            return 0;
        }

        LocalDateTime currentTime = LocalDateTime.now();
        Duration timeElapsed = Duration.between(lastEvent, currentTime);
        int remaining = Math.toIntExact(Math.max(0, (long) cooldown - timeElapsed.getSeconds()));
        this.log.info("Cooldown remaining for player: " + player.getName() + " is: " + remaining + " seconds");
        return remaining;
    }

    /**
     * Creates a location to teleport a player, then teleports them.
     * 
     * @param player Player - The player to teleport.
     * @param world  World - The world to teleport them to.
     * @param X      double - The X coordinate.
     * @param Y      double - The Y coordinate.
     * @param Z      double - The Z coordinate.
     * @param yaw    float - The yaw.
     * @param pitch  float - The pitch
     * @param delay  int - The time to wait before teleporting.
     */
    public void doTeleport(Player player, World world, double X, double Y, double Z, float yaw, float pitch,
            int delay) {
        this.log.info(
                "Preparing to teleport player: " + player.getName() + " to coordinates: " + X + ", " + Y + ", " + Z);
        Location loc = new Location(world, X, Y, Z, yaw, pitch);
        this.doTeleport(player, loc, delay);
    }

    /**
     * Teleports a player to a specified location after a delay.
     *
     * @param player   The player to teleport.
     * @param location The location to teleport the player to.
     * @param delay    The amount of time to wait before teleporting, in seconds.
     */
    public void doTeleport(Player player, Location location, int delay) {
        this.log.info("Executing teleport for player: " + player.getName() + " to location: " + location
                + " with delay: " + delay);
        MessageUtil message = new MessageUtil(player, this.plugin);

        if (!isLocationValid(location, message)) {
            this.log.info("Invalid location; aborting teleport.");
            return;
        }

        // Since the delay would be 0 here, we can teleport the player immediately and
        // then exit.
        if (delay == 0) {
            this.log.info("Teleporting player immediately.");
            player.teleport(location);
            setTeleportStatus(player, false);
            return;
        }

        if (delay > 0) {
            message.send("teleport", "movetocancel");
            this.log.info("Scheduled teleport in " + delay + " seconds for player: " + player.getName());
        }

        this.setTeleportStatus(player, true);

        if (isPaperOrFolia()) {
            this.log.info("Using Paper/Folia to schedule teleport.");
            schedulePaperTeleport(player, location, delay);
        } else {
            this.log.info("Using Spigot to schedule teleport.");
            scheduleSpigotTeleport(player, location, delay);
        }
    }

    /**
     * Checks if the specified location is valid.
     *
     * @param location The location to check.
     * @param message  The MessageUtil instance for sending messages to the player.
     * @return True if the location is valid; false otherwise.
     */
    private boolean isLocationValid(Location location, MessageUtil message) {
        if (location.getWorld() == null) {
            message.send("teleport", "exception");
            this.log.severe("Unable to locate world in universe for location: " + location);
            return false;
        }
        this.log.info("Location is valid: " + location);
        return true;
    }

    /**
     * Checks if the server is running Paper or Folia.
     *
     * @return True if the server is Paper or Folia; false otherwise.
     */
    private boolean isPaperOrFolia() {
        boolean isPaper = Bukkit.getServer().getVersion().contains("Paper");
        boolean isFolia = Bukkit.getServer().getVersion().contains("Folia");
        this.log.info("Server version check - Paper: " + isPaper + ", Folia: " + isFolia);
        return isPaper || isFolia;
    }

    /**
     * Schedules a teleportation using Paper's RegionScheduler.
     *
     * @param player   The player to teleport.
     * @param location The location to teleport the player to.
     * @param delay    The delay in seconds before teleporting.
     */
    private void schedulePaperTeleport(Player player, Location location, int delay) {
        RegionScheduler scheduler = Bukkit.getServer().getRegionScheduler();

        scheduler.runDelayed(this.plugin, location, (task) -> {
            this.log.info("Teleporting player: " + player.getName() + " after delay of " + delay + " seconds.");
            player.teleport(location);
            setTeleportStatus(player, false);
        }, delay * 20L);
    }

    /**
     * Schedules a teleportation using Spigot's scheduler.
     *
     * @param player   The player to teleport.
     * @param location The location to teleport the player to.
     * @param delay    The delay in seconds before teleporting.
     */
    private void scheduleSpigotTeleport(Player player, Location location, int delay) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            this.log.info("Teleporting player: " + player.getName() + " after delay of " + delay + " seconds.");
            player.teleport(location);
            setTeleportStatus(player, false);
        }, delay * 20L);
    }

    /**
     * Sets the teleportation status of a player.
     * 
     * @param player Player - The player whose status will be set.
     * @param status boolean - The teleportation status.
     */
    public void setTeleportStatus(Player player, boolean status) {
        this.log.info("Setting teleport status for player: " + player.getName() + " to: " + status);
        FileUtil data = new FileUtil(this.plugin);
        data.load(data.playerDataFile(player));
        data.set("teleporting", status);
        data.save();
    }

    /**
     * Gets a random location in the world.
     * 
     * @param world World - The world to get a random location from.
     * @return Location - A random location in the world.
     */
    public Location getRandomLocation(World world) {
        Random random = new Random();
        double x = random.nextDouble() * 2000 - 1000; // X coordinate between -1000 and 1000
        double z = random.nextDouble() * 2000 - 1000; // Z coordinate between -1000 and 1000
        int highestBlockY = world.getHighestBlockYAt((int) x, (int) z); // Get the highest block Y at (x, z)
        double y = highestBlockY + 1; // Position the player just above the highest block
        this.log.info("Generated random location: " + x + ", " + y + ", " + z + " in world: " + world.getName());
        return new Location(world, x, y, z);
    }

    /**
     * Gets the teleportation type based on input arguments.
     *
     * @param args The arguments provided to the teleport command.
     * @return Type - The type of teleportation.
     */
    public Type getTeleportType(String[] args) {
        if (args.length == 0)
            return Type.INVALID;
        if (Bukkit.getPlayer(args[0]) != null) {
            return Type.TO_PLAYER;
        }
        return Type.INVALID;
    }

    /**
     * Checks if the teleportation is valid for the given player.
     *
     * @param player The player to check.
     * @return boolean - Whether the teleportation is valid.
     */
    public boolean teleportIsValid(Player player) {
        return true;
    }
}
