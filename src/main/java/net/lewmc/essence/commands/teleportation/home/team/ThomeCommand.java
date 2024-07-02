package net.lewmc.essence.commands.teleportation.home.team;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ThomeCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the ThomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public ThomeCommand(Essence plugin) {
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
        MessageUtil message = new MessageUtil(commandSender, plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);
        TeleportUtil teleUtil = new TeleportUtil(this.plugin);

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

        if (command.getName().equalsIgnoreCase("thome")) {
            CommandUtil cmd = new CommandUtil(this.plugin);
            if (cmd.isDisabled("thome")) {
                return cmd.disabled();
            }

            if (permission.has("essence.home.team.use")) {

                int waitTime = plugin.getConfig().getInt("teleportation.home.wait");
                if (!teleUtil.cooldownSurpassed(player, "home")) {
                    message.send("teleport", "tryagain", new String[] { String.valueOf(teleUtil.cooldownRemaining(player, "home")) });
                    return true;
                }

                FileUtil dataUtil = new FileUtil(this.plugin);
                dataUtil.load("data/teams/"+team+".yml");

                String homeName;
                String chatHomeName;

                if (args.length == 1) {
                    homeName = "homes." + args[0].toLowerCase();
                    chatHomeName = args[0].toLowerCase();
                    if (dataUtil.get(homeName) == null) {
                        dataUtil.close();
                        message.send("teamhome", "notfound", new String[] { args[0].toLowerCase() });
                        return true;
                    }
                } else {
                    homeName = "homes.home";
                    chatHomeName = "home";
                    if (dataUtil.get(homeName) == null) {
                        if (permission.has("essence.home.team.list")) {
                            dataUtil.close();

                            HomeUtil hu = new HomeUtil(this.plugin);
                            StringBuilder setHomes = hu.getTeamHomesList(team);

                            if (setHomes == null) {
                                message.send("teamhome", "noneset");
                                return true;
                            }

                            message.send("teamhome", "list", new String[] { setHomes.toString() });
                            return true;
                        } else {
                            message.send("teamhome", "notfound", new String[] { "home" });
                        }
                    }
                }

                if (dataUtil.get(homeName) == null) {
                    dataUtil.close();
                    message.send("generic", "exception");
                    this.log.warn("Player " + player + " attempted to teleport home to " + chatHomeName + " but couldn't due to an error.");
                    this.log.warn("Error: Unable to load from configuration file, please check configuration file.");
                    return true;
                }

                if (dataUtil.getString(homeName + ".world") == null) {
                    dataUtil.close();
                    message.send("generic", "exception");
                    this.log.warn("Player " + player + " attempted to teleport home to " + chatHomeName + " but couldn't due to an error.");
                    this.log.warn("Error: world is null, please check configuration file.");
                    return true;
                }

                LocationUtil locationUtil = new LocationUtil(this.plugin);
                locationUtil.UpdateLastLocation(player);

                teleUtil.setCooldown(player, "home");

                if (Bukkit.getServer().getWorld(dataUtil.getString(homeName + ".world")) == null) {
                    WorldCreator creator = new WorldCreator(dataUtil.getString(homeName + ".world"));
                    creator.createWorld();
                }

                teleUtil.doTeleport(
                        player,
                        Bukkit.getServer().getWorld(Objects.requireNonNull(dataUtil.getString(homeName + ".world"))),
                        dataUtil.getDouble(homeName + ".X"),
                        dataUtil.getDouble(homeName + ".Y"),
                        dataUtil.getDouble(homeName + ".Z"),
                        (float) dataUtil.getDouble(homeName + ".yaw"),
                        (float) dataUtil.getDouble(homeName + ".pitch"),
                        waitTime
                );
                dataUtil.close();

                message.send("teamhome", "teleporting", new String[] { chatHomeName, waitTime + "" });
                return true;
            } else {
                return permission.not();
            }
        }
        return false;
    }
}