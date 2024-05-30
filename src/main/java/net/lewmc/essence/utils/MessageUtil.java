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
    public void PrivateMessage(String group, String message) {
        message = this.GetMessage(message, group);
        if (message != null) {
            this.cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to send message '"+group+"."+message+"' to player, could not find key in en-gb.yml");
        }
    }
    public void PrivateMessage(String group, String message, String extras) {
        message = this.GetMessage(message, group);
        if (message != null) {
            message = message.replace("{{1}}", extras);
            this.cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to send message '"+group+"."+message+"' to player, could not find key in en-gb.yml");
        }
    }
    public void PrivateMessage(String group, String message, String extra1, String extra2) {
        message = this.GetMessage(message, group);
        if (message != null) {
            message = message.replace("{{1}}", extra1);
            message = message.replace("{{2}}", extra2);
            this.cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to send message '"+group+"."+message+"' to player, could not find key in en-gb.yml");
        }
    }

    public void BroadcastMessage(String Message) {
        Bukkit.broadcastMessage(ChatColor.GOLD + "Broadcast > " + ChatColor.YELLOW + Message);
    }

    public void SendTo(CommandSender cs, String group, String message) {
        message = this.GetMessage(message, group);
        if (message != null) {
            cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to send message '"+group+"."+message+"' to player, could not find key in en-gb.yml");
        }
    }

    public void SendTo(CommandSender cs, String group, String message, String extra1) {
        message = this.GetMessage(message, group);
        if (message != null) {
            message = message.replace("{{1}}", extra1);
            cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to send message '"+group+"."+message+"' to player, could not find key in en-gb.yml");
        }
    }

    public void SendTo(CommandSender cs, String group, String message, String extra1, String extra2) {
        message = this.GetMessage(message, group);
        if (message != null) {
            message = message.replace("{{1}}", extra1);
            message = message.replace("{{2}}", extra2);
            cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to send message '"+group+"."+message+"' to player, could not find key in en-gb.yml");
        }
    }

    private String GetMessage(String code, String group) {
        String language = this.plugin.getConfig().getString("language");
        FileUtil data = new FileUtil(this.plugin);
        data.load("language/"+language+".yml");

        if (data.get(group) != null) {
            String toSend = data.getString("group."+code);
            data.close();
            return toSend;
        } else {
            data.close();
            return null;
        }
    }
}
