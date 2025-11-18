package net.lewmc.essence.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilPlayer;
import net.lewmc.essence.teleportation.tp.UtilTeleport;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Essence location utility.
 */
public class UtilLocation {
    private final Essence plugin;

    /**
     * Constructor for the LocationUtil class.
     * @param plugin Reference to the main Essence class.
     */
    public UtilLocation(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Updates the player's last location.
     * @param p Player - The player
     */
    public void UpdateLastLocation(Player p) {
        UUID pid = p.getUniqueId();

        UtilPlayer up = new UtilPlayer(plugin);
        if (
            up.setPlayer(pid, UtilPlayer.KEYS.LAST_LOCATION_WORLD, p.getLocation().getWorld().getName()) &&
            up.setPlayer(pid, UtilPlayer.KEYS.LAST_LOCATION_X, p.getLocation().getX()) &&
            up.setPlayer(pid, UtilPlayer.KEYS.LAST_LOCATION_Y, p.getLocation().getY()) &&
            up.setPlayer(pid, UtilPlayer.KEYS.LAST_LOCATION_Z, p.getLocation().getZ()) &&
            up.setPlayer(pid, UtilPlayer.KEYS.LAST_LOCATION_YAW, p.getLocation().getYaw()) &&
            up.setPlayer(pid, UtilPlayer.KEYS.LAST_LOCATION_PITCH, p.getLocation().getPitch()) &&
            up.setPlayer(pid, UtilPlayer.KEYS.LAST_LOCATION_IS_BED, false)
        ) {
            return;
        }
        this.plugin.log.warn("Unable to update last location: player "+p.getName()+" may be null.");
    }

    /**
     * Gets the ground Y coordinate.
     * @param world World - The world
     * @param x int - Location X
     * @param z int - Location Z
     * @return int - The ground Y
     */
    public int GetGroundY(World world, int x, int z) {
        int y = 319;
        Material block = world.getBlockAt(x, y, z).getType();
        while ((block == Material.AIR)) {
            y--;
            block = world.getBlockAt(x, y, z).getType();
            if (y == -64) { break; }
        }
        if (!LocationIsSafe(block) || y == -64) { return -64; }
        return y + 1;
    }

    /**
     * Is the location safe?
     * @param block Mateiral - The block.
     * @return boolean - Is safe?
     */
    private boolean LocationIsSafe(Material block) {
        return block != Material.LAVA && block != Material.WATER && block != Material.MAGMA_BLOCK && block != Material.POWDER_SNOW && block != Material.CACTUS && block != Material.VOID_AIR;
    }

    /**
     * Teleports a player back to their last known location.
     * @param player Player - The player to teleport
     * @param waitTime int - Wait time
     */
    public void sendBack(Player player, int waitTime) {
        UtilPlayer up = new UtilPlayer(this.plugin);
        new UtilTeleport(this.plugin).doTeleport(
                player,
                Bukkit.getServer().getWorld(up.getPlayer(player.getUniqueId(), UtilPlayer.KEYS.LAST_LOCATION_WORLD).toString()),
                (Double) up.getPlayer(player.getUniqueId(), UtilPlayer.KEYS.LAST_LOCATION_X),
                (Double) up.getPlayer(player.getUniqueId(), UtilPlayer.KEYS.LAST_LOCATION_Y),
                (Double) up.getPlayer(player.getUniqueId(), UtilPlayer.KEYS.LAST_LOCATION_Z),
                (Float) up.getPlayer(player.getUniqueId(), UtilPlayer.KEYS.LAST_LOCATION_YAW),
                (Float) up.getPlayer(player.getUniqueId(), UtilPlayer.KEYS.LAST_LOCATION_PITCH),
                waitTime,
                true
        );
    }
}
