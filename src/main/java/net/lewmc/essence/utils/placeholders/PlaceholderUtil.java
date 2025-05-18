package net.lewmc.essence.utils.placeholders;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.LogUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * The tag utility replaces tags with preconfigured text (placeholders)
 */
public class PlaceholderUtil {
    private final Essence plugin;
    private final CommandSender cs;

    /**
     * Constructor
     * @param plugin Reference to the main Essence class.
     */
    public PlaceholderUtil(Essence plugin, CommandSender cs) {
        this.plugin = plugin;
        this.cs = cs;
    }

    /**
     * Replaces tags with preconfigured text. This is used to search for and replace multiple placeholders at a time.
     * @param text String - Text to search and replace.
     * @return String - Resulting String
     */
    public String replaceAll(String text) {
        // Old MTs, to remove in 1.10.0
        if (text.contains("{{") && text.contains("}}")) {
            LogUtil lu = new LogUtil(this.plugin);
            lu.warn("You are using Message Tags. These have been deprecated and will be removed in the next version of Essence.");
            lu.warn("Please replace them with Placeholders.");
            lu.warn("For more information, please visit https://wiki.lewmc.net/es-placeholders.html.");
            text = text.replace("{{essence-version}}", this.replaceSingle("version"));
            text = text.replace("{{minecraft-version}}", this.replaceSingle("minecraft_version"));
            text = text.replace("{{time}}", this.replaceSingle("time"));
            text = text.replace("{{date}}", this.replaceSingle("date"));
            text = text.replace("{{datetime}}", this.replaceSingle("datetime"));
            text = text.replace("{{player}}", this.replaceSingle("player"));
        }

        // New placeholder system.
        if (this.plugin.usingPAPI) {
            if (this.cs instanceof Player) {
                return new PAPIUtil().invokePAPI((Player) this.cs, text);
            } else {
                return new PAPIUtil().invokePAPI(null, text);
            }
        } else {
            text = text.replace("%essence_version%", this.replaceSingle("version"));
            text = text.replace("%essence_minecraft_version%", this.replaceSingle("minecraft_version"));
            text = text.replace("%essence_time%", this.replaceSingle("time"));
            text = text.replace("%essence_date%", this.replaceSingle("date"));
            text = text.replace("%essence_datetime%", this.replaceSingle("datetime"));
            text = text.replace("%essence_player%", this.replaceSingle("player"));
        }

        return text;
    }

    /**
     * Converts placeholders into strings. This is used to convert a single placeholder at a time.
     * @param placeholder String - The placeholder to convert (without the braces)
     * @return String - The string the placeholder becomes.
     */
    public String replaceSingle(String placeholder) {
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
            return this.cs.getName();
        } else {
            return placeholder;
        }
    }
}
