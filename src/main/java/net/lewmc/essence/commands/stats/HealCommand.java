package net.lewmc.essence.commands.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.MessageHandler;
import net.lewmc.essence.utils.PermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {
    private Essence plugin;

    /**
     * Constructor for the GamemodeCommands class.
     * @param plugin References to the main plugin class.
     */
    public HealCommand(Essence plugin) {
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
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            plugin.getLogger().warning("[Essence] Sorry, you need to be an in-game player to use this command.");
            return true;
        }
        MessageHandler message = new MessageHandler(commandSender, plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(player, message);

        if (command.getName().equalsIgnoreCase("heal")) {
            if (args.length > 0) {
                if (permission.has("essence.stats.heal.other")) {
                    String pName = args[0];
                    Player p = Bukkit.getPlayer(pName);
                    if (p != null) {
                        message.PrivateMessage("You healed "+p.getName(), false);
                        p.setHealth(20.0);
                    } else {
                        message.PrivateMessage("Player not found.", true);
                    }
                    return true;
                } else {
                    return permission.not();
                }
            } else {
                if (permission.has("essence.stats.heal")) {
                    player.setHealth(20.0);
                    message.PrivateMessage("You've been healed!", false);
                    return true;
                } else {
                    return permission.not();
                }
            }
        }

        return true;
    }
}