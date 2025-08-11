package net.lewmc.essence.team;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.teleportation.home.UtilHome;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandThomes extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the ThomesCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandThomes(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.home.team.list";
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

        UtilHome hu = new UtilHome(this.plugin);
        StringBuilder setHomes = hu.getTeamHomesList(team);

        if (setHomes == null) {
            msg.send("teamhome", "noneset");
            return true;
        }

        msg.send("teamhome", "list", new String[]{setHomes.toString()});
        return true;
    }
}