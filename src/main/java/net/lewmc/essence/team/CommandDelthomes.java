package net.lewmc.essence.team;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /delthomes command.
 */
public class CommandDelthomes extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the DelthomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandDelthomes(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     * @return String - The permission string
     */
    @Override
    protected String requiredPermission() {
        return "essence.home.team.delete";
    }

    /**
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean  true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("delthome")) { return cmd.disabled(); }

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

        String name = "home";
        if (args.length != 0) {
            name = args[0];
        }

        Files dataUtil = new Files(this.plugin.config, this.plugin);
        dataUtil.load("data/teams/" + team + ".yml");

        String homeName = name.toLowerCase();

        if (dataUtil.get("homes." + homeName) == null) {
            dataUtil.close();
            msg.send("teamhome", "notfound", new String[]{name});
            return true;
        }

        if (dataUtil.remove("homes." + homeName)) {
            msg.send("teamhome", "deleted", new String[]{homeName});
        } else {
            msg.send("generic", "exception");
        }

        dataUtil.save();

        return true;
    }
}
