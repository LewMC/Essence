package net.lewmc.essence.chat;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPlayer;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * /ignore command
 */
public class CommandIgnore extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandIgnore class.
     * @param plugin References to the main plugin class.
     */
    public CommandIgnore(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.chat.ignore";
    }

    /**
     * /ignore command handler.
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage message = new UtilMessage(plugin, cs);
        Player sender = (Player) cs;
        UtilPlayer up = new UtilPlayer(this.plugin);

        if (args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if ((p.getName().toLowerCase()).equalsIgnoreCase(args[0])) {
                    List<String> ignoring = (List<String>) up.getPlayer(sender.getUniqueId(), UtilPlayer.KEYS.USER_IGNORING_PLAYERS);
                    if ((Boolean) up.playerIsIgnoring(sender.getUniqueId(), p.getUniqueId())) {
                        message.send("ignore","unignored", new String[]{p.getName()});
                        ignoring.remove(p.getUniqueId().toString());
                    } else {
                        message.send("ignore","ignored", new String[]{p.getName()});
                        ignoring.add(p.getUniqueId().toString());
                    }
                    up.setPlayer(sender.getUniqueId(), UtilPlayer.KEYS.USER_IGNORING_PLAYERS, ignoring);

                    return true;
                }
            }
            message.send("generic", "playernotfound");
        } if (args.length == 0) {
            List<String> ignoring = (List<String>) up.getPlayer(sender.getUniqueId(), UtilPlayer.KEYS.USER_IGNORING_PLAYERS);
            if (ignoring.isEmpty()) {
                message.send("ignore", "ignoringnone");
            } else {
                message.send("ignore", "ignoring", new String[]{ String.join(",", ignoring) });
            }
        } else {
            message.send("ignore","usage");
        }

        return true;
    }
}
