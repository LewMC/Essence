package net.lewmc.essence.teleportation.tp;

import com.tcoded.folialib.FoliaLib;
import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilLocation;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Essence's teleportation utility.
 */
public class UtilTeleport {
    private final Essence plugin;
    private final Logger log;

    /**
     * Used to communicate the type of teleportation to commands where this may be ambiguous.
     */
    public enum Type {
        INVALID, TO_PLAYER, TO_COORD, PLAYER_TO_PLAYER, PLAYER_TO_COORD
    }

    /**
     * Used to communicate the direction of a safe location search.
     */
    public enum Direction {
        UP, DOWN
    }

    /**
     * Constructor for the TeleportUtil class.
     * @param plugin Reference to the main Essence class.
     */
    public UtilTeleport(Essence plugin) {
        this.plugin = plugin;
        this.log = new Logger(plugin.foundryConfig);
    }

    /**
     * Checks if the cooldown has surpassed for a specific type of teleportation.
     * @param player Player - The player to query
     * @param type String - The type of teleportation.
     * @return boolean - If the cooldown has surpassed or not.
     */
    public boolean cooldownSurpassed(Player player, String type) {
        UtilPermission perms = new UtilPermission(this.plugin, player);

        if (perms.has("essence.bypass.teleportcooldown")) {
            return true;
        }

        int cooldown = (int) this.plugin.config.get("teleportation."+type+".cooldown");
        if (cooldown < 0) { return true; }

        Files data = new Files(this.plugin.foundryConfig, this.plugin);
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
        Files data = new Files(this.plugin.foundryConfig, this.plugin);
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
        UtilPermission perms = new UtilPermission(this.plugin, player);

        if (perms.has("essence.bypass.teleportcooldown")) {
            return 0;
        }

        int cooldown = (int) this.plugin.config.get("teleportation."+type + ".cooldown");

        if (cooldown <= 0) {
            return 0;
        }

        Files data = new Files(this.plugin.foundryConfig, this.plugin);
        data.load(data.playerDataFile(player));

        if (data.getString("cooldown."+type) == null) { return 0; }
        String last = data.getString("cooldown."+type);

        data.close();

        LocalDateTime lastEvent;
        try {
            lastEvent = LocalDateTime.parse(Objects.requireNonNull(last));
        } catch (DateTimeException e) {
            this.log.warn("DateTimeException: "+e);
            this.log.warn("Unable to calculate cooldown, the field may be missing or corrupted.");
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

        UtilPermission perms = new UtilPermission(this.plugin, player);

        if (perms.has("essence.bypass.teleportdelay")) {
            delay = 0;
        }

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
        UtilMessage message = new UtilMessage(this.plugin, player);
        if (location.getWorld() == null) {
            message.send("teleport","exception");
            this.log.severe("Unable to locate world in universe.");
            this.log.severe("Details: {\"error\": \"WORLD_IS_NULL\", \"caught\": \"TeleportUtil.java\", \"submitted\": \"null\", \"found\": \"null\"}.");
            this.log.severe("Location: "+location);
            return;
        }
        
        // Cross-world teleportation safety check
        World targetWorld = location.getWorld();
        World currentWorld = player.getWorld();
        if (!targetWorld.equals(currentWorld)) {
            // Validate target world instance validity
            if (!targetWorld.getName().equals(location.getWorld().getName())) {
                message.send("teleport","exception");
                this.log.severe("World mismatch detected during cross-world teleportation.");
                this.log.severe("Details: {\"error\": \"WORLD_MISMATCH\", \"player\": \"" + player.getName() + "\", \"from\": \"" + currentWorld.getName() + "\", \"to\": \"" + targetWorld.getName() + "\"}.");
                return;
            }
        }

        if (delay > 0 && (boolean) this.plugin.config.get("teleportation.move-to-cancel")) {
            message.send("teleport", "movetocancel");
        }
        this.setTeleportStatus(player, true);
        if (flib.isFolia()) {
            // Ensure minimum delay of 1 tick to avoid FoliaLib warnings
            long delayTicks = Math.max(1L, delay * 20L);
            flib.getImpl().runAtEntityLater(player, () -> {
                if (teleportIsValid(player)) {
                    new UtilLocation(plugin).UpdateLastLocation(player);
                    // Use teleportAsync directly in Folia environment, let Bukkit handle chunk loading
                    player.teleportAsync(location);
                    setTeleportStatus(player, false);
                }
            }, delayTicks);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (teleportIsValid(player)) {
                        new UtilLocation(plugin).UpdateLastLocation(player);
                        player.teleport(location);
                        setTeleportStatus(player, false);
                    }
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

    /**
     * Sets a player's teleport status.
     * @param player Player - The player to teleport.
     * @param teleportInFuture boolean - Is the teleport happening in the future?
     */
    public void setTeleportStatus(Player player, boolean teleportInFuture) {
        if (teleportInFuture) {
            this.plugin.teleportingPlayers.add(player.getUniqueId());
        } else {
            this.plugin.teleportingPlayers.remove(player.getUniqueId());
        }
    }

    /**
     * Gets a player's teleport status.
     * @param player Player - The player to teleport.
     * @return boolean - Is valid?
     */
    public boolean teleportIsValid(Player player) {
        if (this.plugin.teleportingPlayers == null) {
            return false;
        } else {
            return this.plugin.teleportingPlayers.contains(player.getUniqueId());
        }
    }

    /**
     * Is the teleport blocked by the teleport toggle?
     * @param requester The player requesting the teleport.
     * @param target The target of the teleport.
     * @return true - Teleport can proceed, false - Teleport should be blocked.
     */
    public boolean teleportToggleCheck(Player requester, Player target) {
        Files targetPd = new Files(this.plugin.foundryConfig, this.plugin);
        targetPd.load(targetPd.playerDataFile(target));

        Files config = new Files(this.plugin.foundryConfig, this.plugin);
        config.load("config.yml");

        UtilPermission ph = new UtilPermission(this.plugin, requester);
        if (
            !targetPd.getBoolean("user.accepting-teleport-requests") &&
            config.getBoolean("teleportation.extended-toggle") &&
            !(ph.has("essence.bypass.extendedteleporttoggle"))
        ) {
            targetPd.close();
            config.close();
            return false;
        } else {
            targetPd.close();
            config.close();
            return true;
        }
    }

    /**
     * Finds a safe location relative to the origin point.
     * @param origin The starting location.
     * @param direction The direction to search.
     * @return A safe location, or null if none found.
     */
    public static Location findFurthestLocation(Location origin, Direction direction, Player player) {
        if (origin == null) {
            return null;
        }

        World world = origin.getWorld();
        if (world == null) {
            return null;
        }

        int x = origin.getBlockX();
        int z = origin.getBlockZ();
        float yaw = origin.getYaw();
        float pitch = origin.getPitch();

        int startY;
        int endY;
        int step;
        if (direction == Direction.UP) {
            startY = Math.min(world.getMaxHeight() - 1, world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING) + 1);
            endY = world.getMinHeight();
            step = -1;
        } else {
            startY = world.getMinHeight();
            endY = world.getMaxHeight();
            step = 1;
        }
        
        for (int y = startY; (direction == Direction.UP) == (y >= endY); y += step) {
            Block feet = world.getBlockAt(x, y, z);
            Block head = (y < world.getMaxHeight() - 1) ? world.getBlockAt(x, y + 1, z) : null;
            Block below = (y > world.getMinHeight()) ? world.getBlockAt(x, y - 1, z) : null;

            if (isSafeLocation(feet, head, below, player)) {
                return new Location(world, x + 0.5, y, z + 0.5, yaw, pitch);
            }
        }

        return null;
    }

    public record LevelLocation(Location location, int finalLevels) {}

    /**
     * Finds a safe location to teleport a player to, given a direction and number of levels.
     *
     * @param origin The location to start searching from.
     * @param direction The direction to search in.
     * @param levels The number of levels to search.
     * @param player The player to check for safe locations.
     * @return A LevelLocation containing the safe location and the number of levels searched, or null if no safe location is found.
     */
    public static LevelLocation findLevelLocation(Location origin, Direction direction, Integer levels, Player player) {
        if (origin == null || levels <= 0) {
            return null;
        }

        World world = origin.getWorld();
        if (world == null) {
            return null;
        }

        int x = origin.getBlockX();
        int z = origin.getBlockZ();
        int y = origin.getBlockY();
        float yaw = origin.getYaw();
        float pitch = origin.getPitch();

        int step = (direction == Direction.UP) ? 1 : -1;
        int currentLevel = 0;
        int currentY = y + step;
        Location lastSafeLocation = null;

        while (currentY >= world.getMinHeight() && currentY <= world.getMaxHeight()) {
            Location checkLoc = new Location(world, x, currentY, z, yaw, pitch);
            Block feet = checkLoc.getBlock();
            Block head = checkLoc.add(0, 1, 0).getBlock();
            Block below = checkLoc.subtract(0, 2, 0).getBlock();

            if (isSafeLocation(feet, head, below, player)) {
                currentLevel++;
                lastSafeLocation = new Location(world, x + 0.5, currentY, z + 0.5, yaw, pitch);

                if (currentLevel == levels) {
                    return new LevelLocation(lastSafeLocation, currentLevel);
                }

                currentY += step;
            }

            currentY += step;
        }

        return new LevelLocation(lastSafeLocation, currentLevel);

    }

    private static boolean isSafeLocation(Block feet, Block head, Block below, Player player) {
        return head != null &&
                (!head.isLiquid() || player.isInvulnerable()) && head.isPassable() &&
                feet != null &&
                (!feet.isLiquid() || player.isInvulnerable()) && feet.isPassable() &&
                below != null &&
                !below.getType().isAir() && below.getType().isSolid();
    }
}
