package net.lewmc.essence.environment;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /weather command.
 */
public class CommandWeather extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandWeather class.
     * @param plugin References to the main plugin class.
     */
    public CommandWeather(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.environment.weather";
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
        UtilEnvironment env = new UtilEnvironment(this.plugin);
        UtilMessage msg = new UtilMessage(plugin, cs);

        if (args.length == 0) {
            if (cs instanceof Player p) {
                // Player check weather
                msg.send("environment", "weather", new String[]{env.getWeather(p.getWorld()).toString(), p.getWorld().getName()});
            } else {
                // Console didn't specify a world
                msg.send("environment", "weatherconsoleusage");
            }
        } else if (args.length == 1) {
            if (cs instanceof Player p) {
                // Player set weather
                if (new UtilPermission(plugin,cs).has("essence.environment.weather.set")) {
                    if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("sun") || args[0].equalsIgnoreCase("sunny")) {
                        env.setWeather(p.getWorld(), UtilEnvironment.Weather.CLEAR);
                    } else if (args[0].equalsIgnoreCase("rain") || args[0].equalsIgnoreCase("raining") || args[0].equalsIgnoreCase("downpour")) {
                        env.setWeather(p.getWorld(), UtilEnvironment.Weather.RAIN);
                    } else if (args[0].equalsIgnoreCase("thunder") || args[0].equalsIgnoreCase("lightning") || args[0].equalsIgnoreCase("storm")) {
                        env.setWeather(p.getWorld(), UtilEnvironment.Weather.THUNDER);
                    } else {
                        msg.send("generic", "unknownweather", new String[]{args[0]});
                        return true;
                    }
                    msg.send("environment", "weatherset", new String[]{env.getWeather(p.getWorld()).toString(), p.getWorld().getName()});
                } else {
                    return new UtilPermission(plugin,cs).not();
                }
            } else {
                // Console check weather
                if (Bukkit.getWorld(args[0]) != null) {
                    msg.send("environment", "weather", new String[]{env.getWeather(Bukkit.getWorld(args[0])).toString(), args[0]});
                } else {
                    msg.send("generic", "worldnotfound", new String[]{args[0]});
                }
            }
        } else if (args.length == 2) {
            if (cs instanceof Player) {
                // Player too many args.
                msg.send("environment", "weatherplayerusage", new String[]{args[0]});
            } else {
                // Console set weather
                if (new UtilPermission(plugin, cs).has("essence.environment.weather.set")) {

                    if (Bukkit.getWorld(args[0]) == null) {
                        msg.send("generic", "worldnotfound", new String[]{args[0]});
                    }

                    if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("sun") || args[1].equalsIgnoreCase("sunny")) {
                        env.setWeather(Bukkit.getWorld(args[0]), UtilEnvironment.Weather.CLEAR);
                    } else if (args[1].equalsIgnoreCase("rain") || args[1].equalsIgnoreCase("raining") || args[1].equalsIgnoreCase("downpour")) {
                        env.setWeather(Bukkit.getWorld(args[0]), UtilEnvironment.Weather.RAIN);
                    } else if (args[1].equalsIgnoreCase("thunder") || args[1].equalsIgnoreCase("lightning") || args[1].equalsIgnoreCase("storm")) {
                        env.setWeather(Bukkit.getWorld(args[0]), UtilEnvironment.Weather.THUNDER);
                    } else {
                        msg.send("generic", "unknownweather", new String[]{args[1]});
                        return true;
                    }
                    msg.send("environment", "weatherset", new String[]{env.getWeather(Bukkit.getWorld(args[0])).toString(), args[0]});
                } else {
                    return new UtilPermission(plugin,cs).not();
                }
            }
        }
        return true;
    }
}