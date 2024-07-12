package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.Location;
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

        Set<String> homes = dataUtil.getKeys("homes", false);

        if (homes == null) {
            return 0;
        } else {
            return homes.size();
        }
    }

    /**
     * Returns the amount of team homes that a user has set.
     * @param player Player - The player who should be checked.
     * @return int - The number of team homes.
     */
    public int getTeamHomeCount(Player player) {
        TeamUtil teamUtil = new TeamUtil(this.plugin, new MessageUtil(player, this.plugin));
        FileUtil dataUtil = new FileUtil(this.plugin);
        dataUtil.load("data/teams/"+teamUtil.getPlayerTeam(player.getUniqueId())+".yml");

        Set<String> keys = dataUtil.getKeys("homes", false);

        int homes = 0;

        if (keys == null) {
            return homes;
        }

        for (String key : keys) {
            if (Objects.equals(dataUtil.getString("homes." + key + ".creator"), player.getUniqueId().toString())) {
                homes++;
            }
        }

        return homes;
    }

    /**
     * Creates a new home
     * @param homeName String - The name of the home.
     * @param player Player - The player.
     * @param loc Location - The location for the home.
     * @return boolean - If the operation was successful.
     */
    public boolean create(String homeName, Player player, Location loc) {
        LogUtil log = new LogUtil(this.plugin);
        log.info("NAME: "+homeName);
        log.info("PLAYER: "+player);
        log.info("LOCATION: "+loc);

        FileUtil playerData = new FileUtil(this.plugin);
        playerData.load(playerData.playerDataFile(player));

        if (playerData.get(homeName) != null) {
            playerData.close();
            MessageUtil message = new MessageUtil(player, this.plugin);
            message.send("home", "alreadyexists");
            return false;
        }

        playerData.set("homes." + homeName + ".world", loc.getWorld().getName());
        playerData.set("homes." + homeName + ".X", loc.getX());
        playerData.set("homes." + homeName + ".Y", loc.getY());
        playerData.set("homes." + homeName + ".Z", loc.getZ());
        playerData.set("homes." + homeName + ".yaw", loc.getYaw());
        playerData.set("homes." + homeName + ".pitch", loc.getPitch());

        return playerData.save();
    }
}
