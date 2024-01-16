package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.MessageHandler;
import net.lewmc.essence.utils.PermissionHandler;
import net.lewmc.essence.utils.ConfigUtil;
import net.lewmc.essence.utils.HomeUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class DelhomeCommand implements CommandExecutor {
    private Essence plugin;

    /**
     * Constructor for the GamemodeCommands class.
     *
     * @param plugin References to the main plugin class.
     */
    public DelhomeCommand(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * @param commandSender Information about who sent the command - player or console.
     * @param command       Information about what command was sent.
     * @param s             Command label - not used here.
     * @param args          The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            plugin.getLogger().warning("[Essence] Sorry, you need to be an in-game player to use this command.");
            return true;
        }
        MessageHandler message = new MessageHandler(commandSender,plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(player, message);

        if (command.getName().equalsIgnoreCase("delhome")) {
            if (permission.has("essence.home.delete")) {
                if (args.length == 0) {
                    message.PrivateMessage("Usage: /delhome <name>", true);
                    return true;
                }
                ConfigUtil config = new ConfigUtil(this.plugin, message);
                config.load("homes.yml");

                String homeName = args[0].toLowerCase();

                ConfigurationSection cs = config.getSection("homes");

                HomeUtil homeUtil = new HomeUtil();
                cs.set(homeUtil.HomeName(player.getUniqueId(), homeName), null);

                // Save the configuration to the file
                config.save();

                message.PrivateMessage("Deleted home '" + args[0] + "'.", false);
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}
