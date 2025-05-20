package net.lewmc.essence.teleportation.tp;

import com.tcoded.folialib.FoliaLib;
import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilLocation;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.Logger;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * /tprandom command.
 */
public class CommandTprandom extends FoundryPlayerCommand {
    private final Essence plugin;
    private final FoliaLib flib;
    private final UtilTeleport teleUtil;

    /**
     * Constructor for the TprandomCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandTprandom(Essence plugin) {
        this.plugin = plugin;
        this.flib = new FoliaLib(this.plugin);
        this.teleUtil = new UtilTeleport(this.plugin);
    }

    /**
     * The required permission.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.teleport.random";
    }

    /**
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean  true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("tprandom")) {
            return cmd.disabled();
        }

        Player p = (Player) cs;

        UtilMessage message = new UtilMessage(this.plugin, cs);
        if (!this.teleUtil.cooldownSurpassed(p, "randomtp")) {
            message.send("teleport", "tryagain", new String[]{String.valueOf(this.teleUtil.cooldownRemaining(p, "randomtp"))});
            return true;
        }
        message.send("tprandom", "searching");

        WorldBorder wb;
        try {
            wb = Objects.requireNonNull(Bukkit.getWorld(p.getWorld().getUID())).getWorldBorder();
        } catch (NullPointerException e) {
            new Logger(this.plugin.config).warn("NullPointerException randomly teleporting: " + e);
            message.send("generic", "exception");
            return true;
        }
        UtilLocation loc = new UtilLocation(this.plugin);

        if (this.flib.isFolia()) {
            this.flib.getImpl().runAsync(wrappedTask -> {
                java.util.Random rand = new java.util.Random();
                Location center = wb.getCenter();
                double maxX = (center.getBlockX() + (wb.getSize() / 2));
                double minX = (center.getBlockX() - (wb.getSize() / 2));
                double maxZ = (center.getBlockZ() + (wb.getSize() / 2));
                double minZ = (center.getBlockZ() - (wb.getSize() / 2));
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
    }

    /**
     * Checks if the requested chunk is loaded.
     * @param player            Player - the player
     * @param teleportLocation  Location - The location
     */
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