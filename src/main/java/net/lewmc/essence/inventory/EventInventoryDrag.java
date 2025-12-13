package net.lewmc.essence.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * EventInventoryDrag class.
 */
public class EventInventoryDrag implements Listener {
    /**
     * Event handler for when a player drags their inventory.
     * @param event InventoryDragEvent - Server thrown event.
     */
    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent event) {
        // Cancels Essence "Dummy" / read-only inventories being interactable.
        if (event.getInventory().getHolder() instanceof TypeReadOnlyInventory) {
            event.setCancelled(true);
        }
    }
}
