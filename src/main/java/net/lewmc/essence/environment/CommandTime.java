package net.lewmc.essence.environment;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * /time command.
 */
public class CommandTime extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandTime class.
     * @param plugin References to the main plugin class.
     */
    public CommandTime(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.environment.time";
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
        UtilEnvironment env = new UtilEnvironment();
        UtilMessage msg = new UtilMessage(plugin, cs);

        if (args.length == 0 || (args.length == 1 && (Objects.equals(args[0], "time") || Objects.equals(args[0], "get") || Objects.equals(args[0], "query") || Objects.equals(args[0], "current") || Objects.equals(args[0], "now")))) {
            if (cs instanceof Player p) {
                // Player check time
                msg.send("environment", "time", new String[]{String.valueOf(env.getTime(p.getWorld())), p.getWorld().getName()});
            } else {
                // Console didn't specify a world
                msg.send("environment", "timeconsoleusage");
            }
        } else if (args.length == 1 || (args.length == 2 && Objects.equals(args[0], "set"))) {
            if (Objects.equals(args[0], "set")) {
                args[0] = args[1];
            }

            if (cs instanceof Player p) {
                // Player set time
                if (new UtilPermission(plugin,cs).has("essence.environment.time.set")) {
                    if (args[0].equalsIgnoreCase("day") || args[0].equalsIgnoreCase("morning")) {
                        env.setTime(p.getWorld(), UtilEnvironment.Time.DAY);
                    } else if (args[0].equalsIgnoreCase("noon") || args[0].equalsIgnoreCase("midday")) {
                        env.setTime(p.getWorld(), UtilEnvironment.Time.MIDDAY);
                    } else if (args[0].equalsIgnoreCase("evening")) {
                        env.setTime(p.getWorld(), UtilEnvironment.Time.EVENING);
                    } else if (args[0].equalsIgnoreCase("night")) {
                        env.setTime(p.getWorld(), UtilEnvironment.Time.NIGHT);
                    } else if (args[0].equalsIgnoreCase("midnight")) {
                        env.setTime(p.getWorld(), UtilEnvironment.Time.MIDNIGHT);
                    } else if (args[0].equalsIgnoreCase("sunrise")) {
                        env.setTime(p.getWorld(), UtilEnvironment.Time.SUNRISE);
                    } else {
                        try {
                            env.setTime(p.getWorld(), Long.parseLong(args[0]));
                        } catch (NumberFormatException e) {
                            msg.send("generic", "unknowntime", new String[]{args[0]});
                            msg.send("generic", "timehelp");
                            return true;
                        }
                    }
                    msg.send("environment", "timeset", new String[]{String.valueOf(env.getTime(p.getWorld())), args[0]});
                } else {
                    return new UtilPermission(plugin,cs).not();
                }
            } else {
                // Console check time
                if (Bukkit.getWorld(args[0]) != null) {
                    msg.send("environment", "time", new String[]{String.valueOf(env.getTime(Bukkit.getWorld(args[0]))), args[0]});
                } else {
                    msg.send("generic", "worldnotfound", new String[]{args[0]});
                }
            }
        } else if (args.length == 2) {
            if (cs instanceof Player) {
                // Player too many args.
                msg.send("environment", "timeplayerusage", new String[]{args[0]});
            } else {
                // Console set time
                if (new UtilPermission(plugin, cs).has("essence.environment.time.set")) {

                    if (Bukkit.getWorld(args[0]) == null) {
                        msg.send("generic", "worldnotfound", new String[]{args[0]});
                    }

                    if (args[1].equalsIgnoreCase("day") || args[1].equalsIgnoreCase("morning")) {
                        env.setTime(Bukkit.getWorld(args[0]), UtilEnvironment.Time.DAY);
                    } else if (args[1].equalsIgnoreCase("noon") || args[1].equalsIgnoreCase("midday")) {
                        env.setTime(Bukkit.getWorld(args[0]), UtilEnvironment.Time.MIDDAY);
                    } else if (args[1].equalsIgnoreCase("evening")) {
                        env.setTime(Bukkit.getWorld(args[0]), UtilEnvironment.Time.EVENING);
                    } else if (args[1].equalsIgnoreCase("night")) {
                        env.setTime(Bukkit.getWorld(args[0]), UtilEnvironment.Time.NIGHT);
                    } else if (args[1].equalsIgnoreCase("midnight")) {
                        env.setTime(Bukkit.getWorld(args[0]), UtilEnvironment.Time.MIDNIGHT);
                    } else if (args[1].equalsIgnoreCase("sunrise")) {
                        env.setTime(Bukkit.getWorld(args[0]), UtilEnvironment.Time.SUNRISE);
                    } else {
                        try {
                            env.setTime(Bukkit.getWorld(args[0]), Long.parseLong(args[1]));
                        } catch (NumberFormatException e) {
                            msg.send("generic", "unknowntime", new String[]{args[1]});
                            msg.send("generic", "timehelp");
                            return true;
                        }
                    }
                    msg.send("environment", "timeset", new String[]{String.valueOf(env.getTime(Bukkit.getWorld(args[0]))), args[0]});
                } else {
                    return new UtilPermission(plugin,cs).not();
                }
            }
        }
        return true;
    }
}