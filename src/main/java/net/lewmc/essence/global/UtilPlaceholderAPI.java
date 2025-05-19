package net.lewmc.essence.global;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

/**
 * Utility for using PlaceholderAPI.
 */
public class UtilPlaceholderAPI {

    /**
     * Invokes PlaceholderAPI.
     * @param player Player - The player who invoked PAPI.
     * @param text String - The text to translate placeholders for.
     * @return String - The translated text.
     */
    public String invokePAPI(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
