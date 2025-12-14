package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import net.lewmc.essence.world.UtilWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

/**
 * EventWorldLoad class.
 */
public class EventWorldLoad implements Listener {
    private final Essence plugin;

    /**
     * Constructor for the EventWorldLoad class.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public EventWorldLoad(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for when a world loads.
     * @param event WorldLoadEvent - Server thrown event.
     */
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        // Deferred tasks.
        if (!this.plugin.deferredTasksRun) {
            this.plugin.log.info("Running deferred tasks...");
            new UtilUpdate(this.plugin).migrateSpawns();
            this.plugin.log.info("Loading worlds...");
            new UtilWorld(this.plugin).autoloadWorlds();
            this.plugin.log.info("Done.");
            this.plugin.deferredTasksRun = true;
        }
    }
}