package net.lewmc.essence.commands.teleportation.tp;

import com.tcoded.folialib.FoliaLib;
import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * /tprandom command.
 */
public class TprandomCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;
    private final FoliaLib flib;
    private final TeleportUtil teleUtil;

    /**
     * Constructor for the TprandomCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public TprandomCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
        this.flib = new FoliaLib(this.plugin);
        this.teleUtil = new TeleportUtil(this.plugin);
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

        if (command.getName().equalsIgnoreCase("tprandom")) {
            CommandUtil cmd = new CommandUtil(this.plugin);
            if (cmd.isDisabled("tprandom")) {
                return cmd.disabled(message);
            }

            if (permission.has("essence.teleport.random")) {
                if (!this.teleUtil.cooldownSurpassed(player, "randomtp")) {
                    message.send("teleport", "tryagain", new String[] { String.valueOf(this.teleUtil.cooldownRemaining(player, "randomtp")) });
                    return true;
                }
                message.send("tprandom", "searching");

                WorldBorder wb;
                try {
                    wb = Objects.requireNonNull(Bukkit.getWorld(player.getWorld().getUID())).getWorldBorder();
                } catch (NullPointerException e) {
                    this.log.warn("NullPointerException randomly teleporting: " + e);
                    message.send("generic", "exception");
                    return true;
                }
                LocationUtil loc = new LocationUtil(this.plugin);

                if (this.flib.isFolia()) {
                    this.flib.getImpl().runAsync(wrappedTask -> {
                        java.util.Random rand = new java.util.Random();
                        Location center = wb.getCenter();
                        double maxX = (center.getBlockX() + (wb.getSize()/2));
                        double minX = (center.getBlockX() - (wb.getSize()/2));
                        double maxZ = (center.getBlockZ() + (wb.getSize()/2));
                        double minZ = (center.getBlockZ() - (wb.getSize()/2));
                        int x = (int) (minX + (maxX - minX) * rand.nextDouble());
                        int z = (int) (minZ + (maxZ - minZ) * rand.nextDouble());
                        Location baseLoc = new Location(player.getWorld(), x, 0, z);

                        this.flib.getImpl().runAtLocation(baseLoc, wrappedTask2 -> {
                            int attempt = 1;
                            int y = loc.GetGroundY(player.getWorld(), x, z);
                            while (y == -64 && attempt != 3) {
                                y = loc.GetGroundY(player.getWorld(), x, z);
                                attempt++;
                            }
                            Location teleportLocation = new Location(player.getWorld(), x, y, z);
                            this.checkChunkLoaded(player, teleportLocation);
                        });
                    });
                    return true;
                }

                this.flib.getImpl().runAsync(wrappedTask -> {
                    Location teleportLocation = loc.GetRandomLocation(player, wb);
                    this.checkChunkLoaded(player, teleportLocation);
                });
                return true;
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }

    private void checkChunkLoaded(Player player, Location teleportLocation) {
        MessageUtil message = new MessageUtil(player, plugin);

        if (teleportLocation.getY() != -64) {
            message.send("tprandom", "teleporting");
        } else {
            message.send("tprandom", "nosafe");
            return;
        }

        Chunk chunk = teleportLocation.getChunk();
        if (this.flib.isFolia()) {
            flib.getImpl().runAtLocation(teleportLocation, wrappedTask -> {
                if (!chunk.isLoaded()) {
                    message.send("tprandom", "generating");
                    chunk.load(true);
                }
                doTeleportation(player, teleportLocation);
            });
        } else {
            if (!chunk.isLoaded()) {
                message.send("tprandom", "generating");
                chunk.load(true);
            }
            doTeleportation(player, teleportLocation);
        }
    }

    private void doTeleportation(Player player, Location teleportLocation) {
        this.teleUtil.setCooldown(player, "randomtp");

        LocationUtil locationUtil = new LocationUtil(plugin);
        locationUtil.UpdateLastLocation(player);

        TeleportUtil tp = new TeleportUtil(plugin);
        tp.doTeleport(player, teleportLocation, 0);
    }
}