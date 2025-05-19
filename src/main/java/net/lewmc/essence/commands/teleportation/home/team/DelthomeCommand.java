package net.lewmc.essence.commands.teleportation.home.team;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import net.lewmc.foundry.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DelthomeCommand implements CommandExecutor {
    private final Logger log;
    private final Essence plugin;

    /**
     * Constructor for the DelthomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public DelthomeCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new Logger(plugin.config);
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
        if (command.getName().equalsIgnoreCase("delthome")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("delthome")) { return cmd.disabled(); }

            if (!(cs instanceof Player p)) { return this.log.noConsole(); }

            MessageUtil message = new MessageUtil(this.plugin, cs);
            PermissionHandler permission = new PermissionHandler(this.plugin, cs);

            TeamUtil tu = new TeamUtil(this.plugin, message);
            String team = tu.getPlayerTeam(p.getUniqueId());

            if (team == null) {
                message.send("team", "noteam");
                return true;
            }

            if (!tu.getRule(team, "allow-team-homes")) {
                message.send("team", "disallowedhomes");
                return true;
            }

            if (permission.has("essence.home.team.delete")) {
                String name  = "home";
                if (args.length != 0) {
                    name = args[0];
                }

                FileUtil dataUtil = new FileUtil(this.plugin);
                dataUtil.load("data/teams/"+team+".yml");

                String homeName = name.toLowerCase();

                if (dataUtil.get("homes."+homeName) == null) {
                    dataUtil.close();
                    message.send("teamhome", "notfound", new String[] { name });
                    return true;
                }

                if (dataUtil.remove("homes."+homeName)) {
                    message.send("teamhome", "deleted", new String[] { homeName });
                } else {
                    message.send("generic", "exception");
                }

                dataUtil.save();
            } else {
                return permission.not();
            }
            return true;
        }

        return false;
    }
}
