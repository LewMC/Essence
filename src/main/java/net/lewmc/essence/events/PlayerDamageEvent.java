package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.TeamUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageEvent implements Listener {
    private final Essence plugin;

    public PlayerDamageEvent(Essence plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p1 = (Player) e.getEntity();

            Entity p2 = e.getDamager();

            if (p2 instanceof Player) {
                TeamUtil team = new TeamUtil(this.plugin, new MessageUtil(p1, this.plugin));
                if (team.areTeammates(p1, (Player)p2)) {
                    if (!team.getRule(team.getPlayerTeam(p1.getUniqueId()), "allow-friendly-fire")) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
