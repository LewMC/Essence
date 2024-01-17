package net.lewmc.essence.commands.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.PermissionHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class FeedCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the FeedCommand class.
     * @param plugin References to the main plugin class.
     */
    public FeedCommand(Essence plugin) {
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
        @NotNull String s, String[] args
    ) {
        if (!(commandSender instanceof Player)) {
            this.log.noConsole();
            return true;
        }
        MessageUtil message = new MessageUtil(commandSender, plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(player, message);

        if (command.getName().equalsIgnoreCase("feed")) {
            if (args.length > 0) {
                if (permission.has("essence.stats.feed.other")) {
                    String pName = args[0];
                    Player p = Bukkit.getPlayer(pName);
                    if (p != null) {
                        message.PrivateMessage("You fed "+p.getName(), false);
                        p.setFoodLevel(20);
                    } else {
                        message.PrivateMessage("Player not found.", true);
                    }
                    return true;
                } else {
                    return permission.not();
                }
            } else {
                if (permission.has("essence.stats.feed")) {
                    player.setFoodLevel(20);
                    message.PrivateMessage("You've been fed!", false);
                    return true;
                } else {
                    return permission.not();
                }
            }
        }

        return true;
    }
}