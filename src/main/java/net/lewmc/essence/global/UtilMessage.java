package net.lewmc.essence.global;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Essence's Messaging Utility
 */
public class UtilMessage {

    private final CommandSender cs;
    private final Essence plugin;

    /**
     * Constructor for the MessageUtil class
     * @param cs CommandSender - the user who sent the command.
     * @param plugin Reference to the main Essence class.
     */
    public UtilMessage(Essence plugin, CommandSender cs) {
        this.plugin = plugin;
        this.cs = cs;
    }

    /**
     * Send a message to the user with additional data.
     * @param group String - The group the message belongs to in the language file.
     * @param message String - The message taken from the language file.
     * @param replace String[] - Text that should be put in place of {{X}} in the message.
     * @since 1.5.3
     */
    public void send(String group, String message, String[] replace) {
        message = this.getMessage(message, group);
        if (message != null) {
            int i = 1;
            for (String item : replace) {
                message = message.replace("{{"+i+"}}", item);
                i++;
            }
            this.cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            new Logger(this.plugin.config).warn("Unable to send message '"+group+".null' to player, could not find key in en-GB.yml");
        }
    }

    /**
     * Send a message to the user.
     * @param group String - The group the message belongs to in the language file.
     * @param message String - The message taken from the language file.
     * @since 1.6.0
     */
    public void send(String group, String message) {
        message = this.getMessage(message, group);
        if (message != null) {
            this.cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            new Logger(this.plugin.config).warn("Unable to send message '"+group+".null' to player, could not find key in en-GB.yml");
        }
    }

    /**
     * Send a message to a user.
     * @param cs CommandSender - The player to send the message to.
     * @param group String - The group the message belongs to in the language file.
     * @param message String - The message taken from the language file.
     * @since 1.6.0
     */
    public void sendTo(CommandSender cs, String group, String message) {
        message = this.getMessage(message, group);
        if (message != null) {
            cs.sendMessage(message);
        } else {
            cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            new Logger(this.plugin.config).warn("Unable to send message '"+group+".null' to player, could not find key in en-GB.yml");
        }
    }

    /**
     * Send a message to a user.
     * @param cs CommandSender - The player to send the message to.
     * @param group String - The group the message belongs to in the language file.
     * @param message String - The message taken from the language file.
     * @param replace String[] - Text that should be put in place of {{X}} in the message.
     * @since 1.6.0
     */
    public void sendTo(CommandSender cs, String group, String message, String[] replace) {
        message = this.getMessage(message, group);
        if (message != null) {
            int i = 1;
            for (String item : replace) {
                message = message.replace("{{"+i+"}}", item);
                i++;
            }
            cs.sendMessage(message);
        } else {
            cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            new Logger(this.plugin.config).warn("Unable to send message '"+group+".null' to player, could not find key in en-GB.yml");
        }
    }

    /**
     * Broadcasts a message to the server.
     * @param message String - The message to be sent.
     */
    public void broadcast(String message) {
        Bukkit.broadcastMessage(ChatColor.GOLD + "Broadcast > " + ChatColor.YELLOW + message);
    }

    /**
     * Retrieves the message from the language file.
     * @param code String - The code of the specific message to be retrieved.
     * @param group String - The group that the message is within.
     * @return String - The message from the language file.
     */
    private String getMessage(String code, String group) {
        String language = this.plugin.getConfig().getString("language");
        Files data = new Files(this.plugin.config, this.plugin);
        data.load("language/"+language+".yml");

        if (data.get(group) != null) {
            String toSend = data.getString(group+"."+code);
            data.close();
            return toSend;
        } else {
            data.close();
            return null;
        }
    }
}