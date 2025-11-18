package net.lewmc.essence.economy;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.TypePlayer;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPlayer;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPay extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the PayCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandPay(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.economy.pay";
    }

    /**
     * /pay command handler
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage message = new UtilMessage(this.plugin, cs);
        if (args.length == 2) {
            try {
                double amount = Double.parseDouble(args[1]);
                if (amount <= 0) {
                    message.send("economy", "positiveamount");
                    return true;
                }
                UtilPlayer up = new UtilPlayer(plugin);

                if (cs instanceof Player sender) {
                    TypePlayer senderData = plugin.players.get(sender.getUniqueId());
                    if (senderData == null) {
                        message.send("generic", "error");
                        return true;
                    }

                    if ((senderData.economy.balance - amount) >= 0) {
                        senderData.economy.balance = senderData.economy.balance - amount;
                        plugin.players.put(sender.getUniqueId(), senderData);

                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if ((p.getName().toLowerCase()).equalsIgnoreCase(args[0])) {
                                TypePlayer receiverData = plugin.players.get(p.getUniqueId());
                                if (receiverData == null) {
                                    message.send("generic", "playernotfound");
                                    continue;
                                }
                                receiverData.economy.balance = receiverData.economy.balance + amount;

                                this.plugin.players.put(p.getUniqueId(), receiverData);

                                message.send("economy", "sent", new String[]{plugin.config.get("economy.symbol").toString() + amount, p.getName()});
                                message.sendTo(p, "economy", "received", new String[]{(plugin.config.get("economy.symbol").toString() + amount), cs.getName()});
                                up.savePlayer(sender);
                                up.savePlayer(p);
                                return true;
                            } else {
                                message.send("generic", "playernotfound");
                            }
                        }
                        message.send("generic", "playernotfound");
                    } else {
                        message.send("economy", "insufficientfunds");
                    }
                } else {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if ((p.getName().toLowerCase()).equalsIgnoreCase(args[0])) {
                            TypePlayer receiverData = plugin.players.get(p.getUniqueId());
                            if (receiverData == null) {
                                message.send("generic", "playernotfound");
                                continue;
                            }
                            receiverData.economy.balance = receiverData.economy.balance + amount;

                            this.plugin.players.put(p.getUniqueId(), receiverData);

                            message.send("economy", "sent", new String[]{plugin.config.get("economy.symbol").toString() + amount, p.getName()});
                            message.sendTo(p, "economy", "received", new String[]{(plugin.config.get("economy.symbol").toString() + amount), cs.getName()});
                            up.savePlayer(p);
                            return true;
                        } else {
                            message.send("generic", "playernotfound");
                        }
                    }
                    message.send("generic", "playernotfound");
                }
            } catch (NumberFormatException e) {
                message.send("economy", "invalidamount");
                return true;
            }
        } else {
            message.send("economy", "payusage");
        }
        return true;
    }
}