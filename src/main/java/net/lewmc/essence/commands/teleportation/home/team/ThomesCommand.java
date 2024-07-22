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
     * @param commandSender Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
        @NotNull CommandSender commandSender,
        @NotNull Command command,
        @NotNull String s,
        String[] args
    ) {
        if (!(commandSender instanceof Player)) {
            this.log.noConsole();
            return true;
        }
        MessageUtil message = new MessageUtil(commandSender, plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        TeamUtil tu = new TeamUtil(this.plugin, message);
        String team = tu.getPlayerTeam(player.getUniqueId());

        if (team == null) {
            message.send("team", "noteam");
            return true;
        }

        if (!tu.getRule(team, "allow-team-homes")) {
            message.send("team", "disallowedhomes");
            return true;
        }

        if (command.getName().equalsIgnoreCase("thomes")) {
            CommandUtil cmd = new CommandUtil(this.plugin);
            if (cmd.isDisabled("thomes")) {
                return cmd.disabled(message);
            }

            if (permission.has("essence.home.team.list")) {
                HomeUtil hu = new HomeUtil(this.plugin);
                StringBuilder setHomes = hu.getTeamHomesList(team);

                if (setHomes == null) {
                    message.send("teamhome", "noneset");
                    return true;
                }

                message.send("teamhome", "list", new String[] { setHomes.toString() });
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}