package net.lewmc.essence.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionHandler {

    private final CommandSender commandSender;
    private final MessageUtil message;

    public PermissionHandler(CommandSender commandSender, MessageUtil message) {
        this.commandSender = commandSender;
        this.message = message;
    }

    public boolean has(String node) {
        if (this.commandSender instanceof Player) {
            Player player = (Player) this.commandSender;
            return player.hasPermission(node);
        } else {
            return true;
        }
    }

    public boolean not() {
        this.message.PrivateMessage("generic", "missingpermission");
        return true;
    }
}