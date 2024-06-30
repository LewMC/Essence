package net.lewmc.essence.commands;

import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.PermissionHandler;
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
                } else if ("reload".equals(args[0])) {
                    PermissionHandler perms = new PermissionHandler(commandSender, message);
                    if (perms.has("essence.admin.reload")) {
                        this.plugin.reloadConfig();
                        this.plugin.verbose = this.plugin.getConfig().getBoolean("verbose");
                        message.send("generic", "reload");
                        return true;
                    } else {
                        return perms.not();
                    }
                }
            } else {
                message.PrivateMessage("about", "version", plugin.getDescription().getVersion());
                message.PrivateMessage("about", "description");
                message.PrivateMessage("about", "author");
                message.PrivateMessage("about", "issues");
                message.PrivateMessage("about", "more");

                return true;
            }
        }

        return false;
    }
}
