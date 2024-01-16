package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.MessageHandler;
import net.lewmc.essence.Essence;
import net.lewmc.essence.events.PermissionHandler;
import net.lewmc.essence.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {
    private Essence plugin;

    /**
     * Constructor for the GamemodeCommands class.
     * @param plugin References to the main plugin class.
     */
    public WarpCommand(Essence plugin) {
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

        if (command.getName().equalsIgnoreCase("warp")) {
            if (args.length > 0) {
                if (permission.has("essence.warp.use")) {
                    ConfigUtil config = new ConfigUtil(this.plugin, message);
                    config.load("warps.yml");

                    if (config.getSection(args[0].toLowerCase()) == null) {
                        message.PrivateMessage("Warp " + args[0].toLowerCase() + " does not exist. Use /warp for a list of warps.", true);
                        return true;
                    }

                    ConfigurationSection cs = config.getSection(args[0].toLowerCase());

                    if (cs.getString("world") == null) {
                        message.PrivateMessage("Unable to warp due to an unexpected error, please see console for details.", true);
                        plugin.getLogger().warning("[Essence] Player "+player+" attempted to warp to "+args[0].toLowerCase()+" but couldn't due to an error.");
                        plugin.getLogger().warning("[Essence] Error: world is null, please check configuration file.");
                        return true;
                    }

                    Location loc = new Location(
                        Bukkit.getServer().getWorld(cs.getString("world")),
                        cs.getDouble("X"),
                        cs.getDouble("Y"),
                        cs.getDouble("Z")
                    );

                    player.teleport(loc);

                    message.PrivateMessage("Warping to "+args[0].toLowerCase()+"...", false);

                    return true;
                } else {
                    permission.not();
                }
            } else {
                if (permission.has("essence.warp.list")) {
                    message.PrivateMessage("This command is temporarily unavailable due to technical issues.", true);
                } else {
                    permission.not();
                }
            }
            return true;
        }

        return false;
    }
}