package net.lewmc.essence.commands.economy;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
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
            if (permission.has("essence.economy.pay")) {
                if (args.length == 2) {
                    DataUtil data = new DataUtil(this.plugin, message);
                    data.load(data.playerDataFile(player));

                    ConfigurationSection cs = data.getSection("economy");
                    double balance = cs.getDouble("balance");

                    double amount = Double.parseDouble(args[1]);

                    if ((balance - amount) >= 0) {
                        cs.set("balance", (balance - amount));
                        data.save();
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if ((p.getName().toLowerCase()).equalsIgnoreCase(args[0])) {
                                LocationUtil locationUtil = new LocationUtil(this.plugin, message);
                                locationUtil.UpdateLastLocation(player);

                                data.load(data.playerDataFile(p));
                                cs = data.getSection("economy");
                                double newBalance = cs.getDouble("balance") + amount;
                                cs.set("balance", newBalance);
                                data.save();

                                message.PrivateMessage("economy", "sent", plugin.getConfig().getString("economy.symbol") + amount, p.getName());
                                message.SendTo(p, "economy", "received", (plugin.getConfig().getString("economy.symbol") + amount), player.getName());
                                return true;
                            }
                        }
                        message.PrivateMessage("generic", "playernotfound");
                    } else {
                        message.PrivateMessage("economy", "insufficientfunds");
                    }
                } else {
                    message.PrivateMessage("economy", "payusage");
                }
                return true;
            } else {
                return permission.not();
            }
        }

        return false;
    }
}