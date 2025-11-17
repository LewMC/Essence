package net.lewmc.essence.inventory;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /clear command.
 */
public class CommandClear extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandClear class.
     *
     * @param plugin References to the main plugin class
     */
    public CommandClear(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission
     * @return String - the permission string
     */
    @Override
    protected String requiredPermission() {
        return "essence.inventory.clear";
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
        UtilMessage msg = new UtilMessage(this.plugin, cs);
        UtilPermission perm = new UtilPermission(this.plugin, cs);

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                msg.send("generic","playernotfound");
                return true;
            }

            if (perm.has("essence.inventory.clear.other")) {
                target.getInventory().clear();
                msg.send("clear", "clearedother", new String[]{ target.getName() });
                msg.sendTo(target, "clear", "clearedby", new String[]{ cs.getName() });
            } else {
                return perm.not();
            }
        } else if (args.length == 0) {
            if (cs instanceof Player) {
                ((Player) cs).getInventory().clear();
                msg.send("clear", "cleared");
            } else {
                msg.send("clear", "consoleusage");
            }
        } else {
            msg.send("clear", "usage");
        }
        return true;
    }
}