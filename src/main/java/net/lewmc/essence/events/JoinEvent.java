package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.WorldCreator;
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
        this.playerJoinMessage(event);

        LogUtil log = new LogUtil(this.plugin);

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(event.getPlayer().getName());
        boolean firstJoin = !offlinePlayer.hasPlayedBefore();

        if (firstJoin) { this.firstJoin(event, log); }

        plugin.reloadConfig();

        if (plugin.getConfig().getBoolean("motd.enabled")) { this.motd(event); }

        if (plugin.getConfig().getBoolean("teleportation.spawn.always-spawn") || firstJoin) {
            this.spawn(event, log);
        }

        FileUtil playerFile = new FileUtil(plugin);
        String playerDataFile = playerFile.playerDataFile(event.getPlayer());

        PlayerUtil pu = new PlayerUtil(this.plugin, event.getPlayer());

        if (!playerFile.exists(playerDataFile)) {
            if (pu.createPlayerData()) {
                if (plugin.verbose) {
                    log.info("Player data saved.");
                }
            } else {
                log.severe("Unable to create player data.");
            }
        } else {
            if (pu.updatePlayerData()) {
                if (plugin.verbose) {
                    log.info("Player data saved.");
                }
            } else {
                log.severe("Unable to create player data.");
            }
        }
    }

    /**
     * Spawns the player.
     * @param event PlayerJoinEvent - The event
     * @param log LogUtil - Logging system.
     */
    private void spawn(PlayerJoinEvent event, LogUtil log) {
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
            if (Bukkit.getServer().getWorld(spawnName) == null) {
                WorldCreator creator = new WorldCreator(spawnName);
                creator.createWorld();
            }
            if (Bukkit.getServer().getWorld(spawnName) != null && Bukkit.getServer().getWorld(spawnName).getSpawnLocation() != null) {
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
                message.send("spawn", "notexist");
                log.info("Failed to respawn player - world '"+Bukkit.getServer().getWorld(spawnName)+"' does not exist.");
            }
        } else {
            TeleportUtil tp = new TeleportUtil(plugin);
            if (Bukkit.getServer().getWorld(spawnName) == null) {
                WorldCreator creator = new WorldCreator(spawnName);
                creator.createWorld();
            }
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

    /**
     * Managers a player's first join.
     * @param event PlayerJoinEvent - The event
     * @param log LogUtil - Logging system.
     */
    private void firstJoin(PlayerJoinEvent event, LogUtil log) {
        KitUtil kit = new KitUtil(this.plugin, event.getPlayer());
        if (this.plugin.getConfig().getList("spawn-kits") != null && !Objects.equals(this.plugin.getConfig().getString("spawn-kits"), "false")) {
            for (Object giveKit : this.plugin.getConfig().getList("spawn-kits")) {
                log.info("Giving player '"+event.getPlayer().getName()+"' spawn kit '"+giveKit+"'");
                kit.giveKit(giveKit.toString());
            }
        }
    }

    /**
     * Displays the MOTD
     * @param event PlayerJoinEvent - The event
     */
    private void motd(PlayerJoinEvent event) {
        if (plugin.getConfig().getString("motd.message") != null) {
            String message = plugin.getConfig().getString("motd.message");
            if (message != null) {
                TagUtil tag = new TagUtil(plugin, event.getPlayer());
                event.getPlayer().sendMessage(tag.doReplacement(message));
            }
        }
    }

    /**
     * Displays the player join message.
     * @param event PlayerJoinEvent - The event
     */
    private void playerJoinMessage(PlayerJoinEvent event) {
        TagUtil tag = new TagUtil(this.plugin, event.getPlayer());
        if (event.getPlayer().hasPlayedBefore()) {
            event.setJoinMessage(tag.doReplacement(this.plugin.getConfig().getString("broadcasts.join")));
        } else {
            event.setJoinMessage(tag.doReplacement(this.plugin.getConfig().getString("broadcasts.first-join")));
        }
    }
}