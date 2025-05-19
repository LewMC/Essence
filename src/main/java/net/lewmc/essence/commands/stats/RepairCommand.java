package net.lewmc.essence.commands.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.CommandUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.PermissionHandler;
import net.lewmc.foundry.Logger;
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

    /**
     * Constructor for the RepairCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public RepairCommand(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("repair")) {
            if (!(cs instanceof Player p)) { return new Logger(this.plugin.config).noConsole(); }

            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("repair")) {
                return cmd.disabled();
            }

            PermissionHandler perms = new PermissionHandler(this.plugin, cs);

            if (perms.has("essence.stats.repair")) {
                MessageUtil msg = new MessageUtil(this.plugin, cs);

                ItemStack itemInHand = p.getInventory().getItemInMainHand();

                if (itemInHand != null && itemInHand.getType() != Material.AIR) {
                    if (itemInHand.getItemMeta() instanceof Damageable) {
                        Damageable damageableMeta = (Damageable) itemInHand.getItemMeta();
                        damageableMeta.setDamage(0);
                        itemInHand.setItemMeta(damageableMeta);
                        msg.send("repair","done");
                    } else {
                        msg.send("repair", "invalidtype");
                    }
                } else {
                    msg.send("repair", "invalidtype");
                }
            } else {
                perms.not();
            }
            return true;
        }

        return false;
    }
}