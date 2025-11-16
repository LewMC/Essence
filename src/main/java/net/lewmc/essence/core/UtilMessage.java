package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

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
     * @param msg String - The message taken from the language file.
     * @param replace String[] - Text that should be put in place of {{X}} in the message.
     * @since 1.5.3
     */
    public void send(String group, String msg, String[] replace) {
        String message = this.getMessage(msg, group);
        if (message != null) {
            int i = 1;
            for (String item : replace) {
                message = message.replace("{{"+i+"}}", item);
                i++;
            }
            this.cs.sendMessage(new UtilPlaceholder(this.plugin, this.cs).replaceAll(message));
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            new Logger(this.plugin.foundryConfig).warn("Unable to send message '"+group+"."+msg+"' to player, could not find key in en-GB.yml");
        }
    }

    /**
     * Send a message to the user.
     * @param group String - The group the message belongs to in the language file.
     * @param msg String - The message taken from the language file.
     * @since 1.6.0
     */
    public void send(String group, String msg) {
        String message = this.getMessage(msg, group);
        if (message != null) {
            this.cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            new Logger(this.plugin.foundryConfig).warn("Unable to send message '"+group+"."+msg+"' to player, could not find key in en-GB.yml");
        }
    }

    /**
     * Send a message to a user.
     * @param cs CommandSender - The player to send the message to.
     * @param group String - The group the message belongs to in the language file.
     * @param msg String - The message taken from the language file.
     * @since 1.6.0
     */
    public void sendTo(CommandSender cs, String group, String msg) {
        String message = this.getMessage(msg, group);
        if (message != null) {
            cs.sendMessage(message);
        } else {
            cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            new Logger(this.plugin.foundryConfig).warn("Unable to send message '"+group+"."+msg+"' to player, could not find key in en-GB.yml");
        }
    }

    /**
     * Send a message to a user.
     * @param cs CommandSender - The player to send the message to.
     * @param group String - The group the message belongs to in the language file.
     * @param msg String - The message taken from the language file.
     * @param replace String[] - Text that should be put in place of {{X}} in the message.
     * @since 1.6.0
     */
    public void sendTo(CommandSender cs, String group, String msg, String[] replace) {
        String message = this.getMessage(msg, group);
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
            new Logger(this.plugin.foundryConfig).warn("Unable to send message '"+group+"."+msg+"' to player, could not find key in en-GB.yml");
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
     * Send a message with clickable buttons to a player.
     * @param cs CommandSender - The player to send the message to.
     * @param group String - The group the message belongs to in the language file.
     * @param msg String - The message taken from the language file.
     * @param replace String[] - Text that should be put in place of {{X}} in the message.
     * @param acceptCommand String - The command to execute when accept button is clicked.
     * @param denyCommand String - The command to execute when deny button is clicked.
     * @since 1.11.0
     */
    public void sendToWithButtons(CommandSender cs, String group, String msg, String[] replace, String acceptCommand, String denyCommand) {
        if (!(cs instanceof Player)) {
            // Fallback to regular message for non-players
            this.sendTo(cs, group, msg, replace);
            return;
        }
        
        Player player = (Player) cs;
        String message = this.getMessage(msg, group);
        if (message != null) {
            int i = 1;
            for (String item : replace) {
                message = message.replace("{{" + i + "}}", item);
                i++;
            }
            
            // Create the main message component
            TextComponent mainMessage = new TextComponent(new UtilPlaceholder(this.plugin, cs).replaceAll(message));
            
            // Create accept button
            TextComponent acceptButton = new TextComponent(" [" + this.getMessage("acceptbutton", group) + "]");
            acceptButton.setColor(net.md_5.bungee.api.ChatColor.GREEN);
            acceptButton.setBold(true);
            acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, acceptCommand));
            acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                new ComponentBuilder(this.getMessage("accepthover", group)).create()));
            
            // Create deny button
            TextComponent denyButton = new TextComponent(" [" + this.getMessage("denybutton", group) + "]");
            denyButton.setColor(net.md_5.bungee.api.ChatColor.RED);
            denyButton.setBold(true);
            denyButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, denyCommand));
            denyButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                new ComponentBuilder(this.getMessage("denyhover", group)).create()));
            
            // Send the message with buttons
            player.spigot().sendMessage(mainMessage, acceptButton, denyButton);
        } else {
            cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            new Logger(this.plugin.foundryConfig).warn("Unable to send message '"+group+"."+msg+"' to player, could not find key in en-GB.yml");
        }
    }

    /**
     * Retrieves the message from the language file.
     * @param code String - The code of the specific message to be retrieved.
     * @param group String - The group that the message is within.
     * @return String - The message from the language file.
     */
    private String getMessage(String code, String group) {
        String language = (String) this.plugin.config.get("language");
        Files data = new Files(this.plugin.foundryConfig, this.plugin);
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