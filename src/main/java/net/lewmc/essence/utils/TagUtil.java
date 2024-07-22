package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * The tag utility replaces tags with preconfigured text (placeholders)
 */
public class TagUtil {
    private final Essence plugin;
    private final String playerName;

    /**
     * Constructor
     * @param plugin Reference to the main Essence class.
     */
    public TagUtil(Essence plugin, Player player) {
        this.plugin = plugin;
        this.playerName = player.getName();
    }

    /**
     * Replaces tags with preconfigured text.
     * @param text String - Text to search and replace.
     * @return String - Resulting String
     */
    public String doReplacement(String text) {
        String mcVersion = this.plugin.getServer().getBukkitVersion();
        String[] mcVersionArray = mcVersion.split("-");

        text = text.replace("{{essence-version}}", this.plugin.getDescription().getVersion());
        text = text.replace("{{minecraft-version}}", mcVersionArray[0]);
        text = text.replace("{{time}}", new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        text = text.replace("{{date}}", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
        text = text.replace("{{datetime}}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
        text = text.replace("{{player}}", this.playerName);

        return text;
    }
}
