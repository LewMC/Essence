package net.lewmc.essence.events;

import net.lewmc.essence.MessageHandler;
import org.bukkit.entity.Player;

public class PermissionHandler {

    private final Player player;
    private MessageHandler message;

    public PermissionHandler(Player player, MessageHandler message) {
        this.player = player;
        this.message = message;
    }

    public boolean has(String exact) {
        return player.hasPermission(exact);
    }

    public boolean not() {
        this.message.PrivateMessage("You don't have the required permission to run this command.", true);
        return true;
    }
}