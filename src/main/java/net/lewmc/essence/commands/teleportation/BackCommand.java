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

    /**
     * Constructor for the BackCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public BackCommand(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * @param cs            Information about who sent the command - player or console.
     * @param command       Information about what command was sent.
     * @param s             Command label - not used here.
     * @param args          The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
            @NotNull CommandSender cs,
            @NotNull Command command,
            @NotNull String s,
            String[] args
    ) {
        if (command.getName().equalsIgnoreCase("back")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("back")) { return cmd.disabled(); }

            if (!(cs instanceof Player p)) { return new LogUtil(this.plugin).noConsole(); }

            PermissionHandler perms = new PermissionHandler(this.plugin, cs);

            if (perms.has("essence.teleport.back")) {
                MessageUtil msg = new MessageUtil(this.plugin, cs);
                FileUtil playerData = new FileUtil(this.plugin);

                playerData.load(playerData.playerDataFile(p));

                if (playerData.get("last-location") == null) {
                    msg.send("back", "cant");
                    return true;
                }

                LocationUtil locationUtil = new LocationUtil(this.plugin);
                locationUtil.UpdateLastLocation(p);

                TeleportUtil tp = new TeleportUtil(plugin);
                tp.doTeleport(
                        p,
                        Bukkit.getServer().getWorld(Objects.requireNonNull(playerData.getString("last-location.world"))),
                        playerData.getDouble("last-location.X"),
                        playerData.getDouble("last-location.Y"),
                        playerData.getDouble("last-location.Z"),
                        (float) playerData.getDouble("last-location.yaw"),
                        (float) playerData.getDouble("last-location.pitch"),
                        0
                );

                playerData.close();

                msg.send("back", "going");

            } else {
                return perms.not();
            }
            return true;
        }
        return false;
    }
}