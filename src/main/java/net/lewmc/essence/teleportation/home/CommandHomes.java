package net.lewmc.essence.teleportation.home;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandHomes implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the HomesCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandHomes(Essence plugin) {
        this.plugin = plugin;
    }

    /**
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
        if (command.getName().equalsIgnoreCase("homes")) {
            UtilCommand cmd = new UtilCommand(this.plugin, cs);
            if (cmd.isDisabled("homes")) { return cmd.disabled(); }

            if (!(cs instanceof Player p)) { return new Logger(this.plugin.config).noConsole(); }

            UtilPermission permission = new UtilPermission(this.plugin, cs);

            if (permission.has("essence.home.list")) {
                UtilMessage message = new UtilMessage(this.plugin, cs);

                UtilHome hu = new UtilHome(this.plugin);
                StringBuilder setHomes = hu.getHomesList(p);

                if (setHomes == null) {
                    message.send("home", "noneset");
                    return true;
                }

                message.send("home", "list", new String[] { setHomes.toString() });
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}