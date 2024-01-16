package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.MessageHandler;
import net.lewmc.essence.Essence;
import net.lewmc.essence.events.PermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandExecutor {
    private Essence plugin;

    /**
     * Constructor for the GamemodeCommands class.
     * @param plugin References to the main plugin class.
     */
    public TeleportCommand(Essence plugin) {
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
        MessageHandler message = new MessageHandler(commandSender);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(player, message);

        if (command.getName().equalsIgnoreCase("tp") || command.getName().equalsIgnoreCase("teleport")) {
            if (args.length == 3) {
                if (permission.has("essence.teleport.coord")) {
                    double x = Double.parseDouble(args[0]);
                    double y = Double.parseDouble(args[1]);
                    double z = Double.parseDouble(args[2]);
                    Location location = new Location(player.getWorld(), x, y, z);
                    player.teleport(location);
                } else {
                    permission.not();
                }
                return true;
            } else if (args.length == 1) {
                if (permission.has("essence.teleport.player")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if ((p.getName().toLowerCase()).equalsIgnoreCase(args[0])) {
                            message.PrivateMessage("Teleporting to " + p.getName(), false);
                            player.teleport(p);
                            return true;
                        }
                    }
                } else {
                    permission.not();
                }
                message.PrivateMessage("Player not found.", true);
                return true;
            } else {
                message.PrivateMessage("Please enter coordinates or a player's name.", true);
            }
            return true;
        }

        return false;
    }
}