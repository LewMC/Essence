package net.lewmc.essence.kit;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.Files;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

/**
 * The Essence Kit utility.
 */
public class UtilKit {
    private final Player player;
    private final Essence plugin;

    /**
     * Constructor for the KitUtil class.
     * @param plugin Reference to the main Essence class.
     * @param player The player the class should reference.
     */
    public UtilKit(Essence plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    /**
     * Gives the user referenced in the constructor the kit specified.
     * @param kit String - the kit to give the user.
     * @return int - Result of operation. 0 = Successful, 1 = No Permission, 2 = Kit is missing, 3 = Claimed too many times.
     */
    public int giveKit(String kit) {
        Files kitData = new Files(this.plugin.foundryConfig, this.plugin);

        kitData.load("data/kits.yml");

        if (kitData.get("kits."+kit) == null) {
            return 2;
        }

        UtilPermission perm = new UtilPermission(this.plugin, this.player);

        if (kitData.get("kits."+kit+".permission") != null && !perm.has(kitData.get("kits."+kit+".permission").toString())) {
            return 1;
        }

        Object max = kitData.get("kits."+kit+".maxclaims");

        Files playerData = new Files(this.plugin.foundryConfig, this.plugin);
        playerData.load(playerData.playerDataFile(this.player));

        Object claims = playerData.get("kits." + kit + ".claims");

        if (claims == null || (int) claims < 0) {
            playerData.set("kits." + kit + ".claims", 0);
        }

        if ((max != null && (int) max != -1) && !perm.has("essence.bypass.maxkitclaims")) {
            if (playerData.getInt("kits." + kit + ".claims") >= (int) max) {
                playerData.save();
                playerData.close();
                return 3;
            }
        }

        playerData.set("kits." + kit + ".claims", playerData.getInt("kits." + kit + ".claims") + 1);

        playerData.save();

        Set<String> items = kitData.getKeys("kits."+kit+".items", false);

        if (items == null) {
            this.plugin.log.warn("Unable to parse items in kit '"+kit+"', kit has no items. Please check the item's spelling and try again, no reload/restart required.");
            return 4;
        }

        for (String object : items) {
            Material item = Material.getMaterial(object);

            if (item == null) {
                this.plugin.log.warn("Unable to parse item '"+object+"' in kit '"+kit+"', Material not found. Please check the item's spelling and try again, no reload/restart required.");
                return 4;
            }

            ItemStack itemStack = new ItemStack(item);
            int amount = kitData.getInt("kits."+kit+".items."+object+".amount");
            if (amount > 0 && amount < 65) {
                itemStack.setAmount(amount);
            } else {
                itemStack.setAmount(1);
                this.plugin.log.warn("Item '"+object+"' in kit '"+kit+"', has an invalid amount '"+amount+"'. Must be between 1 and 64.");
            }

            List<String> enchantments = kitData.getStringList("kits."+kit+".items."+object+".enchantments");
            for (String enchantment : enchantments) {
                String[] e = enchantment.split(":");
                Enchantment parsedEnchantment = Enchantment.getByKey(NamespacedKey.fromString(e[0]));

                if (parsedEnchantment != null) {
                    itemStack.addEnchantment(parsedEnchantment, Integer.parseInt(e[1]));
                } else {
                    this.plugin.log.warn("Unable to parse enchantment '"+enchantment+"' in kit '"+kit+"'");
                }
            }

            this.player.getInventory().addItem(itemStack);
        }

        kitData.close();

        return 0;
    }
}
