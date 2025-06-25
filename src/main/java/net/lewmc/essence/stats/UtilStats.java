package net.lewmc.essence.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Manages player statistics.
 */
public class UtilStats {
    private final Player player;
    private final Essence plugin;

    /**
     * Constructor for the UtilStats class.
     * @param plugin Essence - Main plugin class.
     * @param p Player - The player
     */
    public UtilStats(Essence plugin, Player p) {
        this.plugin = plugin;
        this.player = p;
    }

    /**
     * Sets the player to be invisible.
     * @return boolean - Was the operation successful?
     */
    public boolean toggleInvisible() {
        UtilMessage msg = new UtilMessage(this.plugin, this.player);
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
