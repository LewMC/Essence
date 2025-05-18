package net.lewmc.essence.commands.admin;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * /seen command.
 */
public class SeenCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the SeenCommand class.
     * @param plugin References to the main plugin class.
     */
    public SeenCommand(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
            @NotNull CommandSender cs,
            @NotNull Command command,
            @NotNull String s,
            String[] args
    ) {
        if (command.getName().equalsIgnoreCase("seen")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("seen")) { return cmd.disabled(); }

            PermissionHandler permission = new PermissionHandler(this.plugin, cs);

            if (permission.has("essence.playerinfo.seen")) {
                MessageUtil msg = new MessageUtil(this.plugin, cs);
                if (args.length == 1) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                    if (p.hasPlayedBefore()) {
                        FileUtil fu = new FileUtil(this.plugin);
                        if (fu.exists(fu.playerDataFile(p.getUniqueId()))) {
                            fu.load(fu.playerDataFile(p.getUniqueId()));
                            if (fu.getString("user.last-seen") != null) {
                                msg.send("seen", "lastseen", new String[]{p.getName(), fu.getString("user.last-seen")});
                            } else {
                                msg.send("seen", "neverseen", new String[]{p.getName()});
                            }
                            fu.close();
                        } else {
                            msg.send("generic","playernotfound");
                        }
                    } else {
                        msg.send("generic","playernotfound");
                    }
                } else {
                    msg.send("generic","playernotfound");
                }
                return true;
            } else {
                return permission.not();
            }
        }

        return false;
    }
}