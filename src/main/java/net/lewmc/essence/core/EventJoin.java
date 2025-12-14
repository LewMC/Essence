package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import net.lewmc.essence.kit.UtilKit;
import net.lewmc.essence.teleportation.tp.UtilTeleport;
import net.lewmc.foundry.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.Objects;

/**
 * JoinEvent class.
 */
public class EventJoin implements Listener {
    private final Essence plugin;

    /**
     * Constructor for the JoinEvent class.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public EventJoin(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for when a player joins.
     * @param event PlayerJoinEvent - Server thrown event.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Logger log = new Logger(this.plugin.foundryConfig);

        UtilPlayer up = new UtilPlayer(this.plugin);

        boolean firstJoin = false;
        if (up.createPlayer(event.getPlayer().getUniqueId())) {
            log.info("Player data file created.");
            firstJoin = true;
        } else {
            if (plugin.verbose) {
                log.info("Player data file already exists.");
            }
        }

        if (firstJoin) { this.firstJoin(event, log); }

        if(!up.loadPlayer(event.getPlayer().getUniqueId())) {
            this.plugin.log.severe("Unable to load player data.");
            this.plugin.log.warn("It wasn't possible to load "+event.getPlayer().getName()+"'s player data.");
            this.plugin.log.warn("The player data may be stale/outdated, Essence may have issues.");
        }

        this.playerJoinMessage(event);

        if (!Objects.equals(plugin.config.get("motd"), "false") && !Objects.equals(plugin.config.get("motd"), false)) { this.motd(event); }

        if ((boolean) plugin.config.get("teleportation.spawn.always-spawn") || firstJoin) {
            try {
                this.spawn(event, log);
            } catch (Exception e) {
                log.severe("Unknown exception: " + e.getMessage());
            }
        }

        if (this.plugin.hasPendingUpdate) {
            this.showUpdateAlert(event);
        }
    }

    /**
     * Spawns the player.
     * @param event PlayerJoinEvent - The event
     * @param log Logger - Logging system.
     */
    private void spawn(PlayerJoinEvent event, Logger log) {
        Object spawnConfig = this.plugin.config.get("teleportation.spawn.main-spawn-world");
        if (spawnConfig == null) {
            if (plugin.verbose) {
                log.severe("Config key 'teleportation.spawn.main-spawn-world' is not set; skipping join spawn teleport.");
            }
            return;
        }

        new UtilTeleport(this.plugin).sendToSpawn(event.getPlayer(), spawnConfig.toString());
    }

    /**
     * Managers a player's first join.
     * @param event PlayerJoinEvent - The event
     * @param log Logger - Logging system.
     */
    private void firstJoin(PlayerJoinEvent event, Logger log) {
        UtilKit kit = new UtilKit(this.plugin, event.getPlayer());
        if (this.plugin.config.get("kit.spawn-kits") != null && !Objects.equals(this.plugin.config.get("kit.spawn-kits"), "false")) {
            for (String giveKit : (List<String>) this.plugin.config.get("kit.spawn-kits")) {
                log.info("Giving player '"+event.getPlayer().getName()+"' spawn kit '"+giveKit+"'");
                kit.giveKit(giveKit);
            }
        }
    }

    /**
     * Displays the MOTD
     * @param event PlayerJoinEvent - The event
     */
    private void motd(PlayerJoinEvent event) {
        if (plugin.config.get("chat.motd") instanceof String) {
            String message = plugin.config.get("chat.motd").toString();
            if (message != null) {
                UtilPlaceholder tag = new UtilPlaceholder(plugin, event.getPlayer());
                event.getPlayer().sendMessage(tag.replaceAll(message));
            }
        }
    }

    /**
     * Displays the player join message.
     * @param event PlayerJoinEvent - The event
     */
    private void playerJoinMessage(PlayerJoinEvent event) {
        UtilPlaceholder tag = new UtilPlaceholder(this.plugin, event.getPlayer());
        if (event.getPlayer().hasPlayedBefore()) {
            if (this.plugin.config.get("chat.broadcasts.join") instanceof String) {
                event.setJoinMessage(tag.replaceAll((String) this.plugin.config.get("chat.broadcasts.join")));
            }
        } else {
            if (this.plugin.config.get("chat.broadcasts.first-join") instanceof String) {
                event.setJoinMessage(tag.replaceAll((String) this.plugin.config.get("chat.broadcasts.first-join")));
            }
        }
    }

    /**
     * Displays the update alert.
     * @param event PlayerJoinEvent - The event
     */
    private void showUpdateAlert(PlayerJoinEvent event) {
        UtilMessage msg = new UtilMessage(this.plugin, event.getPlayer());
        UtilPermission perms = new UtilPermission(this.plugin, event.getPlayer());
        if (perms.has("essence.admin.updates") && this.plugin.hasPendingUpdate) {
            msg.send("other", "updatemsg");
            msg.send("other", "updatemore");
        }
    }
}