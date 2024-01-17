package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class DelhomeCommand implements CommandExecutor {
    private final LogUtil log;
    private Essence plugin;

    /**
     * Constructor for the DelhomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public DelhomeCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
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
            this.log.noConsole();
            return true;
        }
        MessageUtil message = new MessageUtil(commandSender,plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(player, message);

        if (command.getName().equalsIgnoreCase("delhome")) {
            if (permission.has("essence.home.delete")) {
                if (args.length == 0) {
                    message.PrivateMessage("Usage: /delhome <name>", true);
                    return true;
                }
                DataUtil config = new DataUtil(this.plugin, message);
                config.load(config.playerDataFile(player));

                String homeName = args[0].toLowerCase();

                ConfigurationSection cs = config.getSection("homes");

                cs.set(homeName, null);

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
