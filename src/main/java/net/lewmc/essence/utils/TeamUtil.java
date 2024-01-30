package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
                ConfigurationSection cs2 = this.data.getSection("user");
                if (cs2 == null) {
                    this.data.createSection("user");
                    cs2 = this.data.getSection("user");
                }
                cs2.set("team", name);
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

    public String requestsToJoin(String team) {
        if (this.data.fileExists("/data/teams/"+team+".yml")) {
            this.loadData(team);
            ConfigurationSection cs = this.data.getSection("members");
            List<String> requests = cs.getStringList("requests");
            this.data.close();

            StringBuilder requestList = new StringBuilder();
            int i = 0;

            for (String key : requests) {
                this.data.load(data.playerDataFile(UUID.fromString(key)));
                ConfigurationSection user = this.data.getSection("user");
                if (i == 0) {
                    requestList.append(user.getString("last-known-name"));
                } else {
                    requestList.append(", ").append(user.getString("last-known-name"));
                }
                this.data.close();
                i++;
            }

            return requestList.toString();
        } else {
            message.PrivateMessage("team", "notfound");
            return "";
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
        this.data.load(data.playerDataFile(player));
        ConfigurationSection cs = this.data.getSection("user");
        if (cs == null) {
            return null;
        } else {
            String team = cs.getString("team");
            this.data.close();
            return team;
        }
    }

    public void loadData(String team) {
        this.data.load("/data/teams/" + team + ".yml");
    }

    public boolean acceptRequest(String team, String player) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);

        this.data.load(this.data.playerDataFile(op.getUniqueId()));
        ConfigurationSection requestedUser = this.data.getSection("user");
        if (requestedUser == null) {
            this.data.createSection("user");
            requestedUser = this.data.getSection("user");
        }
        requestedUser.set("team", team);
        this.data.save();

        this.loadData(team);
        ConfigurationSection teamData = this.data.getSection("members");
        if (teamData == null) {
            this.data.createSection("members");
            teamData = this.data.getSection("members");
        }

        List<String> defaultMembers = teamData.getStringList("default");
        defaultMembers.add(String.valueOf(op.getUniqueId()));
        teamData.set("default", defaultMembers);

        List<String> requestedMembers = teamData.getStringList("requests");
        requestedMembers.remove(String.valueOf(op.getUniqueId()));
        teamData.set("requests", requestedMembers);

        this.data.save();
        return true;
    }

    public boolean declineRequest(String team, String player) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);

        this.loadData(team);
        ConfigurationSection teamData = this.data.getSection("members");
        if (teamData == null) {
            this.data.createSection("members");
            teamData = this.data.getSection("members");
        }

        List<String> requestedMembers = teamData.getStringList("requests");
        requestedMembers.remove(String.valueOf(op.getUniqueId()));
        teamData.set("requests", requestedMembers);

        this.data.save();
        return true;
    }

    public boolean leave(String playerTeam, UUID uuid) {
        this.data.load(this.data.playerDataFile(uuid));

        ConfigurationSection leavingUser = this.data.getSection("user");
        if (leavingUser != null) {
            leavingUser.set("team", null);
        }

        this.data.save();

        this.loadData(playerTeam);

        ConfigurationSection teamData = this.data.getSection("members");
        if (teamData != null) {
            List<String> defaultMembers = teamData.getStringList("default");
            defaultMembers.remove(String.valueOf(uuid));
            teamData.set("default", defaultMembers);
        }

        this.data.save();

        return true;
    }

    public boolean changeLeader(String playerTeam, String newLeader, String oldLeader) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(newLeader);

        this.loadData(playerTeam);
        ConfigurationSection cs = this.data.getSection("members");

        if (cs == null) {
            return false;
        }

        cs.set("leader", String.valueOf(op.getUniqueId()));

        List<String> defaultMembers = cs.getStringList("default");
        defaultMembers.add(oldLeader);
        defaultMembers.remove(String.valueOf(op.getUniqueId()));
        cs.set("default", defaultMembers);

        this.data.save();

        return true;
    }

    public boolean exists(String team) {
        return this.data.fileExists("/data/teams/" + team + ".yml");
    }

    public String getTeamLeader(String team) {
        this.loadData(team);
        ConfigurationSection cs = this.data.getSection("members");
        String leader = cs.getString("leader");
        this.data.close();

        this.data.load(data.playerDataFile(UUID.fromString(leader)));
        ConfigurationSection user = this.data.getSection("user");
        leader = user.getString("last-known-name");
        this.data.close();

        return leader;
    }

    public String getTeamMembers(String team) {
        this.loadData(team);
        ConfigurationSection cs = data.getSection("members");
        List<String> requests = cs.getStringList("default");
        this.data.close();

        StringBuilder membersList = new StringBuilder();
        int i = 0;

        for (String key : requests) {
            this.data.load(data.playerDataFile(UUID.fromString(key)));
            ConfigurationSection user = this.data.getSection("user");
            if (i == 0) {
                membersList.append(user.getString("last-known-name"));
            } else {
                membersList.append(", ").append(user.getString("last-known-name"));
            }
            this.data.close();
            i++;
        }

        return membersList.toString();
    }

    public boolean kick(String playerTeam, String playerToKick) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(playerToKick);
        return this.leave(playerTeam, op.getUniqueId());
    }

    public boolean isMember(String team, String username) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(username);
        String uuid = op.getUniqueId().toString();

        this.loadData(team);
        ConfigurationSection cs = data.getSection("members");
        List<String> members = cs.getStringList("default");
        this.data.close();

        for (String key : members) {
            if (Objects.equals(key, uuid)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasRequested(String team, String username) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(username);
        String uuid = op.getUniqueId().toString();

        this.loadData(team);
        ConfigurationSection cs = data.getSection("members");
        List<String> members = cs.getStringList("requests");
        this.data.close();

        for (String key : members) {
            if (Objects.equals(key, uuid)) {
                return true;
            }
        }

        return false;
    }
}
