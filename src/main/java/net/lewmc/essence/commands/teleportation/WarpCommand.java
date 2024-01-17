package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.PermissionHandler;
import net.lewmc.essence.utils.DataUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Set;

public class WarpCommand implements CommandExecutor {
    private Essence plugin;
    private LogUtil log;

    /**
     * Constructor for the WarpCommand class.
     * @param plugin References to the main plugin class.
     */
    public WarpCommand(Essence plugin) {
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
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            this.log.noConsole();
            return true;
        }
        MessageUtil message = new MessageUtil(commandSender, plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(player, message);

        if (command.getName().equalsIgnoreCase("warp")) {
            if (args.length > 0) {
                if (permission.has("essence.warp.use")) {
                    DataUtil config = new DataUtil(this.plugin, message);
                    config.load("data/warps.yml");

                    if (config.getSection("warps." + args[0].toLowerCase()) == null) {
                        message.PrivateMessage("Warp " + args[0].toLowerCase() + " does not exist. Use /warp for a list of warps.", true);
                        return true;
                    }

                    ConfigurationSection cs = config.getSection("warps." + args[0].toLowerCase());

                    if (cs.getString("world") == null) {
                        message.PrivateMessage("Unable to warp due to an unexpected error, please see console for details.", true);
                        this.log.warn("Player "+player+" attempted to warp to "+args[0].toLowerCase()+" but couldn't due to an error.");
                        this.log.warn("Error: world is null, please check configuration file.");
                        return true;
                    }

                    Location loc = new Location(
                        Bukkit.getServer().getWorld(cs.getString("world")),
                        cs.getDouble("X"),
                        cs.getDouble("Y"),
                        cs.getDouble("Z")
                    );

                    player.teleport(loc);

                    message.PrivateMessage("Warping to '"+args[0].toLowerCase()+"'...", false);

                    return true;
                } else {
                    permission.not();
                }
            } else {
                if (permission.has("essence.warp.list")) {
                    DataUtil dataUtil = new DataUtil(this.plugin, message);
                    dataUtil.load("data/warps.yml");

                    Set<String> keys = dataUtil.getKeys("warps");

                    if (keys == null) {
                        message.PrivateMessage("There are no warps set.", false);
                        return true;
                    }

                    String setWarps = "Warps: ";
                    int i = 0;

                    for (String key : keys) {
                        if (i == 0) {
                            setWarps = setWarps + key;
                        } else {
                            setWarps = setWarps + ", "+key;
                        }
                        i++;
                    }
                    message.PrivateMessage(setWarps, false);
                } else {
                    permission.not();
                }
            }
            return true;
        }

        return false;
    }
}