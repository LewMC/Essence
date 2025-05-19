package net.lewmc.essence.module.teleportation.tp;

import com.tcoded.folialib.FoliaLib;
import net.lewmc.essence.Essence;
import net.lewmc.essence.global.UtilCommand;
import net.lewmc.essence.global.UtilLocation;
import net.lewmc.essence.global.UtilMessage;
import net.lewmc.essence.global.UtilPermission;
import net.lewmc.foundry.Logger;
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
public class CommandTprandom implements CommandExecutor {
    private final Essence plugin;
    private final Logger log;
    private final FoliaLib flib;
    private final UtilTeleport teleUtil;

    /**
     * Constructor for the TprandomCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandTprandom(Essence plugin) {
        this.plugin = plugin;
        this.log = new Logger(plugin.config);
        this.flib = new FoliaLib(this.plugin);
        this.teleUtil = new UtilTeleport(this.plugin);
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
        if (command.getName().equalsIgnoreCase("tprandom")) {
            UtilCommand cmd = new UtilCommand(this.plugin, cs);
            if (cmd.isDisabled("tprandom")) { return cmd.disabled(); }
            if (!(cs instanceof Player p)) { return this.log.noConsole(); }

            UtilMessage message = new UtilMessage(this.plugin, cs);
            UtilPermission perms = new UtilPermission(this.plugin, cs);

            if (perms.has("essence.teleport.random")) {
                if (!this.teleUtil.cooldownSurpassed(p, "randomtp")) {
                    message.send("teleport", "tryagain", new String[] { String.valueOf(this.teleUtil.cooldownRemaining(p, "randomtp")) });
                    return true;
                }
                message.send("tprandom", "searching");

                WorldBorder wb;
                try {
                    wb = Objects.requireNonNull(Bukkit.getWorld(p.getWorld().getUID())).getWorldBorder();
                } catch (NullPointerException e) {
                    this.log.warn("NullPointerException randomly teleporting: " + e);
                    message.send("generic", "exception");
                    return true;
                }
                UtilLocation loc = new UtilLocation(this.plugin);

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
                        Location baseLoc = new Location(p.getWorld(), x, 0, z);

                        this.flib.getImpl().runAtLocation(baseLoc, wrappedTask2 -> {
                            int attempt = 1;
                            int y = loc.GetGroundY(p.getWorld(), x, z);
                            while (y == -64 && attempt != 3) {
                                y = loc.GetGroundY(p.getWorld(), x, z);
                                attempt++;
                            }
                            Location teleportLocation = new Location(p.getWorld(), x, y, z);
                            this.checkChunkLoaded(p, teleportLocation);
                        });
                    });
                    return true;
                }

                this.flib.getImpl().runAsync(wrappedTask -> {
                    Location teleportLocation = loc.GetRandomLocation(p, wb);
                    this.checkChunkLoaded(p, teleportLocation);
                });
                return true;
            } else {
                return perms.not();
            }
        }

        return false;
    }

    private void checkChunkLoaded(Player player, Location teleportLocation) {
        UtilMessage message = new UtilMessage(this.plugin, player);

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

        UtilLocation locationUtil = new UtilLocation(plugin);
        locationUtil.UpdateLastLocation(player);

        UtilTeleport tp = new UtilTeleport(plugin);
        tp.doTeleport(player, teleportLocation, 0);
    }
}