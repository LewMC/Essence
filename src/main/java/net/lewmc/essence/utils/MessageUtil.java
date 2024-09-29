package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * Essence's Messaging Utility
 */
public class MessageUtil {

    private final CommandSender cs;
    private final Essence plugin;

    /**
     * Constructor for the MessageUtil class
     * @param cs CommandSender - the user who sent the command.
     * @param plugin Reference to the main Essence class.
     */
    public MessageUtil(CommandSender cs, Essence plugin) {
        this.cs = cs;
        this.plugin = plugin;
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
            for (int i = 0; i < replace.length; i++) {
                message = message.replace("{{" + (i + 1) + "}}", replace[i]);
            }
            this.cs.sendMessage(Component.text(message)); // Send the formatted message
        } else {
            sendErrorMessage(cs);
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
            this.cs.sendMessage(Component.text(message)); // Send the formatted message
        } else {
            sendErrorMessage(cs);
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
            cs.sendMessage(Component.text(message)); // Send the formatted message
        } else {
            sendErrorMessage(cs);
        }
    }

    /**
     * Send a message to a user with additional data.
     * @param cs CommandSender - The player to send the message to.
     * @param group String - The group the message belongs to in the language file.
     * @param message String - The message taken from the language file.
     * @param replace String[] - Text that should be put in place of {{X}} in the message.
     * @since 1.6.0
     */
    public void sendTo(CommandSender cs, String group, String message, String[] replace) {
        message = this.getMessage(message, group);
        if (message != null) {
            for (int i = 0; i < replace.length; i++) {
                message = message.replace("{{" + (i + 1) + "}}", replace[i]);
            }
            cs.sendMessage(Component.text(message)); // Send the formatted message
        } else {
            sendErrorMessage(cs);
        }
    }

    /**
     * Broadcasts a message to the server.
     * @param message String - The message to be sent.
     */
    public void broadcast(String message) {
        Bukkit.broadcast(Component.text("Broadcast > ").color(NamedTextColor.GOLD)
                .append(Component.text(message).color(NamedTextColor.YELLOW)));
    }

    /**
     * Retrieves the message from the language file.
     * @param code String - The code of the specific message to be retrieved.
     * @param group String - The group that the message is within.
     * @return String - The message from the language file.
     */
    private String getMessage(String code, String group) {
        String language = this.plugin.getConfig().getString("language");
        FileUtil data = new FileUtil(this.plugin);
        data.load("language/" + language + ".yml");

        if (data.get(group) != null) {
            String toSend = data.getString(group + "." + code);
            data.close();
            return toSend;
        } else {
            data.close();
            return null;
        }
    }

    /**
     * Send an error message to a specific CommandSender.
     * @param cs CommandSender - The recipient of the error message.
     */
    private void sendErrorMessage(CommandSender cs) {
        Component errorMessage = Component.text("[Essence] Unable to send message to player, see console for more information.")
                .color(NamedTextColor.DARK_RED);
        cs.sendMessage(errorMessage);
    }
}
