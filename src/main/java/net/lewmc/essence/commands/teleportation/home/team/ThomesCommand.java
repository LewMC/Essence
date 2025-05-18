package net.lewmc.essence.commands.teleportation.home.team;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ThomesCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the ThomesCommand class.
     * @param plugin References to the main plugin class.
     */
    public ThomesCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
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
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("thomes")) { return cmd.disabled(); }

            if (!(cs instanceof Player p)) { return this.log.noConsole(); }

            MessageUtil msg = new MessageUtil(this.plugin, cs);
            PermissionHandler perms = new PermissionHandler(this.plugin, cs);

            TeamUtil tu = new TeamUtil(this.plugin, msg);
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
                HomeUtil hu = new HomeUtil(this.plugin);
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