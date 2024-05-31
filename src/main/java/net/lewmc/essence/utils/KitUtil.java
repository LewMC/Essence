package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KitUtil {
    private final Player player;
    private final Essence plugin;
    private final MessageUtil message;

    public KitUtil(Essence plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.message = new MessageUtil(player, plugin);
    }

    public int giveKit(String kit) {
        FileUtil kitData = new FileUtil(this.plugin);

        kitData.load("data/kits.yml");

        if (kitData.get("kits."+kit) == null) {
            return 2;
        }

        if (kitData.get("kits."+kit+"permission") != null) {
            PermissionHandler perm = new PermissionHandler(this.player, this.message);
            if (!perm.has(kitData.get("kits."+kit+"permission").toString())) {
                return 1;
            }
        }

        List<String> items = kitData.getStringList("kits."+kit+"items");

        for (Object object : items) {
            this.player.getInventory().addItem(new ItemStack(Material.getMaterial((String) object)));
        }

        kitData.close();

        return 0;
    }
}
