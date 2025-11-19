package net.lewmc.essence.inventory;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPlayer;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /confirmclear command
 */
public class CommandConfirmClear extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandConfirmClear class.
     * @param plugin References to the main plugin class.
     */
    public CommandConfirmClear(Essence plugin) {
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
     * /confirmclear command handler.
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage msg = new UtilMessage(plugin, cs);
        Player sender = (Player) cs;
        UtilPlayer up = new UtilPlayer(this.plugin);

        if (!(Boolean)up.getPlayer(sender.getUniqueId(), UtilPlayer.KEYS.USER_CONFIRM_CLEAR)) {
            if (up.setPlayer(sender.getUniqueId(), UtilPlayer.KEYS.USER_CONFIRM_CLEAR, true)) {
                msg.send("clear","confirmon");
            } else {
                msg.send("clear","confirmcant");
            }
        } else {
            if (up.setPlayer(sender.getUniqueId(), UtilPlayer.KEYS.USER_CONFIRM_CLEAR, false)) {
                msg.send("clear","confirmoff");
            } else {
                msg.send("clear","confirmcant");
            }
        }

        return true;
    }
}
