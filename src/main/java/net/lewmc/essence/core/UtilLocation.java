package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

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
        TypePlayer player = this.plugin.players.get(p.getUniqueId());
        player.lastLocation.world = p.getLocation().getWorld().getName();
        player.lastLocation.x = p.getLocation().getX();
        player.lastLocation.y = p.getLocation().getY();
        player.lastLocation.z = p.getLocation().getZ();
        player.lastLocation.yaw = p.getLocation().getYaw();
        player.lastLocation.pitch = p.getLocation().getPitch();
        player.lastLocation.isBed = false;
        this.plugin.players.replace(p.getUniqueId(), player);
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
}
