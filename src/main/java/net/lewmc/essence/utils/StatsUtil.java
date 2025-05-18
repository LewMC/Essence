package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Manages player statistics.
 */
public class StatsUtil {
    private final Player player;
    private final Essence plugin;

    public StatsUtil(Essence plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    /**
     * Sets the player to be invisible.
     * @return boolean - Was the operation successful?
     */
    public boolean toggleInvisible() {
        MessageUtil msg = new MessageUtil(this.plugin, this.player);
        if (!this.player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            this.player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
            msg.send("visibility", "invisible", new String[]{this.player.getName()});
        } else {
            this.player.removePotionEffect(PotionEffectType.INVISIBILITY);
            msg.send("visibility", "visible", new String[]{this.player.getName()});
        }
        return true;
    }
}
