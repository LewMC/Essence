package net.lewmc.essence.commands.admin;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /invisible command.
 */
public class InvisibleCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the InvisibleCommand class.
     * @param plugin References to the main plugin class.
     */
    public InvisibleCommand(Essence plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            String[] args
    ) {
        if (!(commandSender instanceof Player player)) {
            LogUtil log = new LogUtil(this.plugin);
            log.noConsole();
            return true;
        }

        MessageUtil message = new MessageUtil(commandSender, plugin);
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("invisible")) {
            CommandUtil cmd = new CommandUtil(this.plugin);
            if (cmd.isDisabled("invisible")) {
                return cmd.disabled(message);
            }

            StatsUtil stats = new StatsUtil(this.plugin, player, permission);
            return stats.invisible(true);
        }

        return false;
    }
}
