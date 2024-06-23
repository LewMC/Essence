package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Set;

public class HomeUtil {
    private final Essence plugin;

    public HomeUtil(Essence plugin) {
        this.plugin = plugin;
    }

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
}
