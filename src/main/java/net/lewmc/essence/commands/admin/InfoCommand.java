package net.lewmc.essence.commands.admin;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.CommandUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * /info command.
 */
public class InfoCommand extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the InfoCommand class.
     * @param plugin References to the main plugin class.
     */
    public InfoCommand(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.playerinfo.info";
    }

    /**
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        CommandUtil cmd = new CommandUtil(this.plugin, cs);
        if (cmd.isDisabled("info")) { return cmd.disabled(); }

        MessageUtil message = new MessageUtil(this.plugin, cs);
        if (args.length == 1) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            if (p.hasPlayedBefore()) {
                Files fu = new Files(this.plugin.config, this.plugin);
                if (fu.exists(fu.playerDataFile(p.getUniqueId()))) {
                    fu.load(fu.playerDataFile(p.getUniqueId()));
                    message.send("info", "uuid", new String[]{String.valueOf(p.getUniqueId())});
                    message.send("info", "lastname", new String[]{fu.getString("user.last-known-name")});
                    if (fu.getString("user.last-seen") != null) {
                        message.send("info", "lastseen", new String[]{fu.getString("user.last-seen")});
                    } else {
                        message.send("info", "neverseen");
                    }
                    fu.close();
                } else {
                    message.send("generic", "playernotfound");
                }
            } else {
                message.send("generic", "playernotfound");
            }
        } else {
            message.send("generic", "playernotfound");
        }
        return true;
    }
}