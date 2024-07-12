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
    private final LogUtil log;

    /**
     * Constructor for the BalanceCommand class.
     * @param plugin References to the main plugin class.
     */
    public BalanceCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
    }

    /**
     * /bal command handler.
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
        MessageUtil message = new MessageUtil(commandSender, this.plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("balance")) {
            CommandUtil cmd = new CommandUtil(this.plugin);
            if (cmd.isDisabled("balance")) {
                return cmd.disabled(message);
            }

            if (permission.has("essence.economy.balance")) {
                FileUtil data = new FileUtil(this.plugin);
                data.load(data.playerDataFile(player));

                double balance = data.getDouble("economy.balance");
                data.close();

                message.send("economy","balance", new String[] { plugin.getConfig().getString("economy.symbol") + balance });
                return true;
            } else {
                return permission.not();
            }
        }

        return false;
    }
}