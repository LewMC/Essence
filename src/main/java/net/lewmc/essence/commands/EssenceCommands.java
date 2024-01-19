package net.lewmc.essence.commands;

import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.Essence;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EssenceCommands implements CommandExecutor {
    private final Essence plugin;

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
    public boolean onCommand(
        @NotNull CommandSender commandSender,
        @NotNull Command command,
        @NotNull String s,
        String[] args
    ) {
        MessageUtil message = new MessageUtil(commandSender, plugin);

        if (command.getName().equalsIgnoreCase("essence")) {
            if (args.length > 0) {
                if ("help".equals(args[0])) {
                    HelpCommand helpCommand = new HelpCommand(message, args);
                    return helpCommand.runHelpCommand();
                }
            } else {
                message.PrivateMessage("Running Essence "+plugin.getDescription().getVersion()+" for Minecraft "+plugin.getDescription().getAPIVersion(), false);
                message.PrivateMessage("Essence is a lightweight and customisable plugin that adds warps, homes, and more.", false);
                message.PrivateMessage("Created by LewMC.", false);
                message.PrivateMessage("Use /es help for more commands", false);

                return true;
            }
        }

        return false;
    }
}
