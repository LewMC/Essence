package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Team Utility
 */
public class TeamUtil {
    private final Essence plugin;
    private final MessageUtil message;

    /**
     * Constructor for TeamUtil.
     * @param plugin Reference to main Essence class.
     * @param message MessageUtil - reference to MessageUtil class.
     */
    public TeamUtil(Essence plugin, MessageUtil message) {
        this.plugin = plugin;
        this.message = message;
    }

    /**
     * Creates a new team
     * @param name String - Team name
     * @param leader UUID - Leader's UUID
     */
    public void CreateNewTeam(String name, UUID leader) {
        SecurityUtil su = new SecurityUtil();
        if (su.hasSpecialCharacters(name)) {
            message.PrivateMessage("team", "specialchars");
            return;
        }

        FileUtil teamsFile = new FileUtil(this.plugin);

        if (!teamsFile.exists("/data/teams/"+name+".yml")) {
            if (teamsFile.create("/data/teams/"+name+".yml")) {
                teamsFile.load(name);

                teamsFile.set("members.leader", leader.toString());
                teamsFile.set("members.default", null);

                teamsFile.set("rules.allow-friendly-fire", true);
                teamsFile.set("rules.allow-team-homes", true);

                teamsFile.save();

                FileUtil playerDataFile = new FileUtil(this.plugin);
                playerDataFile.load(playerDataFile.playerDataFile(leader));

                playerDataFile.set("user.team", name);
                playerDataFile.save();

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

    /**
     * Adds a join request to a specific team.
     * @param team String - team name
     * @param player UUID - UUID of the player requesting to join.
     */
    public void requestJoin(String team, UUID player) {
        FileUtil teamsFile = new FileUtil(this.plugin);
        if (teamsFile.exists("/data/teams/"+team+".yml")) {
            teamsFile.load("/data/teams/"+team+".yml");
            List<String> requests = teamsFile.getStringList("members.requests");
            requests.add(player.toString());
            teamsFile.set("members.requests", requests);
            teamsFile.save();
            message.PrivateMessage("team", "requested", team);
        } else {
            message.PrivateMessage("team", "notfound");
        }
    }

    /**
     * Lists pending join requests.
     * @param team String - team name
     * @return String - list of join requests as a String
     */
    public String requestsToJoin(String team) {
        FileUtil teamsFile = new FileUtil(this.plugin);
        if (teamsFile.exists("/data/teams/"+team+".yml")) {
            teamsFile.load("/data/teams/"+team+".yml");

            List<String> requests = teamsFile.getStringList("members.requests");
            teamsFile.close();

            StringBuilder requestList = new StringBuilder();
            int i = 0;

            for (String key : requests) {
                FileUtil playerFile = new FileUtil(this.plugin);
                playerFile.load(playerFile.playerDataFile(UUID.fromString(key)));
                if (i == 0) {
                    requestList.append(playerFile.getString("user.last-known-name"));
                } else {
                    requestList.append(", ").append(playerFile.getString("user.last-known-name"));
                }
                playerFile.close();
                i++;
            }

            return requestList.toString();
        } else {
            message.PrivateMessage("team", "notfound");
            return "";
        }
    }

    /**
     * Checks if user is the leader of the team.
     * @param team String - team name
     * @param player UUID - Player's UUID.
     * @return boolean - If they are the leader.
     */
    public boolean isLeader(String team, UUID player) {
        FileUtil teamData = new FileUtil(this.plugin);
        if (teamData.exists("/data/teams/"+team+".yml")) {
            teamData.load("/data/teams/"+team+".yml");
            String leader = teamData.getString("members.leader");
            teamData.close();
            return leader.equalsIgnoreCase(player.toString());
        } else {
            return false;
        }
    }

    /**
     * Get the requested player's team.
     * @param player UUID - Player's UUID.
     * @return String - Name of the player's team.
     */
    public @Nullable String getPlayerTeam(UUID player) {
        FileUtil playerData = new FileUtil(this.plugin);
        playerData.load(playerData.playerDataFile(player));
        if (playerData.getString("user.team") == null) {
            playerData.close();
            return null;
        } else {
            String team = playerData.getString("user.team");
            playerData.close();
            return team;
        }
    }

    /**
     * Accepts a pending join request.
     * @param team String - Team name
     * @param player String - Player's name.
     * @return boolean - If the operation was successful.
     */
    public boolean acceptRequest(String team, String player) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);

        FileUtil playerData = new FileUtil(this.plugin);
        playerData.load(playerData.playerDataFile(op.getUniqueId()));

        playerData.set("user.team", team);
        playerData.save();

        FileUtil teamData = new FileUtil(this.plugin);
        teamData.load("/data/teams/"+team+".yml");

        List<String> defaultMembers = teamData.getStringList("members.default");
        defaultMembers.add(String.valueOf(op.getUniqueId()));
        teamData.set("members.default", defaultMembers);

        List<String> requestedMembers = teamData.getStringList("members.requests");
        requestedMembers.remove(String.valueOf(op.getUniqueId()));
        teamData.set("members.requests", requestedMembers);

        teamData.save();
        return true;
    }

    /**
     * Declines a pending join request.
     * @param team String - Team name
     * @param player String - Player's name.
     * @return boolean - If the operation was successful.
     */
    public boolean declineRequest(String team, String player) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);

