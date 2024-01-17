package net.lewmc.essence.utils;

import org.bukkit.entity.Player;

public class PermissionHandler {

    private final Player player;
    private final MessageUtil message;

    public PermissionHandler(Player player, MessageUtil message) {
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