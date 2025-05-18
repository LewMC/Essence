package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.placeholders.PlaceholderUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * PlayerChatEvent fires when a player sends a message in chat.
 */
public class PlayerChatEvent implements Listener {
    private final Essence plugin;

    /**
     * Constructs the class.
     * @param plugin Reference to the main Essence class.
     */
    public PlayerChatEvent(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Fires when a player sends a message in chat.
     * @param event The AsyncPlayerChatEvent event.
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (this.plugin.chat_manage) {
            String msg = event.getMessage();

            msg = new PlaceholderUtil(this.plugin, event.getPlayer()).replaceAll(this.plugin.chat_nameFormat + " " + msg);

            if (this.plugin.chat_allowMessageFormatting) {
                msg = ChatColor.translateAlternateColorCodes('&', msg);
            }

            // Escape %
            msg = msg.replace("%", "%%");

            event.setMessage(msg);
            event.setFormat(msg);
        }
    }
}
