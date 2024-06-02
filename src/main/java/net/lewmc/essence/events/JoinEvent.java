package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

/**
 * JoinEvent class.
 */
public class JoinEvent implements Listener {
    private final Essence plugin;

    /**
     * Constructor for the JoinEvent class.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public JoinEvent(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for when a player joins.
     * @param event PlayerJoinEvent - Server thrown event.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        LogUtil log = new LogUtil(this.plugin);

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(event.getPlayer().getName());
        boolean firstJoin = offlinePlayer.isOnline();

        if (firstJoin) {
            KitUtil kit = new KitUtil(this.plugin, event.getPlayer());
            if (this.plugin.getConfig().get("spawn-kits") != null && !Objects.equals(this.plugin.getConfig().getString("spawn-kits"), "false")) {
                for (Object giveKit : this.plugin.getConfig().getList("spawn-kits")) {
                    log.info("Giving player '"+event.getPlayer().getName()+"' spawn kit '"+giveKit+"'");
                    kit.giveKit(giveKit.toString());
                }
            }
        }

        plugin.reloadConfig();
        if (plugin.getConfig().getBoolean("motd.enabled")) {
            if (plugin.getConfig().getString("motd.message") != null) {
                String message = plugin.getConfig().getString("motd.message");
                if (message != null) {
                    TagUtil tag = new TagUtil(plugin);
                    event.getPlayer().sendMessage(tag.doReplacement(message));
                }
            }
        }

        if (plugin.getConfig().getBoolean("teleportation.spawn.always-spawn") || firstJoin) {
            MessageUtil message = new MessageUtil(event.getPlayer(), this.plugin);

            FileUtil essenceConfiguration = new FileUtil(this.plugin);
            if (!essenceConfiguration.load("config.yml")) {
                log.severe("Unable to load configuration file 'config.yml'. Essence may be unable to set some player data");
                return;
            }

            String spawnName = essenceConfiguration.get("teleportation.spawn.main-spawn-world").toString();
            essenceConfiguration.close();

            FileUtil spawnConfiguration = new FileUtil(this.plugin);
            if (!spawnConfiguration.load("data/spawns.yml")) {
                log.severe("Unable to load configuration file 'data/spawns.yml'. Essence may be unable to teleport players to the correct spawn");
                return;
            }

            if (spawnConfiguration.get("spawn") == null) {
                if (Bukkit.getServer().getWorld(spawnName) != null) {
                    TeleportUtil tp = new TeleportUtil(plugin);
                    tp.doTeleport(
                            event.getPlayer(),
                            Bukkit.getServer().getWorld(spawnName),
                            Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getX(),
                            Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getY(),
                            Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getZ(),
                            Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getYaw(),
                            Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getPitch(),
                            0
                    );
                } else {
                    message.PrivateMessage("spawn", "notexist");
                    log.info("Failed to respawn player - world '"+Bukkit.getServer().getWorld(spawnName)+"' does not exist.");
                }
            } else {
                TeleportUtil tp = new TeleportUtil(plugin);
                tp.doTeleport(
                        event.getPlayer(),
                        Bukkit.getServer().getWorld(spawnName),
                        spawnConfiguration.getDouble("spawn."+spawnName+".X"),
                        spawnConfiguration.getDouble("spawn."+spawnName+".Y"),
                        spawnConfiguration.getDouble("spawn."+spawnName+".Z"),
                        (float) spawnConfiguration.getDouble("spawn."+spawnName+".yaw"),
                        (float) spawnConfiguration.getDouble("spawn."+spawnName+".pitch"),
                        0
                );
            }

            spawnConfiguration.close();
        }

        FileUtil playerFile = new FileUtil(plugin);
        String playerDataFile = playerFile.playerDataFile(event.getPlayer());

        if (!playerFile.exists(playerDataFile)) {
            if (this.plugin.verbose) {
                log.info("Player data does not exist, creating file...");
            }

            if (playerFile.create(playerDataFile)) {
                if (this.plugin.verbose) {
                    log.info("Created player data!");
                }
            } else {
                log.warn("Unable to create player data! This may cause some commands to stop working.");
                return;
            }

            if (!playerFile.load(playerDataFile)) {
                log.severe("Unable to load configuration file '"+playerDataFile+"'. Essence may be unable to teleport players to the correct spawn");
                return;
            }

            playerFile.set("economy.balance", plugin.getConfig().getDouble("economy.start-money"));
            playerFile.set("economy.accepting-payments", true);
            playerFile.set("user.last-known-name", event.getPlayer().getName());
        } else {
            if (this.plugin.verbose) {
                log.info("Player data exists.");
            }
            if (!playerFile.load(playerDataFile)) {
                log.severe("Unable to load configuration file '"+playerDataFile+"'. Essence may be unable to teleport players to the correct spawn");
                return;
            }

            if (playerFile.get("economy.balance") == null) {
                playerFile.set("economy.balance", plugin.getConfig().getDouble("economy.start-money"));
            }

            if (playerFile.get("economy-accepting-payment") == null) {
                playerFile.set("economy.accepting-payments", true);
            }

            playerFile.set("user.last-known-name", event.getPlayer().getName());
        }
        if (playerFile.save()) {
            if (this.plugin.verbose) {
                log.info("Player data saved.");
            }
        } else {
            log.warn("Unable to save player file - this may cause some issues.");
        }
    }
}