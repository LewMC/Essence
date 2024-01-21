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

public class BackCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the BackCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public BackCommand(Essence plugin) {
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
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("back")) {
            if (permission.has("essence.teleport.back")) {
                DataUtil config = new DataUtil(this.plugin, message);
                config.load(config.playerDataFile(player));

                ConfigurationSection cs = config.getSection("last-location");

                if (cs == null) {
                    message.PrivateMessage("You've not teleported anywhere before, so there's nowhere to go back to!", true);
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
                config.close();

                message.PrivateMessage("Going back...", false);

            } else {
                permission.not();
            }
            return true;
        }
        return false;
    }
}