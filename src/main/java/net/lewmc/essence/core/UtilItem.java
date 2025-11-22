package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import org.bukkit.Material;

import java.util.List;

/**
 * The item utility
 */
public class UtilItem {
    private final Essence plugin;

    /**
     * Constructor for the ItemUtil class.
     * @param plugin Essence - reference to the main class.
     */
    public UtilItem(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Retrieves the item as a Material.
     * @param name String - The item's name.
     * @return Material - The item as a material.
     */
    public Material getMaterial(String name) {
        if (!this.itemIsBlacklisted(name)) {
            return Material.matchMaterial(name);
        }
        return null;
    }

    /**
     * Checks if an item is blacklisted.
     * @param item String - The item name.
     * @return boolean - true/false is blacklisted?
     */
    public boolean itemIsBlacklisted(String item) {
        if (this.plugin.config.get("item-blacklist") != null) {
            List<String> blacklist = (List<String>) this.plugin.config.get("item-blacklist");

            for (String s : blacklist) {
                if (s.equalsIgnoreCase(item)) {
                    return true;
                }
            }
        }
        return false;
    }
}
