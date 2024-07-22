package net.lewmc.essence.commands;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
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
            return true;
        }
        MessageUtil message = new MessageUtil(commandSender, plugin);
        PermissionHandler permission = new PermissionHandler(commandSender, message);
        Player player = (Player) commandSender;

        if (command.getName().equalsIgnoreCase("team")) {
            CommandUtil cmd = new CommandUtil(this.plugin);
            if (cmd.isDisabled("team")) {
                return cmd.disabled(message);
            }

            if (args.length > 0) {
                TeamUtil team = new TeamUtil(this.plugin, message);
                if (args[0].equalsIgnoreCase("create")) {
                    if (permission.has("essence.team.create")) {
                        if (args.length == 2) {
                            team.CreateNewTeam(args[1], player.getUniqueId());
                        } else {
                            message.send("team", "namerequired");
                        }
                    } else {
                        return permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("join")) {
                    if (permission.has("essence.team.join")) {
                        if (args.length == 2) {
                            String playerTeam = team.getPlayerTeam(player.getUniqueId());
                            if (playerTeam == null) {
                                team.requestJoin(args[1], player.getUniqueId());
                            } else {
                                message.send("team", "alreadyinteam", new String[] { playerTeam });
                            }
                        } else {
                            message.send("team", "namerequired");
                        }
                    } else {
                        return permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("requests")) {
                    if (permission.has("essence.team.manage")) {
                        String playerTeam = team.getPlayerTeam(player.getUniqueId());
                        if (playerTeam != null) {
                            if (team.isLeader(playerTeam, player.getUniqueId())) {
                                message.send("team", "teamrequests", new String[] { playerTeam, team.requestsToJoin(playerTeam) });
                                message.send("team", "howtoaccept");
                                message.send("team", "howtodecline");
                            } else {
                                message.send("team", "leaderrequired");
                            }
                        } else {
                            message.send("team", "noteam");
                        }
                    } else {
                        return permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("accept")) {
                    if (permission.has("essence.team.manage")) {
                        if (args.length > 1) {
                            String playerTeam = team.getPlayerTeam(player.getUniqueId());
                            if (!team.hasRequested(playerTeam, args[1])) {
                                message.send("team", "usernotrequested", new String[] { args[1], playerTeam });
                                return true;
                            }
                            if (team.isLeader(playerTeam, player.getUniqueId())) {
                                if (team.acceptRequest(playerTeam, args[1])) {
                                    message.send("team", "accepted", new String[] { args[1] });
                                } else {
                                    message.send("generic", "exception");
                                }
                            } else {
                                message.send("team", "leaderrequired");
                            }
                        } else {
                            message.send("team", "usernamerequired");
                        }
                    } else {
                        return permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("decline")) {
                    if (permission.has("essence.team.manage")) {
                        if (args.length > 1) {
                            String playerTeam = team.getPlayerTeam(player.getUniqueId());
                            if (!team.hasRequested(playerTeam, args[1])) {
                                message.send("team", "usernotrequested", new String[] { args[1], playerTeam });
                                return true;
                            }
                            if (team.isLeader(playerTeam, player.getUniqueId())) {
                                if (team.declineRequest(playerTeam, args[1])) {
                                    message.send("team", "declined", new String[] { args[1] });
                                } else {
                                    message.send("generic", "exception");
                                }
                            } else {
                                message.send("team", "leaderrequired");
                            }
                        } else {
                            message.send("team", "usernamerequired");
                        }
                    } else {
                        return permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("leave")) {
                    if (permission.has("essence.team.join")) {
                        String playerTeam = team.getPlayerTeam(player.getUniqueId());
                        if (playerTeam != null) {
                            if (team.isLeader(playerTeam, player.getUniqueId())) {
                                message.send("team", "requiresdisband", new String[] { playerTeam });
                                return true;
                            }

                            if (team.leave(playerTeam, player.getUniqueId())) {
                                message.send("team", "left", new String[] { playerTeam });
                            } else {
                                message.send("generic", "exception");
                            }
                        } else {
                            message.send("team", "noteam");
                        }
                    } else {
                        return permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("changeleader")) {
                    if (permission.has("essence.team.manage")) {
                        String playerTeam = team.getPlayerTeam(player.getUniqueId());
                        if (args.length <= 1) {
                            message.send("team", "leadernamerequired");
                            return true;
                        }
                        if (playerTeam != null) {
                            if (!team.isMember(playerTeam, args[1])) {
                                message.send("team", "usernotmember", new String[] { args[1], playerTeam });
                                return true;
                            }

                            if (team.isLeader(playerTeam, player.getUniqueId())) {
                                if (team.changeLeader(playerTeam, args[1], String.valueOf(player.getUniqueId()))) {
                                    message.send("team", "leadertransfer", new String[] { args[1], playerTeam });
                                } else {
                                    message.send("general", "exception");
                                }
                            } else {
                                message.send("team", "leaderrequired");
                                return true;
                            }
                        } else {
                            message.send("team", "noteam");
                        }
                    } else {
                        return permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("kick")) {
                    if (permission.has("essence.team.manage")) {
                        String playerTeam = team.getPlayerTeam(player.getUniqueId());
                        if (args.length <= 1) {
                            message.send("team", "usernamerequired");
                            return true;
                        }
                        if (playerTeam != null) {
                            if (team.isLeader(playerTeam, player.getUniqueId())) {
                                if (!team.isMember(playerTeam, args[1])) {
                                    message.send("team", "usernotmember", new String[] { args[1], playerTeam });
                                    return true;
                                }
                                if (team.kick(playerTeam, args[1])) {
                                    message.send("team", "kicked", new String[] { args[1], playerTeam });
                                } else {
                                    message.send("generic", "exception");
                                }
                            } else {
                                message.send("team", "leaderrequired");
                                return true;
                            }
                        } else {
                            message.send("team", "noteam");
                        }
                    } else {
                        return permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("disband")) {
                    if (permission.has("essence.team.manage")) {
                        String playerTeam = team.getPlayerTeam(player.getUniqueId());
                        if (playerTeam != null) {
                            if (team.isLeader(playerTeam, player.getUniqueId())) {
                                if (team.disband(playerTeam, player.getUniqueId().toString())) {
                                    message.send("team", "disbanded", new String[] { playerTeam });
                                } else {
                                    message.send("generic", "exception");
                                }
                            } else {
                                message.send("team", "leaderrequired");
                                return true;
                            }
                        } else {
                            message.send("team", "noteam");
                        }
                    } else {
                        return permission.not();
                    }
                } else if (args[0].equalsIgnoreCase("rule")) {
                    if (permission.has("essence.team.manage")) {
                        String playerTeam = team.getPlayerTeam(player.getUniqueId());
                        if (playerTeam != null) {
                            if (team.isLeader(playerTeam, player.getUniqueId())) {
                                if (args.length > 1 && args[1].equalsIgnoreCase("allow-friendly-fire")) {
                                    if (args.length > 2) {
                                        boolean value;
                                        if (args[2].equalsIgnoreCase("false")) {
                                            value = false;
                                        } else if (args[2].equalsIgnoreCase("true")) {
                                            value = true;
                                        } else {
                                            message.send("team", "malformed");
                                            return true;
                                        }

                                        if (team.setRule(playerTeam, args[1], value)) {
                                            message.send("team", "rulechanged", new String[] { args[1], args[2] } );
                                        } else {
                                            message.send("team", "cantchangerule", new String[] { args[1], args[2] });
                                        }
                                    } else {
                                        message.send("team", "rulevalue", new String[] { args[1], String.valueOf(team.getRule(playerTeam, args[1])) });
                                    }
                                } else {
                                    if (args.length > 1) {
                                        message.send("team", "rulenotfound", new String[] { args[1] });
                                    } else {
                                        message.send("team", "rulemissing");
                                    }
                                }
                            } else {
                                message.send("team", "leaderrequired");
                                return true;
                            }
                        } else {
                            message.send("team", "noteam");
                        }
                    } else {
                        return permission.not();
                    }
                } else {
                    if (permission.has("essence.team.list")) {
                        if (team.exists(args[0])) {
                            message.send("team", "name", new String[] { args[0] });
                            message.send("team", "leader", new String[] { team.getTeamLeader(args[0]) });
                            message.send("team", "members", new String[] { team.getTeamMembers(args[0]) });
                        } else {
                            message.send("team", "malformed");
                        }
                    } else {
                        message.send("team", "malformed");
                    }
                }
            } else {
                message.send("team", "malformed");
            }
        }
        return true;
    }
}
