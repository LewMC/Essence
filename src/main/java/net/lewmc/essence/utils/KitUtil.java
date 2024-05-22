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
        DataUtil data = new DataUtil(this.plugin, this.message);

        data.load("data/kits.yml");
        ConfigurationSection cs = data.getSection("kits."+kit);

        if (cs == null) {
            return 2;
        }

        if (cs.get("permission") != null) {
            PermissionHandler perm = new PermissionHandler(this.player, this.message);
            if (!perm.has(cs.get("permission").toString())) {
                return 1;
            }
        }

        List<?> items = cs.getList("items");

        for (Object object : items) {
            this.player.getInventory().addItem(new ItemStack(Material.getMaterial((String) object)));
        }

        data.close();

        return 0;
    }
}
