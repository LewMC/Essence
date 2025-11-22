package net.lewmc.essence.team;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.teleportation.home.UtilHome;
import net.lewmc.essence.teleportation.tp.UtilTeleport;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import net.lewmc.foundry.Permissions;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandThome extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the ThomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandThome(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.home.team.use";
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
        Logger log = new Logger(this.plugin.foundryConfig);

        UtilMessage message = new UtilMessage(this.plugin, cs);
        UtilTeleport teleUtil = new UtilTeleport(this.plugin);

        UtilTeam tu = new UtilTeam(this.plugin, message);
        String team = tu.getPlayerTeam(p.getUniqueId());

        if (team == null) {
            message.send("team", "noteam");
            return true;
        }

        if (!tu.getRule(team, "allow-team-homes")) {
            message.send("team", "disallowedhomes");
            return true;
        }

        int waitTime = (int) plugin.config.get("teleportation.home.wait");
        if (!teleUtil.cooldownSurpassed(p, "home")) {
            message.send("teleport", "tryagain", new String[]{String.valueOf(teleUtil.cooldownRemaining(p, "home"))});
            return true;
        }

        Files dataUtil = new Files(this.plugin.foundryConfig, this.plugin);
        dataUtil.load("data/teams/" + team + ".yml");

        String homeName;
        String chatHomeName;

        if (args.length == 1) {
            homeName = "homes." + args[0].toLowerCase();
            chatHomeName = args[0].toLowerCase();
            if (dataUtil.get(homeName) == null) {
                dataUtil.close();
                message.send("teamhome", "notfound", new String[]{args[0].toLowerCase()});
                return true;
            }
        } else {
            homeName = "homes.home";
            chatHomeName = "home";
            if (dataUtil.get(homeName) == null) {
                if (new Permissions(cs).has("essence.home.team.list")) {
                    dataUtil.close();

                    UtilHome hu = new UtilHome(this.plugin);
                    StringBuilder setHomes = hu.getTeamHomesList(team);

                    if (setHomes == null) {
                        message.send("teamhome", "noneset");
                        return true;
                    }

                    message.send("teamhome", "list", new String[]{setHomes.toString()});
                    return true;
                } else {
                    message.send("teamhome", "notfound", new String[]{"home"});
                }
            }
        }

        if (dataUtil.get(homeName) == null) {
            dataUtil.close();
            message.send("generic", "exception");
            log.warn("Player " + p + " attempted to teleport home to " + chatHomeName + " but couldn't due to an error.");
            log.warn("Error: Unable to load from configuration file, please check configuration file.");
            return true;
        }

        if (dataUtil.getString(homeName + ".world") == null) {
            dataUtil.close();
            message.send("generic", "exception");
            log.warn("Player " + p + " attempted to teleport home to " + chatHomeName + " but couldn't due to an error.");
            log.warn("Error: world is null, please check configuration file.");
            return true;
        }

        teleUtil.setCooldown(p, "home");

        World world = Bukkit.getServer().getWorld(dataUtil.getString(homeName + ".world"));
        
        if (world == null) {
            message.send("generic", "exception");
            log.warn("Player " + p + " attempted to teleport home to " + chatHomeName + " but couldn't due to an error.");
            log.warn("Error: world is null, please check configuration file.");
        }

        teleUtil.doTeleport(
                p,
                world,
                dataUtil.getDouble(homeName + ".X"),
                dataUtil.getDouble(homeName + ".Y"),
                dataUtil.getDouble(homeName + ".Z"),
                (float) dataUtil.getDouble(homeName + ".yaw"),
                (float) dataUtil.getDouble(homeName + ".pitch"),
                waitTime,
                true
        );
        dataUtil.close();

        if (waitTime > 0) {
            message.send("warp", "teleportingin", new String[]{chatHomeName, waitTime + ""});
        } else {
            message.send("warp", "teleportingnow", new String[]{chatHomeName});
        }
        return true;
    }
}