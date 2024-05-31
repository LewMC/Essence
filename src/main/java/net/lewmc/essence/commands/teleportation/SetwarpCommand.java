package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.utils.*;
import net.lewmc.essence.Essence;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetwarpCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the SetwarpCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public SetwarpCommand(Essence plugin) {
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

        if (command.getName().equalsIgnoreCase("setwarp")) {
            if (permission.has("essence.warp.create")) {
                if (args.length == 0) {
                    message.PrivateMessage("warp", "setusage");
                    return true;
                }
                Location loc = player.getLocation();
                FileUtil warpsData = new FileUtil(this.plugin);
                warpsData.load("data/warps.yml");

                String warpName = args[0].toLowerCase();

                SecurityUtil securityUtil = new SecurityUtil();
                if (securityUtil.hasSpecialCharacters(warpName)) {
                    warpsData.close();
                    message.PrivateMessage("warp", "specialchars");
                    return true;
                }

                if (warpsData.get("warps." + warpName) != null) {
                    warpsData.close();
                    message.PrivateMessage("warp", "alreadyexists");
                    return true;
                }

                warpsData.set("warps."+warpName+".world", loc.getWorld().getName());
                warpsData.set("warps."+warpName+".X", loc.getX());
                warpsData.set("warps."+warpName+".Y", loc.getY());
                warpsData.set("warps."+warpName+".Z", loc.getZ());
                warpsData.set("warps."+warpName+".yaw", loc.getYaw());
                warpsData.set("warps."+warpName+".pitch", loc.getPitch());

                // Save the configuration to the file
                warpsData.save();

                message.PrivateMessage("warp", "created", args[0]);
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}