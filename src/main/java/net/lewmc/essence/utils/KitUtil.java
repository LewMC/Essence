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

        Object max = kitData.get("kits."+kit+".maxclaims");

        LogUtil loga = new LogUtil(this.plugin);

        FileUtil playerData = new FileUtil(this.plugin);
        playerData.load(playerData.playerDataFile(this.player));

        if (!playerData.exists("kits." + kit + ".claims")) {
            playerData.set("kits." + kit + ".claims", 0);
        }

        loga.info(max.toString() + " max out of claims: "+playerData.getInt("kits." + kit + ".claims"));

        if (max != null && (int) max != -1) {
            loga.info("1");
            PermissionHandler perm = new PermissionHandler(this.player, this.message);

            if (!perm.has("essence.bypass.maxkitclaims")) {
                loga.info("2");
                if (playerData.getInt("kits." + kit + ".claims") >= (int) max) {
                    loga.info("3");
                    message.send("kit", "max");
                    playerData.save();
                    playerData.close();
                    return 0;
                }
            }
        }

        loga.info("4");
        playerData.set("kits." + kit + ".claims", playerData.getInt("kits." + kit + ".claims") + 1);

        loga.info("5");
        playerData.save();
        playerData.close();

        List<String> items = kitData.getStringList("kits."+kit+".items");

        for (String object : items) {
            this.player.getInventory().addItem(new ItemStack(Objects.requireNonNull(Material.getMaterial(object))));
        }

        kitData.close();

        return 0;
    }
}
