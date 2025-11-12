package net.lewmc.essence.admin;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /sudo command.
 */
public class CommandSudo extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the SudoCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandSudo(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.admin.sudo";
    }

    /**
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage message = new UtilMessage(this.plugin, cs);
        if (args.length >= 2) {
            Player p = Bukkit.getPlayer(args[0]);
            if (p != null && p.isOnline()) {
                StringBuilder cmd = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    cmd.append(args[i]).append(" ");
                }

                p.performCommand(cmd.toString().trim());
                message.send("sudo", "ran", new String[]{ p.getName(), cmd.toString() });
            } else {
                message.send("generic", "playernotfound");
            }
        } else {
            message.send("sudo", "usage");
        }
        return true;
    }
}