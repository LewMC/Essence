package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.Logger;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.*;
import java.util.Arrays;

public class CommandRules extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the RulesCommands class.
     * @param plugin References to the main plugin class.
     */
    public CommandRules(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Permission required
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.rules";
    }

    /**
     * /rules command handler.
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage msg = new UtilMessage(this.plugin, cs);
        try (BufferedReader br = new BufferedReader(new FileReader(this.plugin.getDataFolder() + File.separator + "rules.txt"))) {
            String rule;
            UtilPlaceholder pu = new UtilPlaceholder(this.plugin, cs);
            while ((rule = br.readLine()) != null) {
                msg.send("other", "rule", new String[] {pu.replaceAll(rule)});
            }
        } catch (IOException e) {
            Logger log = new Logger(this.plugin.config);
            msg.send("generic", "exception");
            log.severe("Unable to display rules.");
            log.severe(e.getMessage());
            log.severe(Arrays.toString(e.getStackTrace()));
        }
        return true;
    }
}
