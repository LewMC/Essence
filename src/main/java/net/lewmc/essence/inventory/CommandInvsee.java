package net.lewmc.essence.inventory;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

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
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean  true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage msg = new UtilMessage(this.plugin, cs);
        if(args.length == 1) {
            Player p = (Player) cs;

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target != null) {
                try {
                    PlayerInventory targetI = Objects.requireNonNull(target.getPlayer()).getInventory();
                    p.openInventory(targetI);
                } catch (Exception e) {
                    msg.send("generic","exception");
                    this.plugin.log.warn("Exception while trying to open inventory");
                    this.plugin.log.warn(e.getMessage());
                }
            } else {
                msg.send("generic","exception");
                this.plugin.log.warn("Exception while trying to open inventory: target is null.");
            }
        } else {
            msg.send("invsee","usage");
        }
        return true;
    }
}