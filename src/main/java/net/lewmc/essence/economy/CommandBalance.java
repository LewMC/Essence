package net.lewmc.essence.economy;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("balance")) { return cmd.disabled(); }

        if (cs instanceof Player p) {
            Files pf = new Files(this.plugin.config, this.plugin);
            pf.load(pf.playerDataFile(p));
            new UtilMessage(this.plugin, cs).send("economy", "balance", new String[]{this.plugin.economySymbol + pf.getDouble("economy.balance")});
        } else {
            new UtilMessage(this.plugin, cs).send("economy", "balance", new String[]{this.plugin.economySymbol + "Infinity"});
        }
        return true;
    }
}