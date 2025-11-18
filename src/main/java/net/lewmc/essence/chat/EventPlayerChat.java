package net.lewmc.essence.chat;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilPlaceholder;
import net.lewmc.essence.core.UtilPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

/**
 * PlayerChatEvent fires when a player sends a message in chat.
 */
public class EventPlayerChat implements Listener {
    private final Essence plugin;

    /**
     * Constructs the class.
     * @param plugin Reference to the main Essence class.
     */
    public EventPlayerChat(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Fires when a player sends a message in chat.
     * @param event The AsyncPlayerChatEvent event.
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if ((boolean) this.plugin.config.get("chat.manage-chat")) {
            String msg = new UtilPlaceholder(this.plugin, event.getPlayer()).replaceAll(this.plugin.config.get("chat.name-format") + " " + event.getMessage());

            if ((boolean) this.plugin.config.get("chat.allow-message-formatting")) {
                msg = ChatColor.translateAlternateColorCodes('&', msg);
            }

            // Escape %
            msg = msg.replace("%", "%%");

            event.setMessage(msg);
            event.setFormat(msg);

            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                List<String> ignoring = (List<String>)new UtilPlayer(this.plugin).getPlayer(p.getUniqueId(), UtilPlayer.KEYS.USER_IGNORING_PLAYERS);
                if (!ignoring.contains(p.getUniqueId().toString())) {
                    p.sendMessage(msg);
                }
            }

            event.setCancelled(true);
        }
    }
}
