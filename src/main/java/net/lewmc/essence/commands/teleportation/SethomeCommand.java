package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SethomeCommand implements CommandExecutor {
    private Essence plugin;
    private LogUtil log;

    /**
     * Constructor for the SethomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public SethomeCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
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
            this.log.noConsole();
            return true;
        }
        MessageUtil message = new MessageUtil(commandSender, this.plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(player, message);

        if (command.getName().equalsIgnoreCase("sethome")) {
            if (permission.has("essence.home.create")) {
                if (args.length == 0) {
                    message.PrivateMessage("Usage: /sethome <name>", true);
                    return true;
                }
                Location loc = player.getLocation();
                DataUtil config = new DataUtil(this.plugin, message);
                config.load(config.playerDataFile(player));

                HomeUtil homeUtil = new HomeUtil();
                String homeName = homeUtil.HomeWrapper(args[0].toLowerCase());

                config.createSection(homeName);

                ConfigurationSection cs = config.getSection(homeName);
                cs.set("X", loc.getX());
                cs.set("Y", loc.getY());
                cs.set("Z", loc.getZ());
                cs.set("world", loc.getWorld().getName());

                // Save the configuration to the file
                config.save();

                message.PrivateMessage("Created home '" + args[0] + "'.", false);
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}