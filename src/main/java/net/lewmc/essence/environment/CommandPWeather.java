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
 * /pweather command.
 */
public class CommandPWeather extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandPWeather class.
     * @param plugin References to the main plugin class.
     */
    public CommandPWeather(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.environment.pweather";
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
        if (cmd.isDisabled("pweather")) {
            return cmd.disabled();
        }

        Player p = (Player) cs;

        UtilEnvironment env = new UtilEnvironment();
        UtilMessage msg = new UtilMessage(plugin, cs);

        if (args.length == 0) {
            msg.send("environment", "playerweather", new String[]{env.getPlayerWeather(p).toString()});
        } else if (args.length == 1) {
            if (new UtilPermission(plugin,cs).has("essence.environment.pweather.set")) {
                if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("sun") || args[0].equalsIgnoreCase("sunny")) {
                    env.setPlayerWeather(p, UtilEnvironment.Weather.CLEAR);
                } else if (args[0].equalsIgnoreCase("rain") || args[0].equalsIgnoreCase("raining") || args[0].equalsIgnoreCase("downpour")) {
                    env.setPlayerWeather(p, UtilEnvironment.Weather.RAIN);
                } else if (args[0].equalsIgnoreCase("thunder") || args[0].equalsIgnoreCase("lightning") || args[0].equalsIgnoreCase("storm")) {
                    env.setPlayerWeather(p, UtilEnvironment.Weather.THUNDER);
                } else if (args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("off")) {
                    env.setPlayerWeather(p, UtilEnvironment.Weather.RESET);
                    msg.send("environment", "playerweatherreset");
                    return true;
                } else {
                    msg.send("generic", "unknownweather", new String[]{args[0]});
                    return true;
                }
                msg.send("environment", "playerweatherset", new String[]{env.getPlayerWeather(p).toString()});
            } else {
                new UtilPermission(plugin, cs).not();
            }
        }

        return true;
    }
}