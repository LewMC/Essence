package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Set;

/**
 * /homes command helper utility.
 */
public class HomeUtil {
    private final Essence plugin;

    /**
     * Constructor for the HomeUtil class.
     * @param plugin Essence - Reference to main class.
     */
    public HomeUtil(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets a list of homes.
     * @param player Player - The player who's homes to list.
     * @return StringBuilder|null - List of homes or null.
     */
    public StringBuilder getHomesList(Player player) {
        FileUtil dataUtil = new FileUtil(this.plugin);
        dataUtil.load(dataUtil.playerDataFile(player));

        Set<String> keys = dataUtil.getKeys("homes", false);

        if (keys == null || Objects.equals(keys.toString(), "[]")) {
            dataUtil.close();
            return null;
        }

        StringBuilder setHomes = new StringBuilder();
        int i = 0;

        for (String key : keys) {
            if (i == 0) {
                setHomes.append(key);
            } else {
                setHomes.append(", ").append(key);
            }
            i++;
        }
        dataUtil.close();

        return setHomes;
    }

    /**
     * Gets a list of team homes.
     * @param team String - The team who's homes to list.
     * @return StringBuilder|null - List of homes or null.
     */
    public StringBuilder getTeamHomesList(String team) {
        FileUtil dataUtil = new FileUtil(this.plugin);
        dataUtil.load("data/teams/"+team+".yml");

        Set<String> keys = dataUtil.getKeys("homes", false);

        if (keys == null || Objects.equals(keys.toString(), "[]")) {
            dataUtil.close();
            return null;
        }

        StringBuilder setHomes = new StringBuilder();
        int i = 0;

        for (String key : keys) {
            if (i == 0) {
                setHomes.append(key);
            } else {
                setHomes.append(", ").append(key);
            }
            i++;
        }
        dataUtil.close();

        return setHomes;
    }

    /**
     * Returns the amount of homes that a user has set.
     * @param player Player - The player who should be checked.
     * @return int - The number of homes.
     */
    public int getHomeCount(Player player) {
        FileUtil dataUtil = new FileUtil(this.plugin);
        dataUtil.load(dataUtil.playerDataFile(player));

        return dataUtil.getKeys("homes", false).size();
    }
}
