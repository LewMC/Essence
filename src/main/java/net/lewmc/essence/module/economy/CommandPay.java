package net.lewmc.essence.module.economy;

import net.lewmc.essence.Essence;
import net.lewmc.essence.global.UtilCommand;
import net.lewmc.essence.global.UtilMessage;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPay extends FoundryPlayerCommand {
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
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("pay")) { return cmd.disabled(); }

        UtilMessage message = new UtilMessage(this.plugin, cs);
        if (args.length == 2) {
            Files senderDataFile = new Files(this.plugin.config, this.plugin);
            senderDataFile.load(senderDataFile.playerDataFile((Player) cs));

            double balance = senderDataFile.getDouble("economy.balance");

            double amount = Double.parseDouble(args[1]);

            if ((balance - amount) >= 0) {
                senderDataFile.set("economy.balance", (balance - amount));
                senderDataFile.save();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if ((p.getName().toLowerCase()).equalsIgnoreCase(args[0])) {
                        Files recieverDataFile = new Files(this.plugin.config, this.plugin);

                        recieverDataFile.load(senderDataFile.playerDataFile(p));
                        double newBalance = recieverDataFile.getDouble("economy.balance") + amount;
                        recieverDataFile.set("economy.balance", newBalance);
                        recieverDataFile.save();

                        message.send("economy", "sent", new String[] { plugin.getConfig().getString("economy.symbol") + amount, p.getName() });
                        message.sendTo(p, "economy", "received", new String[] { (plugin.getConfig().getString("economy.symbol") + amount), cs.getName() });
                        return true;
                    }
                }
                message.send("generic", "playernotfound");
            } else {
                message.send("economy", "insufficientfunds");
            }
        } else {
            message.send("economy", "payusage");
        }
        return true;
    }
}