package net.lewmc.essence.team;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.teleportation.home.UtilHome;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Security;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetthome extends FoundryPlayerCommand {
    private final Essence plugin;
    /**
     * Constructor for the SetthomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandSetthome(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     *
     * @return String - The permission string
     */
    @Override
    protected String requiredPermission() {
        return "essence.home.team.create";
    }

    /**
     * @param cs      Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s       Command label - not used here.
     * @param args    The command's arguments.
     * @return boolean  true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("setthome")) {
            return cmd.disabled();
        }

        Player p = (Player) cs;

        UtilMessage msg = new UtilMessage(this.plugin, cs);

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
        String name;
        if (args.length == 0) {
            name = "home";
        } else {
            name = args[0];
        }

        Location loc = p.getLocation();
        Files dataUtil = new Files(this.plugin.config, this.plugin);
        dataUtil.load("data/teams/" + team + ".yml");

        if (new Security(this.plugin.config).hasSpecialCharacters(name.toLowerCase())) {
            dataUtil.close();
            msg.send("teamhome", "specialchars");
            return true;
        }

        String homeName = "homes." + name.toLowerCase();

        if (dataUtil.get(homeName) != null) {
            dataUtil.close();
            msg.send("teamhome", "alreadyexists");
            return true;
        }

        UtilHome hu = new UtilHome(this.plugin);
        int homeLimit = new UtilPermission(this.plugin, cs).getTeamHomesLimit(p);
        if (hu.getTeamHomeCount(p) >= homeLimit && homeLimit != -1) {
            msg.send("teamhome", "hitlimit");
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

        msg.send("teamhome", "created", new String[]{name});

        return true;
    }
}