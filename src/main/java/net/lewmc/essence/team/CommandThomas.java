package net.lewmc.essence.team;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.teleportation.home.UtilHome;
import net.lewmc.foundry.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandThomas implements CommandExecutor {
    private final Essence plugin;
    private final Logger log;

    /**
     * Constructor for the ThomesCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandThomas(Essence plugin) {
        this.plugin = plugin;
        this.log = new Logger(plugin.config);
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
        if (command.getName().equalsIgnoreCase("thomes")) {
            UtilCommand cmd = new UtilCommand(this.plugin, cs);
            if (cmd.isDisabled("thomes")) { return cmd.disabled(); }

            if (!(cs instanceof Player p)) { return this.log.noConsole(); }

            UtilMessage msg = new UtilMessage(this.plugin, cs);
            UtilPermission perms = new UtilPermission(this.plugin, cs);

            UtilTeam tu = new UtilTeam(this.plugin, msg);
            String team = tu.getPlayerTeam(p.getUniqueId());

            if (team == null) {
                msg.send("team", "noteam");
                return true;
            }

            if (!tu.getRule(team, "allow-team-homes")) {
                msg.send("team", "disallowedhomes");
                return true;
            }

            if (perms.has("essence.home.team.list")) {
                UtilHome hu = new UtilHome(this.plugin);
                StringBuilder setHomes = hu.getTeamHomesList(team);

                if (setHomes == null) {
                    msg.send("teamhome", "noneset");
                    return true;
                }

                msg.send("teamhome", "list", new String[] { setHomes.toString() });
            } else {
                perms.not();
            }
            return true;
        }

        return false;
    }
}