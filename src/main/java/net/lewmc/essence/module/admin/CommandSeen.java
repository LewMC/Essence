package net.lewmc.essence.module.admin;

import net.lewmc.essence.Essence;
import net.lewmc.essence.global.UtilCommand;
import net.lewmc.essence.global.UtilMessage;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * /seen command.
 */
public class CommandSeen extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the SeenCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandSeen(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.playerinfo.seen";
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
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("seen")) { return cmd.disabled(); }

        UtilMessage msg = new UtilMessage(this.plugin, cs);
        if (args.length == 1) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            if (p.hasPlayedBefore()) {
                Files fu = new Files(this.plugin.config, this.plugin);
                if (fu.exists(fu.playerDataFile(p.getUniqueId()))) {
                    fu.load(fu.playerDataFile(p.getUniqueId()));
                    if (fu.getString("user.last-seen") != null) {
                        msg.send("seen", "lastseen", new String[]{p.getName(), fu.getString("user.last-seen")});
                    } else {
                        msg.send("seen", "neverseen", new String[]{p.getName()});
                    }
                    fu.close();
                } else {
                    msg.send("generic", "playernotfound");
                }
            } else {
                msg.send("generic", "playernotfound");
            }
        } else {
            msg.send("generic", "playernotfound");
        }
        return true;
    }
}