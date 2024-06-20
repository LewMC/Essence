package net.lewmc.essence.commands.teleportation.tp;

import net.lewmc.essence.utils.*;
import net.lewmc.essence.Essence;
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

        if (!console(commandSender)) {
            player = (Player) commandSender;
        }

        MessageUtil message = new MessageUtil(commandSender, plugin);
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("tp")) {
            TeleportUtil tu = new TeleportUtil(this.plugin);
            TeleportUtil.Type type = tu.getTeleportType(args);

            if (type == TeleportUtil.Type.TO_COORD) {
                if (console(commandSender)) {
                    this.log.noConsole();
                    return true;
                }

                if (permission.has("essence.teleport.coord")) {
                    LocationUtil locationUtil = new LocationUtil(this.plugin);
                    locationUtil.UpdateLastLocation(player);

                    double x = Double.parseDouble(args[0]);
                    double y = Double.parseDouble(args[1]);
                    double z = Double.parseDouble(args[2]);
                    Location location = new Location(player.getWorld(), x, y, z);
                    TeleportUtil tp = new TeleportUtil(this.plugin);
                    tp.doTeleport(player, location, 0);

                    message.PrivateMessage("teleport", "tocoord", x+", "+y+", "+z);
                } else {
                    return permission.not();
                }
                return true;
            } else if (type == TeleportUtil.Type.PLAYER_TO_COORD) {
                if (permission.has("essence.teleport.other") && permission.has("essence.teleport.coord")) {
                    String p = args[0];
                    if (p.equalsIgnoreCase("@s")) {
                        LocationUtil locationUtil = new LocationUtil(this.plugin);
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

                        LocationUtil locationUtil = new LocationUtil(this.plugin);
                        locationUtil.UpdateLastLocation(playerToTeleport);

                        double x = Double.parseDouble(args[1]);
                        double y = Double.parseDouble(args[2]);
                        double z = Double.parseDouble(args[3]);
                        Location location = new Location(playerToTeleport.getWorld(), x, y, z);
                        TeleportUtil tp = new TeleportUtil(this.plugin);
                        tp.doTeleport(playerToTeleport, location, 0);

                        message.PrivateMessage("teleport", "playertocoord", playerToTeleport.getName(), x+", "+y+", "+z);
                    }
                } else {
                    return permission.not();
                }
                return true;
            } else if (type == TeleportUtil.Type.TO_PLAYER) {
                if (console(commandSender)) {
                    this.log.noConsole();
                    return true;
                }
                if (permission.has("essence.teleport.player")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if ((p.getName().toLowerCase()).equalsIgnoreCase(args[0])) {
                            LocationUtil locationUtil = new LocationUtil(this.plugin);
                            locationUtil.UpdateLastLocation(player);

                            message.PrivateMessage("teleport", "to", p.getName());

                            TeleportUtil tp = new TeleportUtil(this.plugin);
                            tp.doTeleport(player, p.getLocation(), 0);
                            return true;
                        }
                    }
                } else {
                    return permission.not();
                }
                message.PrivateMessage("generic", "playernotfound");
                return true;
            } else if (type == TeleportUtil.Type.PLAYER_TO_PLAYER) {
                if (permission.has("essence.teleport.other") && permission.has("essence.teleport.player")) {
                    Player player1 = null;
                    Player player2 = null;

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if ((p.getName().toLowerCase()).equalsIgnoreCase(args[0])) {
                            player1 = p;
                        }
                        if ((p.getName().toLowerCase()).equalsIgnoreCase(args[1])) {
                            player2 = p;
                        }
                    }

                    if (player1 == null || player2 == null) {
                        message.PrivateMessage("generic", "playernotfound");
                        return true;
                    } else {
                        LocationUtil locationUtil = new LocationUtil(this.plugin);
                        locationUtil.UpdateLastLocation(player);

                        message.PrivateMessage("teleport", "toplayer", player1.getName(), player2.getName());

                        TeleportUtil tp = new TeleportUtil(this.plugin);
                        tp.doTeleport(player1, player2.getLocation(), 0);
                        return true;
                    }
                } else {
                    return permission.not();
                }
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