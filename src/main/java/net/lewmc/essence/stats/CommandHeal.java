package net.lewmc.essence.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CommandHeal extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the HealCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandHeal(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.stats.heal";
    }

    /**
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage message = new UtilMessage(this.plugin, cs);

        if (args.length > 0) {
            return this.healOther(new UtilPermission(this.plugin, cs), cs, message, args);
        } else {
            if (!(cs instanceof Player)) {
                message.send("heal","usage");
                return true;
            }

            return this.healSelf(cs, message);
        }
    }

    /**
     * Applies healing effects to a player
     * @param player - The player to heal
     */
    private void healPlayer(Player player) {
        player.setHealth((Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue()));
        player.setFoodLevel(20);
        player.setSaturation(10);
        player.setExhaustion(0F);
        player.setFireTicks(0);
        player.setRemainingAir(player.getMaximumAir());
        player.clearActivePotionEffects();
    }

    /**
     * Heals the command sender.
     * @param sender CommandSender - The user to heal.
     * @param message MessageUtil - The messaging system.
     * @return boolean - If the operation was successful
     */
    private boolean healSelf(CommandSender sender, UtilMessage message) {
        Player player = (Player) sender;
        healPlayer(player);
        message.send("heal", "beenhealed");
        return true;
    }

    /**
     * Heals another user.
     * @param permission PermissionHandler - The permission system.
     * @param sender CommandSender - The user to heal.
     * @param message MessageUtil - The messaging system.
     * @param args String[] - List of command arguments.
     * @return boolean - If the operation was successful
     */
    private boolean healOther(UtilPermission permission, CommandSender sender, UtilMessage message, String[] args) {
        if (permission.has("essence.stats.heal.other")) {
            String pName = args[0];
            Player p = Bukkit.getPlayer(pName);
            if (p != null) {
                message.send("heal", "healed", new String[] { p.getName() });
                if (!(sender instanceof Player)) {
                    message.sendTo(p, "heal", "serverhealed");
                } else {
                    message.sendTo(p, "heal", "healedby", new String[] { sender.getName() });
                }
                healPlayer(p);
            } else {
                message.send("generic", "playernotfound");
            }
            return true;
        } else {
            return permission.not();
        }
    }
}