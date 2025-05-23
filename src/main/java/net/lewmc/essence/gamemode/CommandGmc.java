package net.lewmc.essence.gamemode;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * The /gmc command.
 */
public class CommandGmc extends FoundryCommand {

    private final Essence plugin;

    /**
     * Constructor for the CommandGmc class.
     * @param plugin References to the main plugin class.
     */
    public CommandGmc(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return null;
    }

    /**
     * Runs the command.
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean  true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        return new UtilGamemode().processShortCommand("gmc", GameMode.CREATIVE, cs, args, this.plugin);
    }
}
