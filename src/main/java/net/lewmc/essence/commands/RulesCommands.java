package net.lewmc.essence.commands;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.CommandUtil;
import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.PermissionHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Arrays;

public class RulesCommands implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the RulesCommands class.
     * @param plugin References to the main plugin class.
     */
    public RulesCommands(Essence plugin) {
        this.plugin = plugin;
    }

    /**
    * /rules command handler.
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
        if (command.getName().equalsIgnoreCase("rules")) {
            CommandUtil cmd = new CommandUtil(this.plugin);
            MessageUtil message = new MessageUtil(commandSender, plugin);
            if (cmd.isDisabled("rules")) {
                return cmd.disabled(message);
            }

            PermissionHandler perms = new PermissionHandler(commandSender, message);
            if (perms.has("essence.rules")) {
                try (BufferedReader br = new BufferedReader(new FileReader(this.plugin.getDataFolder() + File.separator + "rules.txt"))) {
                    String rule;
                    while ((rule = br.readLine()) != null) {
                        message.send("other", "rule", new String[] {rule});
                    }
                } catch (IOException e) {
                    LogUtil log = new LogUtil(this.plugin);
                    message.send("generic", "exception");
                    log.severe("Unable to display rules.");
                    log.severe(e.getMessage());
                    log.severe(Arrays.toString(e.getStackTrace()));
                }
                return true;
            } else {
                return perms.not();
            }
        }

        return false;
    }
}
