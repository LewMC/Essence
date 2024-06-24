package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Set;

/**
 * Essence's Permission Handler.
 */
public class PermissionHandler {

    private final CommandSender commandSender;
    private final MessageUtil message;

    /**
     * Constructor.
     * @param commandSender CommandSender - The user who executed the command.
     * @param message MessageUtil - A reference to the MessageUtil class.
     */
    public PermissionHandler(CommandSender commandSender, MessageUtil message) {
        this.commandSender = commandSender;
        this.message = message;
    }

    /**
     * Checks if the user has a specific permission.
     * @param node String - the permission node to check.
     * @return boolean - If the user has a permission (true/false)
     */
    public boolean has(String node) {
        if (this.commandSender instanceof Player) {
            Player player = (Player) this.commandSender;
            return player.hasPermission(node);
        } else {
            return true;
        }
    }

    /**
     * Informs the user that they do not have a permission.
     */
    public boolean not() {
        this.message.PrivateMessage("generic", "missingpermission");
        return false;
    }

    /**
     * Gets the maximum number of homes a player can claim from the permission system.
     * @param player Player - The player to check.
     * @return int - The number of homes (-1 is unlimited)
     */
    public int getHomesLimit(Player player) {
        Set<PermissionAttachmentInfo> perms = player.getEffectivePermissions();
        String[] vars;

        for (PermissionAttachmentInfo i : perms) {
            if (i.getPermission().contains("essence.home.limit")) {
                vars = i.getPermission().split("\\.");
                return Integer.parseInt(vars[vars.length - 1]);
            }
        }

        return -1;
    }

    /**
     * Gets the maximum number of homes a player can claim from the permission system.
     * @param player Player - The player to check.
     * @return int - The number of homes (-1 is unlimited)
     */
    public int getWarpsLimit(Player player) {
        return -1;
    }
}