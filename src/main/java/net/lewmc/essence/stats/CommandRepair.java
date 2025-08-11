package net.lewmc.essence.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class CommandRepair extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the RepairCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandRepair(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.stats.repair";
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
        UtilMessage msg = new UtilMessage(this.plugin, cs);

        ItemStack itemInHand = ((Player) cs).getInventory().getItemInMainHand();

        if (itemInHand.getType() != Material.AIR) {
            if (itemInHand.getItemMeta() instanceof Damageable damageableMeta) {
                damageableMeta.setDamage(0);
                itemInHand.setItemMeta(damageableMeta);
                msg.send("repair", "done");
            } else {
                msg.send("repair", "invalidtype");
            }
        } else {
            msg.send("repair", "invalidtype");
        }
        return true;
    }
}