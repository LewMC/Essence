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

/**
 * Team command class.
 */
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
            if (args.length > 0) {
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
                                message.PrivateMessage("team", "teamrequests", playerTeam, team.requestsToJoin(playerTeam));
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
                } else if (args[0].equalsIgnoreCase("accept")) {
                    if (permission.has("essence.team.manage")) {
                        if (args.length > 1) {
                            String playerTeam = team.getPlayerTeam(player.getUniqueId());
                            if (!team.hasRequested(playerTeam, args[1])) {
                                message.PrivateMessage("team", "usernotrequested", args[1], playerTeam);
                                return true;
                            }
                            if (team.isLeader(playerTeam, player.getUniqueId())) {
                                if (team.acceptRequest(playerTeam, args[1])) {
                                    message.PrivateMessage("team", "accepted", args[1]);
                                } else {
                                    message.PrivateMessage("generic", "exception");
                                }
                            } else {
                                message.PrivateMessage("team", "leaderrequired");
                            }
                        } else {
                            message.PrivateMessage("team", "usernamerequired");
                        }
                    } else {
                        permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("decline")) {
                    if (permission.has("essence.team.manage")) {
                        if (args.length > 1) {
                            String playerTeam = team.getPlayerTeam(player.getUniqueId());
                            if (!team.hasRequested(playerTeam, args[1])) {
                                message.PrivateMessage("team", "usernotrequested", args[1], playerTeam);
                                return true;
                            }
                            if (team.isLeader(playerTeam, player.getUniqueId())) {
                                if (team.declineRequest(playerTeam, args[1])) {
                                    message.PrivateMessage("team", "declined", args[1]);
                                } else {
                                    message.PrivateMessage("generic", "exception");
                                }
                            } else {
                                message.PrivateMessage("team", "leaderrequired");
                            }
                        } else {
                            message.PrivateMessage("team", "usernamerequired");
                        }
                    } else {
                        permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("leave")) {
                    if (permission.has("essence.team.join")) {
                        String playerTeam = team.getPlayerTeam(player.getUniqueId());
                        if (playerTeam != null) {
                            if (team.isLeader(playerTeam, player.getUniqueId())) {
                                message.PrivateMessage("team", "requiresdisband", playerTeam);
                                return true;
                            }

                            if (team.leave(playerTeam, player.getUniqueId())) {
                                message.PrivateMessage("team", "left", playerTeam);
                            } else {
                                message.PrivateMessage("generic", "exception");
                            }
                        } else {
                            message.PrivateMessage("team", "noteam");
                        }
                    } else {
                        permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("changeleader")) {
                    if (permission.has("essence.team.manage")) {
                        String playerTeam = team.getPlayerTeam(player.getUniqueId());
                        if (args.length <= 1) {
                            message.PrivateMessage("team", "leadernamerequired");
                            return true;
                        }
                        if (playerTeam != null) {
                            if (!team.isMember(playerTeam, args[1])) {
                                message.PrivateMessage("team", "usernotmember", args[1], playerTeam);
                                return true;
                            }

                            if (team.isLeader(playerTeam, player.getUniqueId())) {
                                if (team.changeLeader(playerTeam, args[1], String.valueOf(player.getUniqueId()))) {
                                    message.PrivateMessage("team", "leadertransfer", args[1], playerTeam);
                                } else {
                                    message.PrivateMessage("general", "exception");
                                }
                            } else {
                                message.PrivateMessage("team", "leaderrequired");
                                return true;
                            }
                        } else {
                            message.PrivateMessage("team", "noteam");
                        }
                    } else {
                        permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("kick")) {
                    if (permission.has("essence.team.manage")) {
                        String playerTeam = team.getPlayerTeam(player.getUniqueId());
                        if (args.length <= 1) {
                            message.PrivateMessage("team", "usernamerequired");
                            return true;
                        }
                        if (playerTeam != null) {
                            if (team.isLeader(playerTeam, player.getUniqueId())) {
                                if (!team.isMember(playerTeam, args[1])) {
                                    message.PrivateMessage("team", "usernotmember", args[1], playerTeam);
                                    return true;
                                }
                                if (team.kick(playerTeam, args[1])) {
                                    message.PrivateMessage("team", "kicked", args[1], playerTeam);
                                } else {
                                    message.PrivateMessage("generic", "exception");
                                }
                            } else {
                                message.PrivateMessage("team", "leaderrequired");
                                return true;
                            }
                        } else {
                            message.PrivateMessage("team", "noteam");
                        }
                    } else {
                        permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("disband")) {
                    if (permission.has("essence.team.manage")) {
                        String playerTeam = team.getPlayerTeam(player.getUniqueId());
                        if (playerTeam != null) {
                            if (team.isLeader(playerTeam, player.getUniqueId())) {
                                if (team.disband(playerTeam, player.getUniqueId().toString())) {
                                    message.PrivateMessage("team", "disbanded", playerTeam);
                                } else {
                                    message.PrivateMessage("generic", "exception");
                                }
                            } else {
                                message.PrivateMessage("team", "leaderrequired");
                                return true;
                            }
                        } else {
                            message.PrivateMessage("team", "noteam");
                        }
                    } else {
                        permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("rule")) {
                    if (permission.has("essence.team.manage")) {
                        String playerTeam = team.getPlayerTeam(player.getUniqueId());
                        if (playerTeam != null) {
                            if (team.isLeader(playerTeam, player.getUniqueId())) {
                                if (args.length > 1 && args[1].equalsIgnoreCase("allow-friendly-fire")) {
                                    if (args.length > 2) {
                                        if (team.setRule(playerTeam, args[1], args[2])) {
                                            message.PrivateMessage("team", "rulechanged", args[1], args[2]);
                                        } else {
                                            message.PrivateMessage("team", "cantchangerule", args[1], args[2]);
                                        }
                                    } else {
                                        message.PrivateMessage("team", "rulevalue", args[1], String.valueOf(team.getRule(playerTeam, args[1])));
                                    }
                                } else {
                                    if (args.length > 1) {
                                        message.PrivateMessage("team", "rulenotfound", args[1]);
                                    } else {
                                        message.PrivateMessage("team", "rulemissing");
                                    }
                                }
                            } else {
                                message.PrivateMessage("team", "leaderrequired");
                                return true;
                            }
                        } else {
                            message.PrivateMessage("team", "noteam");
                        }
                    } else {
                        permission.not();
                    }
                } else {
                    if (permission.has("essence.team.list")) {
                        if (team.exists(args[0])) {
                            message.PrivateMessage("team", "name", args[0]);
                            message.PrivateMessage("team", "leader", team.getTeamLeader(args[0]));
                            message.PrivateMessage("team", "members", team.getTeamMembers(args[0]));
                        } else {
                            message.PrivateMessage("team", "malformed");
                        }
                    } else {
                        message.PrivateMessage("team", "malformed");
                    }
                }
            } else {
                message.PrivateMessage("team", "malformed");
            }
        }
        return true;
    }
}
