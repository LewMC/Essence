package net.lewmc.essence.commands;

import net.lewmc.essence.MessageHandler;
import net.lewmc.essence.Essence;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EssenceCommands implements CommandExecutor {
    public Essence plugin;

    /**
     * Constructor for the EssenceCommands class.
     * @param plugin References to the main plugin class.
     */
    public EssenceCommands(Essence plugin) {
        this.plugin = plugin;
    }

    /**
    * /essence command handler.
    * @param commandSender Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            plugin.getLogger().warning("[Essence] Sorry, you need to be an in-game player to use this command.");
            return true;
        }
        MessageHandler message = new MessageHandler(commandSender, plugin);
        Player player = (Player) commandSender;

        /*
         *  CMD HELP
         */
        if (command.getName().equalsIgnoreCase("es") || command.getName().equalsIgnoreCase("essence")) {
            if (args.length > 0) {
                if ("help".equals(args[0])) {
                    HelpCommand helpCommand = new HelpCommand(message, args);
                    return helpCommand.runHelpCommand();
                }
            } else {
                message.PrivateMessage("Running Essence "+plugin.getDescription().getVersion()+" for Minecraft "+plugin.getDescription().getAPIVersion(), false);
                message.PrivateMessage("Essence is a plugin that adds a number of basic commands created by Lewis Milburn.", false);
                message.PrivateMessage("Use /es help for more commands", false);

                return true;
            }
        }

        return false;
    }
}
