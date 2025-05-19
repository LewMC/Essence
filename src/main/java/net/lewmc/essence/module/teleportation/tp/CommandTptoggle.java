package net.lewmc.essence.module.teleportation.tp;

import net.lewmc.essence.Essence;
import net.lewmc.essence.global.UtilCommand;
import net.lewmc.essence.global.UtilMessage;
import net.lewmc.essence.global.UtilPermission;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * /tpaccept command.
 */
public class CommandTptoggle implements CommandExecutor {
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
     * @param cs            Information about who sent the command - player or console.
     * @param command       Information about what command was sent.
     * @param s             Command label - not used here.
     * @param args          The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
            @NotNull CommandSender cs,
            @NotNull Command command,
            @NotNull String s,
            String[] args
    ) {
        if (command.getName().equalsIgnoreCase("tptoggle")) {

            UtilCommand cmd = new UtilCommand(this.plugin, cs);
            if (cmd.isDisabled("tptoggle")) { return cmd.disabled(); }
            if (cmd.console(cs)) { return new Logger(this.plugin.config).noConsole(); }

            UtilPermission perms = new UtilPermission(this.plugin, cs);

            if (perms.has("essence.teleport.request.toggle")) {
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
            } else {
                return perms.not();
            }
        }
        return false;
    }
}