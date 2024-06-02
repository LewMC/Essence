package net.lewmc.essence.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
}