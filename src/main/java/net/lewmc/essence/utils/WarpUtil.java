package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Set;

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
        FileUtil dataUtil = new FileUtil(this.plugin);
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
}
