package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TeamUtil {
    private final Essence plugin;
    private final MessageUtil message;
    private final DataUtil data;

    public TeamUtil(Essence plugin, MessageUtil message) {
        this.plugin = plugin;
        this.message = message;
        this.data = new DataUtil(this.plugin, this.message);
    }
    public void CreateNewTeam(String name, UUID leader) {
        SecurityUtil su = new SecurityUtil();
        if (su.hasSpecialCharacters(name)) {
            message.PrivateMessage("team", "specialchars");
            return;
        }

        if (!this.data.fileExists("/data/teams/"+name+".yml")) {
            if (this.data.createFile("/data/teams/"+name+".yml")) {
                this.loadData(name);
                this.data.createSection("members");
                ConfigurationSection cs = this.data.getSection("members");
                cs.set("leader", leader.toString());
                cs.set("default", null);
                this.data.save();

                this.data.load(data.playerDataFile(leader));
                ConfigurationSection cs2 = this.data.getSection("team");
                if (cs2 == null) {
                    this.data.createSection("team");
                    cs2 = this.data.getSection("team");
                }
                cs2.set("name", name);
                this.data.save();

                message.PrivateMessage("team", "created", name);
            } else {
                message.PrivateMessage("generic", "exception");
                LogUtil log = new LogUtil(this.plugin);
                log.warn("Unable to create new team file at '/data/teams/"+name+".yml' - is this file writeable?");
            }
        } else {
            message.PrivateMessage("team", "exists");
        }
    }

    public void requestJoin(String team, UUID player) {
        if (this.data.fileExists("/data/teams/"+team+".yml")) {
            this.loadData(team);
            ConfigurationSection cs = this.data.getSection("members");
            List<String> requests = cs.getStringList("requests");
            requests.add(player.toString());
            cs.set("requests", requests);
            this.data.save();
            message.PrivateMessage("team", "requested", team);
        } else {
            message.PrivateMessage("team", "notfound");
        }
    }

    public boolean isLeader(String team, UUID player) {
        if (this.data.fileExists("/data/teams/"+team+".yml")) {
            this.loadData(team);
            ConfigurationSection cs = this.data.getSection("members");
            String leader = cs.getString("leader");
            this.data.close();
            return leader.equalsIgnoreCase(player.toString());
        } else {
            return false;
        }
    }

    public @Nullable String getPlayerTeam(UUID player) {
        this.data.load("/data/players/"+player.toString());
        ConfigurationSection cs = this.data.getSection("team");
        String team = cs.getString("name");
        this.data.close();
        return team;
    }

    public void loadData(String team) {
        data.load("/data/teams/" + team + ".yml");
    }
}
