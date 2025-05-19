package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.Files;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * The warp utility.
 */
public class WarpUtil {
    private final Essence plugin;

    /**
     * Constructor for the WarpUtil class.
     * @param plugin Essence - Reference to the main class.
     */
    public WarpUtil(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns the amount of warps that a user has set.
     * @param player Player - The player who should be checked.
     * @return int - The number of warps.
     */
    public int getWarpCount(Player player) {
        Files dataUtil = new Files(this.plugin.config, this.plugin);
        dataUtil.load("data/warps.yml");

        Set<String> keys = dataUtil.getKeys("warps", false);

        int warps = 0;

        if (keys == null) {
            return warps;
        }

        for (String key : keys) {
            if (Objects.equals(dataUtil.getString("warps." + key + ".creator"), player.getUniqueId().toString())) {
                warps++;
            }
        }

        return warps;
    }

    /**
     * Creates a new warp.
     * @param warpName String - The name of the warp.
     * @param playerUUID UUID - Owner's UUID.
     * @param loc Location - The location of the warp.
     * @return boolean - If the operation was successful.
     */
    public boolean create(String warpName, UUID playerUUID, Location loc) {
        Files warpsData = new Files(this.plugin.config, this.plugin);
        warpsData.load("data/warps.yml");

        if (warpsData.get("warps." + warpName) != null) {
            warpsData.close();
            MessageUtil message = new MessageUtil(this.plugin, this.plugin.getServer().getPlayer(playerUUID));
            message.send("warp", "alreadyexists");
            return false;
        }

        warpsData.set("warps."+warpName+".creator", playerUUID.toString());
        warpsData.set("warps."+warpName+".world", loc.getWorld().getName());
        warpsData.set("warps."+warpName+".X", loc.getX());
        warpsData.set("warps."+warpName+".Y", loc.getY());
        warpsData.set("warps."+warpName+".Z", loc.getZ());
        warpsData.set("warps."+warpName+".yaw", loc.getYaw());
        warpsData.set("warps."+warpName+".pitch", loc.getPitch());

        return warpsData.save();
    }
}
