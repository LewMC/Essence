package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

public class TeamUtil {
    private final Essence plugin;
    private final MessageUtil message;

    public TeamUtil(Essence plugin, MessageUtil message) {
        this.plugin = plugin;
        this.message = message;
    }
    public boolean CreateNewTeam(String name, String leader) {
        SecurityUtil su = new SecurityUtil();
        if (su.hasSpecialCharacters(name)) {
            message.PrivateMessage("team", "specialchars");
            return false;
        }
        File file = new File(this.plugin.getDataFolder() + File.separator + "teams" + File.separator + name + ".yml");
        if (!file.exists()) {
            this.plugin.saveResource("teams/"+name+".yml", false);

            DataUtil data = new DataUtil(this.plugin, this.message);
            data.load("teams/"+name+".yml");
            data.createSection("members");
            ConfigurationSection cs = data.getSection("members");
            cs.set("leader", leader);
            return true;
        } else {
            message.PrivateMessage("team", "exists");
            return false;
        }
    }
}
