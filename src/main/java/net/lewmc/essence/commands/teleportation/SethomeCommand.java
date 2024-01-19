package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SethomeCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

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
    public boolean onCommand(
        @NotNull CommandSender commandSender,
        @NotNull Command command,
        @NotNull String s,
        String[] args
    ) {
        if (!(commandSender instanceof Player)) {
            this.log.noConsole();
            return true;
        }
        MessageUtil message = new MessageUtil(commandSender, this.plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("sethome")) {
            if (permission.has("essence.home.create")) {

                String name;
                if (args.length == 0) {
                    name = "home";
                } else {
                    name = args[0];
                }

                Location loc = player.getLocation();
                DataUtil config = new DataUtil(this.plugin, message);
                config.load(config.playerDataFile(player));

                SecurityUtil securityUtil = new SecurityUtil();
                if (securityUtil.hasSpecialCharacters(name.toLowerCase())) {
                    message.PrivateMessage("Homes cannot contain special characters!", true);
                    return true;
                }

                HomeUtil homeUtil = new HomeUtil();
                String homeName = homeUtil.HomeWrapper(name.toLowerCase());

                if (config.sectionExists(homeName)) {
                    message.PrivateMessage("A home with this name already exists.", true);
                    return true;
                }

                config.createSection(homeName);

                ConfigurationSection cs = config.getSection(homeName);
                cs.set("world", loc.getWorld().getName());
                cs.set("X", loc.getX());
                cs.set("Y", loc.getY());
                cs.set("Z", loc.getZ());
                cs.set("yaw", loc.getYaw());
                cs.set("pitch", loc.getPitch());

                // Save the configuration to the file
                config.save();

                message.PrivateMessage("Created home '" + name + "'.", false);
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}