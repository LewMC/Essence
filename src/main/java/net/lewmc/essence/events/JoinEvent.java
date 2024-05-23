package net.lewmc.essence.events;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class JoinEvent implements Listener {
    private final Essence plugin;

    public JoinEvent(Essence plugin) {
        this.plugin = plugin;
    }

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
            DataUtil config = new DataUtil(this.plugin, message);
            config.load("config.yml");
            ConfigurationSection configCS = config.getSection("teleportation");
            String spawnName = configCS.get("spawn.main-spawn-world").toString();
            config.close();

            config.load("data/spawns.yml");

            ConfigurationSection cs = config.getSection("spawn." + spawnName);

            if (cs == null) {
                if (Bukkit.getServer().getWorld(spawnName) != null) {
                    event.getPlayer().teleport(new Location(
                            Bukkit.getServer().getWorld(spawnName),
                            Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getX(),
                            Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getY(),
                            Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getZ(),
                            Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getYaw(),
                            Bukkit.getServer().getWorld(spawnName).getSpawnLocation().getPitch()
                    ));
                } else {
                    message.PrivateMessage("spawn", "notexist");
                    log.info("Failed to respawn player - world '"+Bukkit.getServer().getWorld(spawnName)+"' does not exist.");
                }
            } else {
                event.getPlayer().teleport(new Location(
                        Bukkit.getServer().getWorld(spawnName),
                        cs.getDouble("X"),
                        cs.getDouble("Y"),
                        cs.getDouble("Z"),
                        (float) cs.getDouble("yaw"),
                        (float) cs.getDouble("pitch")
                ));
            }

            config.close();
        }

        DataUtil data = new DataUtil(plugin, new MessageUtil(event.getPlayer(), plugin));
        String playerDataFile = data.playerDataFile(event.getPlayer());

        if (!data.fileExists(playerDataFile)) {
            log.info("Player data does not exist, creating file...");
            if (data.createFile(playerDataFile)) {
                log.info("Created player data!");
            } else {
                log.warn("Unable to create player data! This may cause some commands to stop working.");
                return;
            }

            data.load(playerDataFile);
            data.createSection("economy");
            ConfigurationSection economy = data.getSection("economy");
            economy.set("balance", plugin.getConfig().getDouble("economy.start-money"));
            economy.set("accepting-payments", true);
            data.createSection("user");
            ConfigurationSection user = data.getSection("user");
            user.set("last-known-name", event.getPlayer().getName());
        } else {
            data.load(playerDataFile);
            ConfigurationSection cs = data.getSection("user");
            if (cs == null) {
                data.createSection("user");
                cs = data.getSection("user");
            }
            cs.set("last-known-name", event.getPlayer().getName());
        }
        data.save();
    }
}