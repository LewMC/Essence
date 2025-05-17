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
    private final PermissionHandler permission;
    private final Essence plugin;

    public StatsUtil(Essence plugin, Player player, PermissionHandler permission) {
        this.plugin = plugin;
        this.player = player;
        this.permission = permission;
    }

    /**
     * Sets the player to be invisible.
     * @return boolean - Was the operation successful?
     */
    public boolean toggleInvisible() {
            if (this.permission.has("essence.admin.invisible")) {
                if (!this.player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    this.player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                    MessageUtil message = new MessageUtil(this.player, this.plugin);
                    message.send("visibility", "invisible", new String[]{this.player.getName()});
                } else {
                    this.player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    MessageUtil message = new MessageUtil(this.player, this.plugin);
                    message.send("visibility", "visible", new String[]{this.player.getName()});
                }
                return true;
            } else {
                return this.permission.not();
            }
    }
}
