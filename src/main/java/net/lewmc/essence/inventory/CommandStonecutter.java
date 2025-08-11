package net.lewmc.essence.inventory;

import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /stonecutter command.
 */
public class CommandStonecutter extends FoundryPlayerCommand {
    /**
     * Constructor for the StonecutterCommand class.
     */
    public CommandStonecutter() {}

    /**
     * The required permission
     * @return String - the permission string
     */
    @Override
    protected String requiredPermission() {
        return "essence.inventory.stonecutter";
    }

    /**
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean  true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        Player p = (Player) cs;
        p.openStonecutter(null, true);
        return true;
    }
}