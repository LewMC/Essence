package net.lewmc.essence.commands.teleportation.home;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
                FileUtil playerData = new FileUtil(this.plugin);
                playerData.load(playerData.playerDataFile(player));

                SecurityUtil securityUtil = new SecurityUtil();
                if (securityUtil.hasSpecialCharacters(name.toLowerCase())) {
                    playerData.close();
                    message.PrivateMessage("home", "specialchars");
                    return true;
                }

                String homeName = "homes." + name.toLowerCase();

                if (playerData.get(homeName) != null) {
                    playerData.close();
                    message.PrivateMessage("home", "alreadyexists");
                    return true;
                }

                playerData.set(homeName + "world", loc.getWorld().getName());
                playerData.set(homeName + "X", loc.getX());
                playerData.set(homeName + "Y", loc.getY());
                playerData.set(homeName + "Z", loc.getZ());
                playerData.set(homeName + "yaw", loc.getYaw());
                playerData.set(homeName + "pitch", loc.getPitch());

                // Save the configuration to the file
                playerData.save();

                message.PrivateMessage("home", "created", name);
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}