package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * The Essence Kit utility.
 */
public class KitUtil {
    private final Player player;
    private final Essence plugin;
    private final MessageUtil message;

    /**
     * Constructor for the KitUtil class.
     * @param plugin Reference to the main Essence class.
     * @param player The player the class should reference.
     */
    public KitUtil(Essence plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.message = new MessageUtil(player, plugin);
    }

    /**
     * Gives the user referenced in the constructor the kit specified.
     * @param kit String - the kit to give the user.
     * @return int - Result of operation. 0 = Successful, 1 = No Permission, 2 = Kit is missing.
     */
    public int giveKit(String kit) {
        FileUtil kitData = new FileUtil(this.plugin);

        kitData.load("data/kits.yml");

        if (kitData.get("kits."+kit) == null) {
            return 2;
        }

        if (kitData.get("kits."+kit+".permission") != null) {
            PermissionHandler perm = new PermissionHandler(this.player, this.message);
            if (!perm.has(kitData.get("kits."+kit+".permission").toString())) {
                return 1;
            }
        }

        List<String> items = kitData.getStringList("kits."+kit+".items");

        for (String object : items) {
            this.player.getInventory().addItem(new ItemStack(Objects.requireNonNull(Material.getMaterial(object))));
        }

        kitData.close();

        return 0;
    }
}
