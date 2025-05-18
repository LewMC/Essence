package net.lewmc.essence.commands.admin;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /visible command.
 */
public class InvisibleCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the InvisibleCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public InvisibleCommand(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The Invisible command.
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

        if (command.getName().equalsIgnoreCase("invisible")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("invisible")) { return cmd.disabled(); }

            if (!(cs instanceof Player)) { return new LogUtil(this.plugin).noConsole(); }

            PermissionHandler perm = new PermissionHandler(this.plugin, cs);
            if (!perm.has("essence.admin.invisible")) { return perm.not(); }

            return new StatsUtil(this.plugin, (Player) cs).toggleInvisible();
        }

        return false;
    }
}
