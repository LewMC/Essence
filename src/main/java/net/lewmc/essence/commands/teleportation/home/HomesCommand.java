package net.lewmc.essence.commands.teleportation.home;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HomesCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the HomesCommand class.
     * @param plugin References to the main plugin class.
     */
    public HomesCommand(Essence plugin) {
        this.plugin = plugin;
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
            LogUtil log = new LogUtil(this.plugin);
            log.noConsole();
            return true;
        }
        MessageUtil message = new MessageUtil(commandSender, plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("homes")) {
            if (permission.has("essence.home.list")) {
                HomeUtil hu = new HomeUtil(this.plugin);
                StringBuilder setHomes = hu.getHomesList(player);

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