package net.lewmc.essence.economy;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPlayer;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /bal command.
 */
public class CommandBalance extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the BalanceCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandBalance(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.economy.balance";
    }

    /**
     * /bal command handler.
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        if (cs instanceof Player p) {
            new UtilMessage(this.plugin, cs).send("economy", "balance", new String[]{this.plugin.config.get("economy.symbol").toString() + new UtilPlayer(this.plugin).getPlayer(p.getUniqueId(), UtilPlayer.KEYS.ECONOMY_BALANCE)});
        } else {
            new UtilMessage(this.plugin, cs).send("economy", "balance", new String[]{this.plugin.config.get("economy.symbol").toString() + "Infinity"});
        }
        return true;
    }
}