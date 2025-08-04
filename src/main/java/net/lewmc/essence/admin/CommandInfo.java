package net.lewmc.essence.admin;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Permissions;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * /info command.
 */
public class CommandInfo extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the InfoCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandInfo(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.admin.info";
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
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("info")) { return cmd.disabled(); }

        UtilMessage message = new UtilMessage(this.plugin, cs);
        if (args.length == 1) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            if (p.hasPlayedBefore()) {
                Files fu = new Files(this.plugin.config, this.plugin);
                if (fu.exists(fu.playerDataFile(p.getUniqueId()))) {
                    fu.load(fu.playerDataFile(p.getUniqueId()));

                    message.send("info", "uuid", new String[]{String.valueOf(p.getUniqueId())});

                    if (fu.exists("user.nickname")) {
                        message.send("info", "lastname", new String[]{fu.getString("user.last-known-name")});
                    } else {
                        message.send("info", "lastname", new String[]{"Unknown"});
                    }

                    if (fu.exists("user.nickname")) {
                        message.send("info", "nickname", new String[]{fu.getString("user.nickname")});
                    } else {
                        message.send("info", "nickname", new String[]{"None"});
                    }

                    if (fu.exists("user.last-seen")) {
                        message.send("info", "lastseen", new String[]{fu.getString("user.last-seen")});
                    } else {
                        message.send("info", "neverseen");
                    }

                    if (new Permissions(cs).has("essence.admin.info.viewip")) {
                        if (fu.exists("user.ip-address")) {
                            message.send("info", "ip", new String[]{fu.getString("user.ip-address")});
                        } else {
                            message.send("info", "ip", new String[]{"Unknown"});
                        }
                    } else {
                        message.send("info", "noip");
                    }

                    if (fu.exists("user.team")) {
                        message.send("info", "team", new String[]{fu.getString("user.team")});
                    } else {
                        message.send("info", "team", new String[]{"None"});
                    }
                    fu.close();

                    if (this.plugin.flyingPlayers.contains(p.getUniqueId())) {
                        message.send("info", "canfly", new String[]{ "Yes" });
                    } else {
                        message.send("info", "canfly", new String[]{ "No" });
                    }
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