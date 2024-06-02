package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.TeamUtil;
import org.bukkit.entity.Entity;
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
        if (event.getEntity() instanceof Player) {
            Player p1 = (Player) event.getEntity();

            Entity p2 = event.getDamager();

            if (p2 instanceof Player) {
                TeamUtil team = new TeamUtil(this.plugin, new MessageUtil(p1, this.plugin));
                if (team.areTeammates(p1, (Player)p2)) {
                    if (!team.getRule(team.getPlayerTeam(p1.getUniqueId()), "allow-friendly-fire")) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
