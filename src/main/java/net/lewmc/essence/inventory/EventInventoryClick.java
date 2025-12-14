package net.lewmc.essence.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * EventInventoryClick class.
 */
public class EventInventoryClick implements Listener {
    /**
     * Event handler for when a player clicks their inventory.
     * @param event InventoryClickEvent - Server thrown event.
     */
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        // Cancels Essence "Dummy" / read-only inventories being interactable.
        if (event.getInventory().getHolder() instanceof TypeReadOnlyInventory) {
            event.setCancelled(true);
        }
    }
}
