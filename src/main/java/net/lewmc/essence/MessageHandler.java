package net.lewmc.essence;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageHandler {

    private CommandSender cs;
    private Essence plugin;

    public MessageHandler(CommandSender cs, Essence plugin) {
        this.cs = cs;
        this.plugin = plugin;
    }
    public void PrivateMessage(String Message, boolean Error) {
        if (Error) {
            this.cs.sendMessage(ChatColor.DARK_RED + "[" + plugin.getConfig().get("prefix") + "] " + ChatColor.RED + Message);
        } else {
            this.cs.sendMessage(ChatColor.GOLD + "[" + plugin.getConfig().get("prefix") + "] " + ChatColor.YELLOW + Message);
        }
    }

    public void BroadcastMessage(String Message) {
        Bukkit.broadcastMessage(ChatColor.GOLD + "Broadcast > " + ChatColor.YELLOW + Message);
    }
}
