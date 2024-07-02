package net.lewmc.essence.commands.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.PermissionHandler;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

public class RepairCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the RepairCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public RepairCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
    }

    /**
     * @param commandSender Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(commandSender instanceof Player)) {
            this.log.noConsole();
            return true;
        }

        MessageUtil message = new MessageUtil(commandSender, plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("repair")) {
            if (permission.has("essence.stats.repair")) {
                ItemStack itemInHand = player.getInventory().getItemInMainHand();

                if (itemInHand != null && itemInHand.getType() != Material.AIR) {
                    if (itemInHand.getItemMeta() instanceof Damageable) {
                        Damageable damageableMeta = (Damageable) itemInHand.getItemMeta();
                        damageableMeta.setDamage(0);
                        itemInHand.setItemMeta(damageableMeta);
                        message.send("repair","done");
                    } else {
                        message.send("repair", "invalidtype");
                    }
                } else {
                    message.send("repair", "invalidtype");
                }
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}