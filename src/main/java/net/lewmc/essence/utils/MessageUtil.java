package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        message = this.GetMessage(message, group);
        if (message != null) {
            int i = 1;
            for (String item : replace) {
                message = message.replace("{{"+i+"}}", item);
                i++;
            }
            this.cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to send message '"+group+"."+message+"' to player, could not find key in en-GB.yml");
        }
    }

    /**
     * Send a message to the user.
     * @param group String - The group the message belongs to in the language file.
     * @param message String - The message taken from the language file.
     */
    public void send(String group, String message) {
        message = this.GetMessage(message, group);
        if (message != null) {
            this.cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to send message '"+group+"."+message+"' to player, could not find key in en-GB.yml");
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
        message = this.GetMessage(message, group);
        if (message != null) {
            cs.sendMessage(message);
        } else {
            cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to send message '"+group+"."+message+"' to player, could not find key in en-GB.yml");
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
        message = this.GetMessage(message, group);
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
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to send message '"+group+"."+message+"' to player, could not find key in en-GB.yml");
        }
    }

    /**
     * Send a message to the user - use send(String, String) instead.
     * @param group String - The group the message belongs to in the language file.
     * @param message String - The message taken from the language file.
     * @deprecated
     */
    @Deprecated
    public void PrivateMessage(String group, String message) {
        message = this.GetMessage(message, group);
        if (message != null) {
            this.cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to send message '"+group+"."+message+"' to player, could not find key in en-GB.yml");
        }
    }

    /**
     * Send a message to the user - Use send(String, String, String[]) instead.
     * @param group String - The group the message belongs to in the language file.
     * @param message String - The message taken from the language file.
     * @param extra1 String - Text that should be put in place of {{1}} in the message.
     * @deprecated
     */
    @Deprecated
    public void PrivateMessage(String group, String message, String extra1) {
        message = this.GetMessage(message, group);
        if (message != null) {
            message = message.replace("{{1}}", extra1);
            this.cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to send message '"+group+"."+message+"' to player, could not find key in en-GB.yml");
        }
    }

    /**
     * Send a message to the user - Use send(String, String, String[]) instead.
     * @param group String - The group the message belongs to in the language file.
     * @param message String - The message taken from the language file.
     * @param extra1 String - Text that should be put in place of {{1}} in the message.
     * @param extra2 String - Text that should be put in place of {{2}} in the message.
     * @deprecated
     */
    @Deprecated
    public void PrivateMessage(String group, String message, String extra1, String extra2) {
        message = this.GetMessage(message, group);
        if (message != null) {
            message = message.replace("{{1}}", extra1);
            message = message.replace("{{2}}", extra2);
            this.cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to send message '"+group+"."+message+"' to player, could not find key in en-GB.yml");
        }
    }

    /**
     * Broadcasts a message to the server.
     * @param message String - The message to be sent.
     */
    public void BroadcastMessage(String message) {
        Bukkit.broadcastMessage(ChatColor.GOLD + "Broadcast > " + ChatColor.YELLOW + message);
    }

    /**
     * Send a message to a specific user. Use sendTo(Player, String, String) instead.
     * @param group String - The group the message belongs to in the language file.
     * @param message String - The message taken from the language file.
     * @deprecated
     */
    @Deprecated
    public void SendTo(CommandSender cs, String group, String message) {
        message = this.GetMessage(message, group);
        if (message != null) {
            cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to send message '"+group+"."+message+"' to player, could not find key in en-GB.yml");
        }
    }


    /**
     * Send a message to a specific user. Use sendTo(Player, String, String, String[]) instead.
     * @param group String - The group the message belongs to in the language file.
     * @param message String - The message taken from the language file.
     * @param extra1 String - Text that should be put in place of {{1}} in the message.
     * @deprecated
     */
    @Deprecated
    public void SendTo(CommandSender cs, String group, String message, String extra1) {
        message = this.GetMessage(message, group);
        if (message != null) {
            message = message.replace("{{1}}", extra1);
            cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to send message '"+group+"."+message+"' to player, could not find key in en-GB.yml");
        }
    }

    /**
     * Send a message to a specific user. Use sendTo(Player, String, String, String[]) instead.
     * @param group String - The group the message belongs to in the language file.
     * @param message String - The message taken from the language file.
     * @param extra1 String - Text that should be put in place of {{1}} in the message.
     * @param extra2 String - Text that should be put in place of {{2}} in the message.
     * @deprecated
     */
    @Deprecated
    public void SendTo(CommandSender cs, String group, String message, String extra1, String extra2) {
        message = this.GetMessage(message, group);
        if (message != null) {
            message = message.replace("{{1}}", extra1);
            message = message.replace("{{2}}", extra2);
            cs.sendMessage(message);
        } else {
            this.cs.sendMessage(ChatColor.DARK_RED + "[Essence] " + ChatColor.RED + "Unable to send message to player, see console for more information.");
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to send message '"+group+"."+message+"' to player, could not find key in en-GB.yml");
        }
    }

    /**
     * Retrieves the message from the language file.
     * @param code String - The code of the specific message to be retrieved.
     * @param group String - The group that the message is within.
     * @return String - The message from the language file.
     */
    private String GetMessage(String code, String group) {
        String language = this.plugin.getConfig().getString("language");
        FileUtil data = new FileUtil(this.plugin);
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
