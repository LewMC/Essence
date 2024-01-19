package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HomeCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the HomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public HomeCommand(Essence plugin) {
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
        MessageUtil message = new MessageUtil(commandSender, plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(player, message);

        if (command.getName().equalsIgnoreCase("home")) {
            if (permission.has("essence.home.use")) {
                DataUtil config = new DataUtil(this.plugin, message);
                config.load(config.playerDataFile(player));

                HomeUtil homeUtil = new HomeUtil();

                String homeName;
                String chatHomeName;

                if (args.length == 1) {
                    homeName = homeUtil.HomeWrapper(args[0].toLowerCase());
                    chatHomeName = args[0].toLowerCase();
                    if (config.getSection(homeName) == null) {
                        message.PrivateMessage("Home " + chatHomeName + " does not exist. Use /homes for a list of homes.", true);
                        return true;
                    }
                } else {
                    homeName = homeUtil.HomeWrapper("home");
                    chatHomeName = "home";
                    if (config.getSection(homeName) == null) {
                        message.PrivateMessage("Home does not exist. Use /homes for a list of homes.", true);
                        return true;
                    }
                }

                ConfigurationSection cs = config.getSection(homeName);

                if (cs == null) {
                    message.PrivateMessage("Unable to teleport home due to an unexpected error, please see console for details.", true);
                    this.log.warn("Player " + player + " attempted to teleport home to " + chatHomeName + " but couldn't due to an error.");
                    this.log.warn("Error: Unable to load from configuration file, please check configuration file.");
                    return true;
                }

                if (cs.getString("world") == null) {
                    message.PrivateMessage("Unable to teleport home due to an unexpected error, please see console for details.", true);
                    this.log.warn("Player " + player + " attempted to teleport home to " + chatHomeName + " but couldn't due to an error.");
                    this.log.warn("Error: world is null, please check configuration file.");
                    return true;
                }

                LocationUtil locationUtil = new LocationUtil(this.plugin, message);
                locationUtil.UpdateLastLocation(player);


                Location loc = new Location(
                    Bukkit.getServer().getWorld(cs.getString("world")),
                    cs.getDouble("X"),
                    cs.getDouble("Y"),
                    cs.getDouble("Z"),
                    (float) cs.getDouble("yaw"),
                    (float) cs.getDouble("pitch")
                );

                player.teleport(loc);

                message.PrivateMessage("Teleporting to home '" + chatHomeName + "'...", false);

            } else {
                permission.not();
            }
            return true;
        }
        return false;
    }
}