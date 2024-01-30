package net.lewmc.essence.commands;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.PermissionHandler;
import net.lewmc.essence.utils.TeamUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamCommands implements CommandExecutor {
    private final Essence plugin;
    /**
     * Constructor for the GamemodeCommands class.
     *
     * @param plugin References to the main plugin class.
     */
    public TeamCommands(Essence plugin) {
        this.plugin = plugin;
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
            LogUtil log = new LogUtil(this.plugin);
            log.noConsole();
        }
        MessageUtil message = new MessageUtil(commandSender, plugin);
        PermissionHandler permission = new PermissionHandler(commandSender, message);
        Player player = (Player) commandSender;

        if (command.getName().equalsIgnoreCase("team")) {
            if (args.length > 1) {
                TeamUtil team = new TeamUtil(this.plugin, message);
                if (args[0].equalsIgnoreCase("create")) {
                    if (permission.has("essence.team.create")) {
                        if (args.length == 2) {
                            team.CreateNewTeam(args[1], player.getUniqueId());
                        } else {
                            message.PrivateMessage("team", "namerequired");
                        }
                    } else {
                        permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("join")) {
                    if (permission.has("essence.team.join")) {
                        if (args.length == 2) {
                            String playerTeam = team.getPlayerTeam(player.getUniqueId());
                            if (playerTeam == null) {
                                team.requestJoin(args[1], player.getUniqueId());
                            } else {
                                message.PrivateMessage("team", "alreadyinteam", playerTeam);
                            }
                        } else {
                            message.PrivateMessage("team", "namerequired");
                        }
                    } else {
                        permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("requests")) {
                    if (permission.has("essence.team.manage")) {
                        String playerTeam = team.getPlayerTeam(player.getUniqueId());
                        if (playerTeam != null) {
                            if (team.isLeader(playerTeam, player.getUniqueId())) {
                                message.PrivateMessage("team", "teamrequests", playerTeam, "Test");
                                message.PrivateMessage("team", "howtoaccept");
                                message.PrivateMessage("team", "howtodecline");
                            } else {
                                message.PrivateMessage("team", "leaderrequired");
                            }
                        } else {
                            message.PrivateMessage("team", "noteam");
                        }
                    } else {
                        permission.not();
                    }
                } else {
                    message.PrivateMessage("team", "malformed");
                }
            } else {
                message.PrivateMessage("team", "malformed");
            }
        }
        return true;
    }
}
