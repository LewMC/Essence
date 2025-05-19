package net.lewmc.essence.global;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Set;

/**
 * Essence's Permission Handler.
 */
public class UtilPermission {

    private final CommandSender cs;
    private final Essence plugin;

    /**
     * Constructor.
     * @param plugin Reference to the main Essence class.
     * @param cs CommandSender - The user who executed the command.
     */
    public UtilPermission(Essence plugin, CommandSender cs) {
        this.plugin = plugin;
        this.cs = cs;
    }

    /**
     * Checks if the user has a specific permission.
     * @param node String - the permission node to check.
     * @return boolean - If the user has a permission (true/false)
     */
    public boolean has(String node) {
        return new Permissions(this.cs).has(node);
    }

    /**
     * Informs the user that they do not have a permission.
     */
    public boolean not() {
        new UtilMessage(this.plugin, this.cs).send("generic", "missingpermission");
        return true;
    }

    /**
     * Gets the maximum number of homes a player can set from the permission system.
     * @param player Player - The player to check.
     * @return int - The number of homes (-1 is unlimited)
     */
    public int getHomesLimit(Player player) {
        return this.getLimit(player, "home");
    }

    /**
     * Gets the maximum number of team homes a player can set from the permission system.
     * @param player Player - The player to check.
     * @return int - The number of team homes (-1 is unlimited)
     */
    public int getTeamHomesLimit(Player player) {
        return this.getLimit(player, "home.team");
    }

    /**
     * Gets the maximum number of warps a player can set from the permission system.
     * @param player Player - The player to check.
     * @return int - The number of warps (-1 is unlimited)
     */
    public int getWarpsLimit(Player player) {
        return this.getLimit(player, "warp");
    }

    /**
     * Collects the limits for a defined type.
     * @param player Player - The player to check.
     * @param type String - The type to check.
     * @return int - The number allowed (-1 is unlimited)
     */
    private int getLimit(Player player, String type) {
        Set<PermissionAttachmentInfo> perms = player.getEffectivePermissions();
        String[] vars;

        for (PermissionAttachmentInfo i : perms) {
            if (i.getPermission().contains("essence."+type+".limit")) {
                vars = i.getPermission().split("\\.");
                return Integer.parseInt(vars[vars.length - 1]);
            }
        }

        return -1;
    }
}