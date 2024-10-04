package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

/**
 * Essence location utility.
 */
public class LocationUtil {
    private final Essence plugin;

    /**
     * Constructor for the LocationUtil class.
     * @param plugin Reference to the main Essence class.
     */
    public LocationUtil(Essence plugin) {
        this.plugin = plugin;
    }

    public void UpdateLastLocation(Player player) {
        FileUtil playerData = new FileUtil(this.plugin);
        playerData.load(playerData.playerDataFile(player));

        playerData.set("last-location.world", player.getLocation().getWorld().getName());
        playerData.set("last-location.X", player.getLocation().getX());
        playerData.set("last-location.Y", player.getLocation().getY());
        playerData.set("last-location.Z", player.getLocation().getZ());
        playerData.set("last-location.yaw", player.getLocation().getYaw());
        playerData.set("last-location.pitch", player.getLocation().getPitch());

        playerData.save();
    }

    public Location GetRandomLocation(Player player, WorldBorder wb) {
        World world = player.getWorld();

        Location center = wb.getCenter();
        double maxX = (center.getBlockX() + (wb.getSize()/2));
        double minX = (center.getBlockX() - (wb.getSize()/2));
        double maxZ = (center.getBlockZ() + (wb.getSize()/2));
        double minZ = (center.getBlockZ() - (wb.getSize()/2));

        int x = (int) (minX + (maxX - minX) * this.plugin.rand.nextDouble());
        int z = (int) (minZ + (maxZ - minZ) * this.plugin.rand.nextDouble());

        int attempt = 1;
        int y = GetGroundY(world, x, z);
        while (y == -64 && attempt != 3) {
            y = GetGroundY(world, x, z);
            attempt++;
        }
        return new Location(world, (float) x, (float) y, (float) z);
    }

    private int GetGroundY(World world, int x, int z) {
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

    private boolean LocationIsSafe(Material block) {
        return block != Material.LAVA && block != Material.WATER && block != Material.MAGMA_BLOCK && block != Material.POWDER_SNOW && block != Material.CACTUS && block != Material.VOID_AIR;
    }
}
