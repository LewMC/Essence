package net.lewmc.essence.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Read-only inventory type. Essence cancels all events of players trying to remove items.
 */
public class TypeReadOnlyInventory implements InventoryHolder {
    /**
     * Retrieves the fake inventory.
     * @return Inventory - the inventory.
     */
    @Override
    public Inventory getInventory() {
        return null;
    }
}
