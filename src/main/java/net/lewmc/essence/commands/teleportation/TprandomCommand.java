package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.utils.*;
import net.lewmc.essence.Essence;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TprandomCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the TprandomCommand class.
     * @param plugin References to the main plugin class.
     */
    public TprandomCommand(Essence plugin) {
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
        if (!(commandSender instanceof Player)) {
            this.log.noConsole();
            return true;
        }
        MessageUtil message = new MessageUtil(commandSender, plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);
        TeleportUtil teleUtil = new TeleportUtil(this.plugin);

        if (command.getName().equalsIgnoreCase("tprandom")) {
            if (permission.has("essence.teleport.random")) {
                if (!teleUtil.cooldownSurpassed(player, "randomtp")) {
                    message.PrivateMessage("teleport", "tryagain", String.valueOf(teleUtil.cooldownRemaining(player, "randomtp")));
                    return true;
                }
                message.PrivateMessage("tprandom", "searching");
                WorldBorder wb;
                try {
                    wb = Objects.requireNonNull(Bukkit.getWorld(player.getWorld().getUID())).getWorldBorder();
                } catch (NullPointerException e) {
                    this.log.warn("NullPointerException randomly teleporting: " + e);
                    message.PrivateMessage("generic", "exception");
                    return true;
                }

                LocationUtil loc = new LocationUtil(this.plugin, message);

                teleUtil.setCooldown(player, "randomtp");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Location teleportLocation = loc.GetRandomLocation(player, wb);
                        if (teleportLocation.getY() != -64) {
                            message.PrivateMessage("tprandom", "teleporting");

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Chunk chunk = teleportLocation.getChunk();
                                    if (!chunk.isLoaded()) {
                                        message.PrivateMessage("tprandom", "generating");
                                        chunk.load(true);
                                    }

                                    LocationUtil locationUtil = new LocationUtil(plugin, message);
                                    locationUtil.UpdateLastLocation(player);
                                    teleportPlayer(player, teleportLocation);
                                }
                            }.runTask(plugin);
                        } else {
                            message.PrivateMessage("tprandom", "nosafe");
                        }
                    }
                }.runTaskAsynchronously(this.plugin);
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }

    public void teleportPlayer(Player player, Location loc) {
        player.teleport(loc);
    }
}