package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.TeamUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * PlayerDamageEvent class.
 */
public class PlayerDamageEvent implements Listener {
    private final Essence plugin;

    /**
     * Constructor for the PlayerDamageEvent class.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public PlayerDamageEvent(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for when an entity is damaged by another entity.
     * @param event EntityDamageByEntityEvent - Server thrown event.
     */
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player p1) {
            if (event.getDamager() instanceof Player p2) {
                TeamUtil team = new TeamUtil(this.plugin, new MessageUtil(this.plugin, p1));
                if (team.areTeammates(p1, p2)) {
                    if (!team.getRule(team.getPlayerTeam(p1.getUniqueId()), "allow-friendly-fire")) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
