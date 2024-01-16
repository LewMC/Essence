package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.MessageHandler;
import net.lewmc.essence.Essence;
import net.lewmc.essence.events.PermissionHandler;
import net.lewmc.essence.utils.ConfigUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SetwarpCommand implements CommandExecutor {
    private Essence plugin;

    /**
     * Constructor for the GamemodeCommands class.
     *
     * @param plugin References to the main plugin class.
     */
    public SetwarpCommand(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * @param commandSender Information about who sent the command - player or console.
     * @param command       Information about what command was sent.
     * @param s             Command label - not used here.
     * @param args          The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            this.plugin.getLogger().warning("[Essence] Sorry, you need to be an in-game player to use this command.");
            return true;
        }
        MessageHandler message = new MessageHandler(commandSender, this.plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(player, message);

        if (command.getName().equalsIgnoreCase("setwarp")) {
            if (permission.has("essence.warp.create")) {
                if (args.length == 0) {
                    message.PrivateMessage("Usage: /setwarp <name>", true);
                    return true;
                }
                Location loc = player.getLocation();
                ConfigUtil config = new ConfigUtil(this.plugin, message);
                config.load("warps.yml");

                String warpName = args[0].toLowerCase();

                config.createSection(warpName);

                ConfigurationSection cs = config.getSection(warpName);
                cs.set("X", loc.getX());
                cs.set("Y", loc.getY());
                cs.set("Z", loc.getZ());
                cs.set("world", loc.getWorld().getName());

                // Save the configuration to the file
                config.save();

                message.PrivateMessage("Created warp: " + args[0] + " at your location", false);
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}