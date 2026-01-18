package net.lewmc.essence.environment;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /lightning command.
 */
public class CommandLightning extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandWeather class.
     * @param plugin References to the main plugin class.
     */
    public CommandLightning(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.environment.lightning";
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
        UtilMessage msg = new UtilMessage(this.plugin, cs);
        if (args.length == 1) {
            UtilPermission up = new UtilPermission(this.plugin, cs);
            if (!up.has("essence.environment.lightning.other")) {
                return up.not();
            }

            Player target = this.plugin.getServer().getPlayer(args[0]);
            if (target != null) {
                target.getWorld().strikeLightningEffect(target.getLocation());
            } else {
                msg.send("generic", "playernotfound");
            }
        } else {
            if (!(cs instanceof Player p)) {
                msg.send("generic", "customerror", new String[]{"To run this command from the console, select a player (/lightning <player>)"});
                return false;
            }

            p.getWorld().strikeLightningEffect(p.getLocation());
        }
        return true;
    }
}