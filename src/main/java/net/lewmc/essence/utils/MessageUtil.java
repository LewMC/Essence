package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtil {

    private final CommandSender cs;
    private final Essence plugin;

    public MessageUtil(CommandSender cs, Essence plugin) {
        this.cs = cs;
        this.plugin = plugin;
    }
    public void PrivateMessage(String Message, boolean Error) {
        if (Error) {
            this.cs.sendMessage(ChatColor.DARK_RED + "[" + plugin.getConfig().get("chat-prefix") + "] " + ChatColor.RED + Message);
        } else {
            this.cs.sendMessage(ChatColor.GOLD + "[" + plugin.getConfig().get("chat-prefix") + "] " + ChatColor.YELLOW + Message);
        }
    }

    public void BroadcastMessage(String Message) {
        Bukkit.broadcastMessage(ChatColor.GOLD + "Broadcast > " + ChatColor.YELLOW + Message);
    }
}
