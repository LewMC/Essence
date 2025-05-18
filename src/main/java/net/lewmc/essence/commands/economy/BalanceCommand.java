package net.lewmc.essence.commands.economy;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BalanceCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the BalanceCommand class.
     * @param plugin References to the main plugin class.
     */
    public BalanceCommand(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * /bal command handler.
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
            @NotNull CommandSender cs,
            @NotNull Command command,
            @NotNull String s,
            String[] args
    ) {
        if (command.getName().equalsIgnoreCase("balance")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("balance")) { return cmd.disabled(); }

            PermissionHandler permission = new PermissionHandler(this.plugin, cs);

            if (permission.has("essence.economy.balance")) {
                MessageUtil msg = new MessageUtil(this.plugin, cs);
                if (cs instanceof Player p) {
                    FileUtil pf = new FileUtil(this.plugin);
                    pf.load(pf.playerDataFile(p));
                    msg.send("economy", "balance", new String[]{this.plugin.economySymbol + pf.getDouble("economy.balance")});
                } else {
                    msg.send("economy", "balance", new String[]{this.plugin.economySymbol + "Infinity"});
                }
                return true;
            } else {
                return permission.not();
            }
        }

        return false;
    }
}