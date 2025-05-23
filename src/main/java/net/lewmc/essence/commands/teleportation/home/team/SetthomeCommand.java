package net.lewmc.essence.commands.teleportation.home.team;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetthomeCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the SetthomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public SetthomeCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
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
        if (command.getName().equalsIgnoreCase("setthome")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("setthome")) { return cmd.disabled(); }

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

            if (permission.has("essence.home.team.create")) {

                String name;
                if (args.length == 0) {
                    name = "home";
                } else {
                    name = args[0];
                }

                Location loc = p.getLocation();
                FileUtil dataUtil = new FileUtil(this.plugin);
                dataUtil.load("data/teams/"+team+".yml");

                SecurityUtil securityUtil = new SecurityUtil();
                if (securityUtil.hasSpecialCharacters(name.toLowerCase())) {
                    dataUtil.close();
                    message.send("teamhome", "specialchars");
                    return true;
                }

                String homeName = "homes." + name.toLowerCase();

                if (dataUtil.get(homeName) != null) {
                    dataUtil.close();
                    message.send("teamhome", "alreadyexists");
                    return true;
                }

                HomeUtil hu = new HomeUtil(this.plugin);
                int homeLimit = permission.getTeamHomesLimit(p);
                if (hu.getTeamHomeCount(p) >= homeLimit && homeLimit != -1) {
                    message.send("teamhome", "hitlimit");
                    return true;
                }

                dataUtil.set(homeName + ".creator", p.getUniqueId().toString());
                dataUtil.set(homeName + ".world", loc.getWorld().getName());
                dataUtil.set(homeName + ".X", loc.getX());
                dataUtil.set(homeName + ".Y", loc.getY());
                dataUtil.set(homeName + ".Z", loc.getZ());
                dataUtil.set(homeName + ".yaw", loc.getYaw());
                dataUtil.set(homeName + ".pitch", loc.getPitch());

                // Save the configuration to the file
                dataUtil.save();

                message.send("teamhome", "created", new String[] { name });
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}