        FileUtil teamData = new FileUtil(this.plugin);
        teamData.load("/data/teams/"+team+".yml");

        List<String> requestedMembers = teamData.getStringList("members.requests");
        requestedMembers.remove(String.valueOf(op.getUniqueId()));
        teamData.set("members.requests", requestedMembers);

        teamData.save();
        return true;
    }

    /**
     * Leaves a team.
     * @param playerTeam String - The name of the team the player should leave.
     * @param uuid UUID - The player who is leaving's UUID,
     * @return boolean - If the operation is successful.
     */
    public boolean leave(String playerTeam, UUID uuid) {
        
        FileUtil playerData = new FileUtil(this.plugin);
        playerData.load(playerData.playerDataFile(uuid));

        if (playerData.get("team") != null) {
            playerData.set("team", null);
        }

        playerData.save();

        FileUtil teamData = new FileUtil(this.plugin);
        teamData.load("/data/teams/"+playerTeam+".yml");

        List<String> defaultMembers = teamData.getStringList("members.default");
        defaultMembers.remove(String.valueOf(uuid));
        teamData.set("members.default", defaultMembers);

        playerData.save();

        return true;
    }

    /**
     * Changes a team's leader.
     * @param playerTeam String - Name of the team.
     * @param newLeader String - Name of the team's new leader.
     * @param oldLeader String - Name of the team's old leader.
     * @return boolean - If the operation was successful.
     */
    public boolean changeLeader(String playerTeam, String newLeader, String oldLeader) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(newLeader);

        FileUtil teamData = new FileUtil(this.plugin);
        teamData.load("/data/teams/"+playerTeam+".yml");

        teamData.set("members.leader", String.valueOf(op.getUniqueId()));

        List<String> defaultMembers = teamData.getStringList("members.default");
        defaultMembers.add(oldLeader);
        defaultMembers.remove(String.valueOf(op.getUniqueId()));
        teamData.set("members.default", defaultMembers);

        teamData.save();

        return true;
    }

    /**
     * Gets a team's leader,
     * @param team String - The team to request the leader of.
     * @return String - The leader of the team.
     */
    public String getTeamLeader(String team) {
        FileUtil teamData = new FileUtil(this.plugin);
        teamData.load("/data/teams/"+team+".yml");
        String leader = teamData.getString("members.leader");
        teamData.close();

        FileUtil playerData = new FileUtil(this.plugin);
        playerData.load(playerData.playerDataFile(UUID.fromString(leader)));
        leader = playerData.getString("user.last-known-name");
        playerData.close();

        return leader;
    }

    /**
     * Gets a string list of the team's members.
     * @param team String - Name of the team.
     * @return String - List of team members as a string.
     */
    public String getTeamMembers(String team) {
        FileUtil teamData = new FileUtil(this.plugin);
        teamData.load("/data/teams/"+team+".yml");
        List<String> requests = teamData.getStringList("members.default");
        teamData.close();

        StringBuilder membersList = new StringBuilder();
        int i = 0;

        for (String key : requests) {
            FileUtil playerDataFile = new FileUtil(this.plugin);
            playerDataFile.load(playerDataFile.playerDataFile(UUID.fromString(key)));
            if (i == 0) {
                membersList.append(playerDataFile.getString("user.last-known-name"));
            } else {
                membersList.append(", ").append(playerDataFile.getString("user.last-known-name"));
            }
            playerDataFile.close();
            i++;
        }

        return membersList.toString();
    }

    /**
     * Kicks a player from a team.
     * @param playerTeam String - Name of the team.
     * @param playerToKick String - Name of the player to kick.
     * @return boolean - If the operation was successful.
     */
    public boolean kick(String playerTeam, String playerToKick) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(playerToKick);
        return this.leave(playerTeam, op.getUniqueId());
    }

    /**
     * Checks if the player is a member of a team.
     * @param team String - Name of the team.
     * @param username String - Player's username.
     * @return boolean - If the player is a member.
     */
    public boolean isMember(String team, String username) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(username);
        String uuid = op.getUniqueId().toString();

        FileUtil teamData = new FileUtil(this.plugin);
        teamData.load("/data/teams/"+team+".yml");
        List<String> members = teamData.getStringList("members.default");
        teamData.close();

        for (String key : members) {
            if (Objects.equals(key, uuid)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a user has requested to join a team.
     * @param team String - Team name.
     * @param username String - Player name.
     * @return boolean - If the player has requested to join a team.
     */
    public boolean hasRequested(String team, String username) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(username);
        String uuid = op.getUniqueId().toString();

        FileUtil teamData = new FileUtil(this.plugin);
        teamData.load("/data/teams/"+team+".yml");
        List<String> members = teamData.getStringList("members.requests");
        teamData.close();

        for (String key : members) {
            if (Objects.equals(key, uuid)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Disbands a team.
     * @param team String - Name of the team.
     * @param teamLeader String - A team's leader.
     * @return boolean - If the operation was successful.
     */
    public boolean disband(String team, String teamLeader) {
        FileUtil teamData = new FileUtil(this.plugin);
        teamData.load("/data/teams/"+team+".yml");
        List<String> members = teamData.getStringList("members.default");
        teamData.close();

        for (String key : members) {
            FileUtil playerData = new FileUtil(this.plugin);
            playerData.load(playerData.playerDataFile(UUID.fromString(key)));
            playerData.set("user.team", null);
            playerData.save();
        }

        FileUtil playerData = new FileUtil(this.plugin);
        playerData.load(playerData.playerDataFile(UUID.fromString(teamLeader)));
        playerData.set("user.team", null);
        playerData.save();

        return teamData.delete("/data/teams/" + team + ".yml");
    }

    /**
     * Checks if players are in the same team..
     * @param p1 Player - Player 1.
     * @param p2 Player - Player 2.
     * @return boolean - If the players are in the same team.
     */
    public boolean areTeammates(Player p1, Player p2) {
        FileUtil p1data = new FileUtil(this.plugin);
        p1data.load(p1data.playerDataFile(p1));
        if (p1data.getString("user.team") == null) { return false; }
        String p1team = p1data.getString("user.team");
        p1data.close();

        FileUtil p2data = new FileUtil(this.plugin);
        p2data.load(p2data.playerDataFile(p2));
        if (p2data.getString("user.team") == null) { return false; }
        String p2team = p2data.getString("user.team");
        p2data.close();

        return (p1team.equalsIgnoreCase(p2team));
    }

    /**
     * Gets a rule for a team.
     * @param team String - Team name.
     * @param rule String - Rule name
     * @return boolean - If the rule is set to true or false.
     */
    public boolean getRule(String team, String rule) {
        FileUtil teamData = new FileUtil(this.plugin);
        teamData.load("/data/teams/"+team+".yml");

        boolean result = teamData.getBoolean("rules."+rule);
        teamData.save();

        return result;
    }

    /**
     * Sets a rule for a team.
     * @param team String - Team name.
     * @param rule String - Rule to set.
     * @param value String - Value of a rule (must be "true" or "false")
     * @return boolean - if the operation was successful.
     * @deprecated Use setRule(String, String, boolean) instead.
     */
    @Deprecated
    public boolean setRule(String team, String rule, String value) {
        boolean booleanValue;
        if (value.equalsIgnoreCase("true")) {
            booleanValue = true;
        } else if (value.equalsIgnoreCase("false")) {
            booleanValue = false;
        } else {
            return false;
        }

        FileUtil teamData = new FileUtil(this.plugin);
        teamData.load("/data/teams/"+team+".yml");

        teamData.set("rules."+rule, booleanValue);
        teamData.save();

        return true;
    }

    /**
     * Sets a rule for a team.
     * @param team String - Team name.
     * @param rule String - Rule to set.
     * @param value boolean - Value of a rule.
     * @return boolean - if the operation was successful.
     */
    public boolean setRule(String team, String rule, boolean value) {

        FileUtil teamData = new FileUtil(this.plugin);
        teamData.load("/data/teams/"+team+".yml");

        teamData.set("rules."+rule, value);
        teamData.save();

        return true;
    }

    /**
     * Checks if a team exists.
     * @param team String - Team name
     * @return boolean - If the team exists.
     */
    public boolean exists(String team) {
        FileUtil teamData = new FileUtil(this.plugin);
        return teamData.exists("/data/teams/"+team+".yml");
    }
}
