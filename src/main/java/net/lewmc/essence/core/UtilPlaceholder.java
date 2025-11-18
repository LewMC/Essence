package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import net.lewmc.essence.team.UtilTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

/**
 * The tag utility replaces tags with preconfigured text (placeholders)
 */
public class UtilPlaceholder {
    private final Essence plugin;
    private final CommandSender cs;

    /**
     * Constructor
     * @param plugin Reference to the main Essence class.
     */
    public UtilPlaceholder(Essence plugin, CommandSender cs) {
        this.plugin = plugin;
        this.cs = cs;
    }

    /**
     * Replaces tags with preconfigured text. This is used to search for and replace multiple placeholders at a time.
     * @param text String - Text to search and replace.
     * @return String - Resulting String
     */
    public String replaceAll(String text) {
        if (this.plugin.integrations.PAPIEnabled) {
            if (this.cs instanceof Player) {
                return new UtilPlaceholderAPI().invokePAPI((Player) this.cs, text);
            } else {
                return new UtilPlaceholderAPI().invokePAPI(null, text);
            }
        } else {
            text = text.replace("%essence_version%", this.replaceSingle("version"));
            text = text.replace("%essence_minecraft_version%", this.replaceSingle("minecraft_version"));
            text = text.replace("%essence_time%", this.replaceSingle("time"));
            text = text.replace("%essence_date%", this.replaceSingle("date"));
            text = text.replace("%essence_datetime%", this.replaceSingle("datetime"));
            text = text.replace("%essence_player%", this.replaceSingle("player"));
            text = text.replace("%essence_username%", this.replaceSingle("username"));
            text = text.replace("%essence_team%", this.replaceSingle("team_name"));
            text = text.replace("%essence_team_name%", this.replaceSingle("team_name"));
            text = text.replace("%essence_team_leader%", this.replaceSingle("team_leader"));
            text = text.replace("%essence_team_prefix%", this.replaceSingle("team_prefix"));
            text = text.replace("%essence_combined_prefix%", this.replaceSingle("combined_prefix"));
            text = text.replace("%essence_player_prefix%", this.replaceSingle("player_prefix"));
            text = text.replace("%essence_player_suffix%", this.replaceSingle("player_suffix"));
            text = text.replace("%essence_balance%", this.replaceSingle("balance"));
        }

        return text;
    }

    /**
     * Converts placeholders into strings. This is used to convert a single placeholder at a time.
     * @param placeholder String - The placeholder to convert (without the braces)
     * @return String - The string the placeholder becomes.
     */
    public String replaceSingle(String placeholder) {
        UtilTeam tu = new UtilTeam(this.plugin, new UtilMessage(this.plugin, this.cs));

        if (placeholder.equalsIgnoreCase("version")) {
            return this.plugin.getDescription().getVersion();
        } else if (placeholder.equalsIgnoreCase("minecraft_version")) {
            String mcVersion = this.plugin.getServer().getBukkitVersion();
            String[] mcVersionArray = mcVersion.split("-");
            return mcVersionArray[0];
        } else if (placeholder.equalsIgnoreCase("time")) {
            return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        } else if (placeholder.equalsIgnoreCase("date")) {
            return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        } else if (placeholder.equalsIgnoreCase("datetime")) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        } else if (placeholder.equalsIgnoreCase("player")) {
            if ((boolean) this.plugin.config.get("chat.manage-chat")) {
                return new UtilPlayer(this.plugin).getDisplayname(this.cs);
            } else {
                return cs.getName();
            }
        } else if (placeholder.equalsIgnoreCase("username")) {
            return cs.getName();
        } else if (placeholder.equalsIgnoreCase("team_name")) {
            if (this.cs instanceof Player p) {
                return Objects.requireNonNullElse(tu.getPlayerTeam(p.getUniqueId()), "No team");
            } else {
                return "No team";
            }
        } else if (placeholder.equalsIgnoreCase("team_leader")) {
            if (this.cs instanceof Player p) {
                return Objects.requireNonNullElse(tu.getTeamLeader(tu.getPlayerTeam(p.getUniqueId())), "No leader");
            } else {
                return "No leader";
            }
        } else if (placeholder.equalsIgnoreCase("team_prefix")) {
            return tu.getTeamPrefix(this.cs);
        } else if (placeholder.equalsIgnoreCase("combined_prefix")) {
            return new UtilPlayer(this.plugin).getPlayerPrefix(cs) + tu.getTeamPrefix(this.cs);
        } else if (placeholder.equalsIgnoreCase("player_prefix")) {
            return new UtilPlayer(this.plugin).getPlayerPrefix(cs);
        } else if (placeholder.equalsIgnoreCase("player_suffix")) {
            return new UtilPlayer(this.plugin).getPlayerSuffix(cs);
        } else if (placeholder.equalsIgnoreCase("balance")) {
            if (cs instanceof Player p) {
                TypePlayer playerData = this.plugin.players.get(p.getUniqueId());
                if (playerData != null) {
                    return this.plugin.config.get("economy.symbol").toString() + playerData.economy.balance;
                } else {
                    return this.plugin.config.get("economy.symbol").toString() + "0.0";
                }
            } else {
                return this.plugin.config.get("economy.symbol").toString() + "Infinity";
            }
        } else {
            return placeholder;
        }
    }
}
