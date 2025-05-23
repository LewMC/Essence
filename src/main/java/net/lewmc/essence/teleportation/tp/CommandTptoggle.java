package net.lewmc.essence.teleportation.tp;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /tptoggle command.
 */
public class CommandTptoggle extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the TptoggleCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandTptoggle(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.teleport.request.toggle";
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
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("tptoggle")) { return cmd.disabled(); }

        Files file = new Files(this.plugin.config, this.plugin);

        Player p = (Player) cs;
        file.load(file.playerDataFile(p.getUniqueId()));

        UtilMessage msg = new UtilMessage(this.plugin, cs);

        if (file.getBoolean("user.accepting-teleport-requests")) {
            file.set("user.accepting-teleport-requests", false);
            msg.send("teleport", "toggled", new String[] { "disabled" });
        } else {
            file.set("user.accepting-teleport-requests", true);
            msg.send("teleport", "toggled", new String[] { "enabled" });
        }

        file.save();
        return true;
    }
}