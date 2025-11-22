package net.lewmc.essence.world;

import net.lewmc.essence.Essence;
import org.bukkit.WorldCreator;

/**
 * The UtilWorld class, used for world management.
 */
public class UtilWorld {
    private final Essence plugin;

    /**
     * Constructor for the class.
     * @param plugin Essence - Reference to the main plugin class.
     */
    public UtilWorld(Essence plugin) {
        this.plugin = plugin;
    }

    public boolean createWorld(String name) {
        if (name == null) {
            return false;
        } else {
            WorldCreator wc = new WorldCreator(name);
            wc.createWorld();
            return true;
        }
    }
}
