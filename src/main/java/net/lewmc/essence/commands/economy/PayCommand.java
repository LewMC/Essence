package net.lewmc.essence.commands.economy;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PayCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the PayCommand class.
     * @param plugin References to the main plugin class.
     */
    public PayCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
    }

    /**
     * /pay command handler
     * @param commandSender Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            String[] args
    ) {
        if (!(commandSender instanceof Player)) {
            this.log.noConsole();
            return true;
        }
        MessageUtil message = new MessageUtil(commandSender, plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("pay")) {
            CommandUtil cmd = new CommandUtil(this.plugin);
            if (cmd.isDisabled("pay")) {
                return cmd.disabled(message);
            }

            if (permission.has("essence.economy.pay")) {
                if (args.length == 2) {
                    FileUtil senderDataFile = new FileUtil(this.plugin);
                    senderDataFile.load(senderDataFile.playerDataFile(player));

                    double balance = senderDataFile.getDouble("economy.balance");

                    double amount = Double.parseDouble(args[1]);

                    if ((balance - amount) >= 0) {
                        senderDataFile.set("economy.balance", (balance - amount));
                        senderDataFile.save();
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if ((p.getName().toLowerCase()).equalsIgnoreCase(args[0])) {
                                FileUtil recieverDataFile = new FileUtil(this.plugin);

                                recieverDataFile.load(senderDataFile.playerDataFile(p));
                                double newBalance = recieverDataFile.getDouble("economy.balance") + amount;
                                recieverDataFile.set("economy.balance", newBalance);
                                recieverDataFile.save();

                                message.send("economy", "sent", new String[] { plugin.getConfig().getString("economy.symbol") + amount, p.getName() });
                                message.sendTo(p, "economy", "received", new String[] { (plugin.getConfig().getString("economy.symbol") + amount), player.getName() });
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
            } else {
                return permission.not();
            }
        }

        return false;
    }
}