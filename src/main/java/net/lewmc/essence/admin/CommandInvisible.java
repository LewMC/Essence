package net.lewmc.essence.admin;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.stats.UtilStats;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /visible command.
 */
public class CommandInvisible extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the InvisibleCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandInvisible(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.admin.invisible";
    }

    /**
     * The Invisible command.
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("invisible")) { return cmd.disabled(); }

        return new UtilStats(this.plugin, (Player) cs).toggleInvisible();
    }
}
