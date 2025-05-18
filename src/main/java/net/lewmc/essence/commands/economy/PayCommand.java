package net.lewmc.essence.commands.economy;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import net.lewmc.essence.utils.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PayCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the PayCommand class.
     * @param plugin References to the main plugin class.
     */
    public PayCommand(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * /pay command handler
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
        if (command.getName().equalsIgnoreCase("pay")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("pay")) { return cmd.disabled(); }

            PermissionHandler permission = new PermissionHandler(this.plugin, cs);

            if (permission.has("essence.economy.pay")) {
                MessageUtil msg = new MessageUtil(this.plugin, cs);
                if (args.length == 2) {
                    Economy sender = new Economy(this.plugin, cs);
                    double amount = Double.parseDouble(args[1]);

                    if ((sender.balance() - amount) >= 0) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if ((p.getName().toLowerCase()).equalsIgnoreCase(args[0])) {
                                Economy recipient = new Economy(this.plugin, p);

                                recipient.balanceAdd(amount);
                                sender.balanceSubtract(amount);

                                msg.send("economy", "sent", new String[] { this.plugin.economySymbol + amount, p.getName() });
                                msg.sendTo(p, "economy", "received", new String[] { this.plugin.economySymbol + amount, p.getName() });
                                return true;
                            }
                        }
                        msg.send("generic", "playernotfound");
                    } else {
                        msg.send("economy", "insufficientfunds");
                    }
                } else {
                    msg.send("economy", "payusage");
                }
                return true;
            } else {
                return permission.not();
            }
        }

        return false;
    }
}