package net.lewmc.essence.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandEnchant extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandEnchant class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandEnchant(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.stats.enchant";
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

        if (args.length == 2) {
            ItemStack item = ((Player) cs).getInventory().getItemInMainHand();
            Enchantment enc = Enchantment.getByKey(NamespacedKey.minecraft(args[0]));

            if (enc != null) {
                if (enc.canEnchantItem(item)) {
                    item.addEnchantment(enc, Integer.parseInt(args[1]));
                    msg.send("enchant", "done");
                } else {
                    msg.send("enchant", "invalidtype");
                }
            } else {
                msg.send("enchant", "notfound");
            }
        } else {
            msg.send("enchant", "usage");
        }
        return true;
    }
}