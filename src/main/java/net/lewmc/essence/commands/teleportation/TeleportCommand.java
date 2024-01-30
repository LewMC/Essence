package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.utils.LocationUtil;
import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.PermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeleportCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the TeleportCommand class.
     * @param plugin References to the main plugin class.
     */
    public TeleportCommand(Essence plugin) {
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
    public boolean onCommand(
        @NotNull CommandSender commandSender,
        @NotNull Command command,
        @NotNull String s,
        String[] args
    ) {
        Player player = null;

        if (console(commandSender) && args.length != 4) {
            this.log.noConsole();
            return true;
        }

        if (!console(commandSender)) {
            player = (Player) commandSender;
        }
        MessageUtil message = new MessageUtil(commandSender, plugin);
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("tp")) {
            if (args.length == 3) {
                if (permission.has("essence.teleport.coord")) {
                    LocationUtil locationUtil = new LocationUtil(this.plugin, message);
                    locationUtil.UpdateLastLocation(player);

                    double x = Double.parseDouble(args[0]);
                    double y = Double.parseDouble(args[1]);
                    double z = Double.parseDouble(args[2]);
                    Location location = new Location(player.getWorld(), x, y, z);
                    player.teleport(location);
                } else {
                    permission.not();
                }
                return true;
            } else if (args.length == 4) {
                if (permission.has("essence.teleport.other") && permission.has("essence.teleport.coord")) {
                    String p = args[0];
                    if (p.equalsIgnoreCase("@s")) {
                        LocationUtil locationUtil = new LocationUtil(this.plugin, message);
                        locationUtil.UpdateLastLocation(player);

                        double x = Double.parseDouble(args[1]);
                        double y = Double.parseDouble(args[2]);
                        double z = Double.parseDouble(args[3]);
                        Location location = new Location(player.getWorld(), x, y, z);
                        player.teleport(location);
                    } else {
                        Player playerToTeleport = this.plugin.getServer().getPlayer(args[0]);
                        if (playerToTeleport == null) {
                            message.PrivateMessage("generic", "playernotfound");
                            return true;
                        }

                        LocationUtil locationUtil = new LocationUtil(this.plugin, message);
                        locationUtil.UpdateLastLocation(playerToTeleport);

                        double x = Double.parseDouble(args[1]);
                        double y = Double.parseDouble(args[2]);
                        double z = Double.parseDouble(args[3]);
                        Location location = new Location(playerToTeleport.getWorld(), x, y, z);
                        playerToTeleport.teleport(location);
                    }
                } else {
                    permission.not();
                }
                return true;
            } else if (args.length == 1) {
                if (permission.has("essence.teleport.player")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if ((p.getName().toLowerCase()).equalsIgnoreCase(args[0])) {
                            LocationUtil locationUtil = new LocationUtil(this.plugin, message);
                            locationUtil.UpdateLastLocation(player);

                            message.PrivateMessage("teleport", "to", p.getName());
                            player.teleport(p);
                            return true;
                        }
                    }
                } else {
                    permission.not();
                }
                message.PrivateMessage("generic", "playernotfound");
                return true;
            } else {
                message.PrivateMessage("teleport", "usage");
            }
            return true;
        }

        return false;
    }

    public boolean console(CommandSender commandSender) {
        return !(commandSender instanceof Player);
    }
}