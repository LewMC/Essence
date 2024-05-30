package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
                FileUtil playerData = new FileUtil(this.plugin);
                playerData.load(playerData.playerDataFile(player));

                if (playerData.get("last-location") == null) {
                    message.PrivateMessage("back", "cant");
                    return true;
                }

                LocationUtil locationUtil = new LocationUtil(this.plugin, message);
                locationUtil.UpdateLastLocation(player);

                TeleportUtil tp = new TeleportUtil(plugin);
                tp.doTeleport(
                        player,
                        Bukkit.getServer().getWorld(Objects.requireNonNull(playerData.getString("last-location.world"))),
                        playerData.getDouble("last-location.X"),
                        playerData.getDouble("last-location.Y"),
                        playerData.getDouble("last-location.Z"),
                        (float) playerData.getDouble("last-location.yaw"),
                        (float) playerData.getDouble("last-location.pitch")
                );

                playerData.close();

                message.PrivateMessage("back", "going");

            } else {
                permission.not();
            }
            return true;
        }
        return false;
    }
}