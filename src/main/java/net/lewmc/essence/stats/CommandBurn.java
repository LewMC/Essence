package net.lewmc.essence.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBurn extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the BurnCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandBurn(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     *
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.stats.burn";
    }

    /**
     * @param cs      Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s       Command label - not used here.
     * @param args    The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage message = new UtilMessage(this.plugin, cs);

        if (args.length == 2) {
            try {
                int seconds = Integer.parseInt(args[1]);
                if (seconds <= 0) {
                    message.send("burn", "usage");
                    return true;
                }
                String pName = args[0];
                Player p = Bukkit.getPlayer(pName);
                if (p != null) {
                    message.send("burn", "burned", new String[]{ p.getName() });
                    if (!(cs instanceof Player)) {
                        message.sendTo(p, "burn", "serverburned");
                    } else {
                        message.sendTo(p, "burn", "burnedby", new String[]{ cs.getName() });
                    }
                    p.setFireTicks(seconds * 20);
                } else {
                    message.send("generic", "playernotfound");
                }
            } catch (NumberFormatException e) {
                message.send("burn", "invalidnumber", new String[] { args[1] });
            }
        } else {
            message.send("burn", "usage");
        }
        return true;
    }
}
