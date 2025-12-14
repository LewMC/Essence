package net.lewmc.essence.inventory;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

/**
 * /invsee command.
 */
public class CommandInvsee extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandInvsee class.
     */
    public CommandInvsee(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission
     * @return String - the permission string
     */
    @Override
    protected String requiredPermission() {
        return "essence.invsee";
    }

    /**
     * @param cs       Information about who sent the command - player or console.
     * @param command  Information about what command was sent.
     * @param s        Command label - not used here.
     * @param args     The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage msg = new UtilMessage(this.plugin, cs);
        if (args.length == 1 && cs instanceof Player p) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target != null && target.getPlayer() != null) {
                try {
                    Player targetPlayer = target.getPlayer();
                    PlayerInventory targetInv = targetPlayer.getInventory();

                    Inventory readOnlyInv = Bukkit.createInventory(
                            new TypeReadOnlyInventory(),
                            45,
                            targetPlayer.getName() + "'s Inventory"
                    );

                    readOnlyInv.setContents(targetInv.getContents());

                    readOnlyInv.setItem(36, targetInv.getHelmet());
                    readOnlyInv.setItem(37, targetInv.getChestplate());
                    readOnlyInv.setItem(38, targetInv.getLeggings());
                    readOnlyInv.setItem(39, targetInv.getBoots());
                    readOnlyInv.setItem(40, targetInv.getItemInOffHand());

                    p.openInventory(readOnlyInv);

                } catch (Exception e) {
                    msg.send("generic", "exception");
                    this.plugin.log.warn(e.getMessage());
                }
            } else {
                msg.send("generic", "playernotfound");
            }
        } else {
            msg.send("invsee", "usage");
        }
        return true;
    }
}