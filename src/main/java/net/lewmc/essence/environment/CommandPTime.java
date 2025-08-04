package net.lewmc.essence.environment;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /ptime command.
 */
public class CommandPTime extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandPTime class.
     * @param plugin References to the main plugin class.
     */
    public CommandPTime(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.environment.ptime";
    }

    /**
     * /bal command handler.
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("ptime")) {
            return cmd.disabled();
        }

        Player p = (Player) cs;

        UtilEnvironment env = new UtilEnvironment();
        UtilMessage msg = new UtilMessage(plugin, cs);

        if (args.length == 0) {
            msg.send("environment", "playertime", new String[]{String.valueOf(env.getPlayerTime(p))});
        } else if (args.length == 1) {
            if (new UtilPermission(plugin,cs).has("essence.environment.ptime.set")) {
                if (args[0].equalsIgnoreCase("day") || args[0].equalsIgnoreCase("morning")) {
                    env.setPlayerTime(p, UtilEnvironment.Time.DAY);
                } else if (args[0].equalsIgnoreCase("noon") || args[0].equalsIgnoreCase("midday")) {
                    env.setPlayerTime(p, UtilEnvironment.Time.MIDDAY);
                } else if (args[0].equalsIgnoreCase("evening")) {
                    env.setPlayerTime(p, UtilEnvironment.Time.EVENING);
                } else if (args[0].equalsIgnoreCase("night")) {
                    env.setPlayerTime(p, UtilEnvironment.Time.NIGHT);
                } else if (args[0].equalsIgnoreCase("midnight")) {
                    env.setPlayerTime(p, UtilEnvironment.Time.MIDNIGHT);
                } else if (args[0].equalsIgnoreCase("sunrise")) {
                    env.setPlayerTime(p, UtilEnvironment.Time.SUNRISE);
                } else if (args[0].equalsIgnoreCase("reset")) {
                    env.setPlayerTime(p, UtilEnvironment.Time.RESET);
                    msg.send("environment", "playertimereset");
                    return true;
                } else {
                    try {
                        env.setPlayerTime(p, Long.parseLong(args[0]));
                    } catch (NumberFormatException e) {
                        msg.send("generic", "unknowntime", new String[]{args[0]});
                        msg.send("generic", "timehelp");
                        return true;
                    }
                }
                msg.send("environment", "playertimeset", new String[]{String.valueOf(env.getTime(p.getWorld())), args[0]});
            } else {
                new UtilPermission(plugin, cs).not();
            }
        }

        return true;
    }
}