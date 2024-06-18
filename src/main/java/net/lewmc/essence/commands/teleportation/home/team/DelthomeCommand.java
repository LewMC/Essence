package net.lewmc.essence.commands.teleportation.home.team;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DelthomeCommand implements CommandExecutor {
    private final LogUtil log;
    private final Essence plugin;

    /**
     * Constructor for the DelthomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public DelthomeCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
    }

    /**
     * @param commandSender Information about who sent the command - player or console.
     * @param command       Information about what command was sent.
     * @param s             Command label - not used here.
     * @param args          The command's arguments.
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
        MessageUtil message = new MessageUtil(commandSender,plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        TeamUtil tu = new TeamUtil(this.plugin, message);
        String team = tu.getPlayerTeam(player.getUniqueId());

        if (team == null) {
            message.PrivateMessage("team", "noteam");
            return true;
        }

        if (!tu.getRule(team, "allow-team-homes")) {
            message.PrivateMessage("team", "disallowedhomes");
            return true;
        }

        if (command.getName().equalsIgnoreCase("delthome")) {
            if (permission.has("essence.home.team.delete")) {
                String name;
                if (args.length == 0) {
                    name = "home";
                } else {
                    name = args[0];
                }

                FileUtil dataUtil = new FileUtil(this.plugin);
                dataUtil.load("/data/teams/"+team+".yml");

                String homeName = name.toLowerCase();

                if (dataUtil.get("homes."+homeName) == null) {
                    dataUtil.close();
                    message.PrivateMessage("home", "notfound", name);
                    return true;
                }

                if (dataUtil.remove("homes."+homeName)) {
                    message.PrivateMessage("home", "deleted", homeName);
                } else {
                    message.PrivateMessage("generic", "exception");
                }

                dataUtil.save();
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}
