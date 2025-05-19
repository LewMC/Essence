package net.lewmc.essence.team;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Team command class.
 */
public class CommandTeam implements CommandExecutor {
    private final Essence plugin;
    /**
     * Constructor for the GamemodeCommands class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandTeam(Essence plugin) {
        this.plugin = plugin;
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
        if (command.getName().equalsIgnoreCase("team")) {
            if (!(cs instanceof Player p)) { return new Logger(this.plugin.config).noConsole(); }
            
            UtilCommand cmd = new UtilCommand(this.plugin, cs);
            if (cmd.isDisabled("team")) { return cmd.disabled(); }

            UtilMessage msg = new UtilMessage(this.plugin, cs);
            UtilPermission perms = new UtilPermission(this.plugin, cs);

            if (args.length > 0) {
                UtilTeam team = new UtilTeam(this.plugin, msg);
                if (args[0].equalsIgnoreCase("create")) {
                    if (perms.has("essence.team.create")) {
                        if (args.length == 2) {
                            team.create(args[1], p.getUniqueId());
                        } else {
                            msg.send("team", "namerequired");
                        }
                    } else {
                        return perms.not();
                    }
                } else if (args[0].equalsIgnoreCase("join")) {
                    if (perms.has("essence.team.join")) {
                        if (args.length == 2) {
                            String playerTeam = team.getPlayerTeam(p.getUniqueId());
                            if (playerTeam == null) {
                                team.requestJoin(args[1], p.getUniqueId());
                            } else {
                                msg.send("team", "alreadyinteam", new String[] { playerTeam });
                            }
                        } else {
                            msg.send("team", "namerequired");
                        }
                    } else {
                        return perms.not();
                    }
                } else if (args[0].equalsIgnoreCase("requests")) {
                    if (perms.has("essence.team.manage")) {
                        String playerTeam = team.getPlayerTeam(p.getUniqueId());
                        if (playerTeam != null) {
                            if (team.isLeader(playerTeam, p.getUniqueId())) {
                                msg.send("team", "teamrequests", new String[] { playerTeam, team.requestsToJoin(playerTeam) });
                                msg.send("team", "howtoaccept");
                                msg.send("team", "howtodecline");
                            } else {
                                msg.send("team", "leaderrequired");
                            }
                        } else {
                            msg.send("team", "noteam");
                        }
                    } else {
                        return perms.not();
                    }
                } else if (args[0].equalsIgnoreCase("accept")) {
                    if (perms.has("essence.team.manage")) {
                        if (args.length > 1) {
                            String playerTeam = team.getPlayerTeam(p.getUniqueId());
                            if (!team.hasRequested(playerTeam, args[1])) {
                                msg.send("team", "usernotrequested", new String[] { args[1], playerTeam });
                                return true;
                            }
                            if (team.isLeader(playerTeam, p.getUniqueId())) {
                                if (team.acceptRequest(playerTeam, args[1])) {
                                    msg.send("team", "accepted", new String[] { args[1] });
                                } else {
                                    msg.send("generic", "exception");
                                }
                            } else {
                                msg.send("team", "leaderrequired");
                            }
                        } else {
                            msg.send("team", "usernamerequired");
                        }
                    } else {
                        return perms.not();
                    }
                } else if (args[0].equalsIgnoreCase("decline")) {
                    if (perms.has("essence.team.manage")) {
                        if (args.length > 1) {
                            String playerTeam = team.getPlayerTeam(p.getUniqueId());
                            if (!team.hasRequested(playerTeam, args[1])) {
                                msg.send("team", "usernotrequested", new String[] { args[1], playerTeam });
                                return true;
                            }
                            if (team.isLeader(playerTeam, p.getUniqueId())) {
                                if (team.declineRequest(playerTeam, args[1])) {
                                    msg.send("team", "declined", new String[] { args[1] });
                                } else {
                                    msg.send("generic", "exception");
                                }
                            } else {
                                msg.send("team", "leaderrequired");
                            }
                        } else {
                            msg.send("team", "usernamerequired");
                        }
                    } else {
                        return perms.not();
                    }
                } else if (args[0].equalsIgnoreCase("leave")) {
                    if (perms.has("essence.team.join")) {
                        String playerTeam = team.getPlayerTeam(p.getUniqueId());
                        if (playerTeam != null) {
                            if (team.isLeader(playerTeam, p.getUniqueId())) {
                                msg.send("team", "requiresdisband", new String[] { playerTeam });
                                return true;
                            }

                            if (team.leave(playerTeam, p.getUniqueId())) {
                                msg.send("team", "left", new String[] { playerTeam });
                            } else {
                                msg.send("generic", "exception");
                            }
                        } else {
                            msg.send("team", "noteam");
                        }
                    } else {
                        return perms.not();
                    }
                } else if (args[0].equalsIgnoreCase("changeleader")) {
                    if (perms.has("essence.team.manage")) {
                        String playerTeam = team.getPlayerTeam(p.getUniqueId());
                        if (args.length <= 1) {
                            msg.send("team", "leadernamerequired");
                            return true;
                        }
                        if (playerTeam != null) {
                            if (!team.isMember(playerTeam, args[1])) {
                                msg.send("team", "usernotmember", new String[] { args[1], playerTeam });
                                return true;
                            }

                            if (team.isLeader(playerTeam, p.getUniqueId())) {
                                if (team.changeLeader(playerTeam, args[1], String.valueOf(p.getUniqueId()))) {
                                    msg.send("team", "leadertransfer", new String[] { args[1], playerTeam });
                                } else {
                                    msg.send("general", "exception");
                                }
                            } else {
                                msg.send("team", "leaderrequired");
                                return true;
                            }
                        } else {
                            msg.send("team", "noteam");
                        }
                    } else {
                        return perms.not();
                    }
                } else if (args[0].equalsIgnoreCase("kick")) {
                    if (perms.has("essence.team.manage")) {
                        String playerTeam = team.getPlayerTeam(p.getUniqueId());
                        if (args.length <= 1) {
                            msg.send("team", "usernamerequired");
                            return true;
                        }
                        if (playerTeam != null) {
                            if (team.isLeader(playerTeam, p.getUniqueId())) {
                                if (!team.isMember(playerTeam, args[1])) {
                                    msg.send("team", "usernotmember", new String[] { args[1], playerTeam });
                                    return true;
                                }
                                if (team.kick(playerTeam, args[1])) {
                                    msg.send("team", "kicked", new String[] { args[1], playerTeam });
                                } else {
                                    msg.send("generic", "exception");
                                }
                            } else {
                                msg.send("team", "leaderrequired");
                                return true;
                            }
                        } else {
                            msg.send("team", "noteam");
                        }
                    } else {
                        return perms.not();
                    }
                } else if (args[0].equalsIgnoreCase("disband")) {
                    if (perms.has("essence.team.manage")) {
                        String playerTeam = team.getPlayerTeam(p.getUniqueId());
                        if (playerTeam != null) {
                            if (team.isLeader(playerTeam, p.getUniqueId())) {
                                if (team.disband(playerTeam, p.getUniqueId().toString())) {
                                    msg.send("team", "disbanded", new String[] { playerTeam });
                                } else {
                                    msg.send("generic", "exception");
                                }
                            } else {
                                msg.send("team", "leaderrequired");
                                return true;
                            }
                        } else {
                            msg.send("team", "noteam");
                        }
                    } else {
                        return perms.not();
                    }
                } else if (args[0].equalsIgnoreCase("rule")) {
                    if (perms.has("essence.team.manage")) {
                        String playerTeam = team.getPlayerTeam(p.getUniqueId());
                        if (playerTeam != null) {
                            if (team.isLeader(playerTeam, p.getUniqueId())) {
                                if (args.length > 1 && args[1].equalsIgnoreCase("allow-friendly-fire")) {
                                    if (args.length > 2) {
                                        boolean value;
                                        if (args[2].equalsIgnoreCase("false")) {
                                            value = false;
                                        } else if (args[2].equalsIgnoreCase("true")) {
                                            value = true;
                                        } else {
                                            msg.send("team", "malformed");
                                            return true;
                                        }

                                        if (team.setRule(playerTeam, args[1], value)) {
                                            msg.send("team", "rulechanged", new String[] { args[1], args[2] } );
                                        } else {
                                            msg.send("team", "cantchangerule", new String[] { args[1], args[2] });
                                        }
                                    } else {
                                        msg.send("team", "rulevalue", new String[] { args[1], String.valueOf(team.getRule(playerTeam, args[1])) });
                                    }
                                } else {
                                    if (args.length > 1) {
                                        msg.send("team", "rulenotfound", new String[] { args[1] });
                                    } else {
                                        msg.send("team", "rulemissing");
                                    }
                                }
                            } else {
                                msg.send("team", "leaderrequired");
                                return true;
                            }
                        } else {
                            msg.send("team", "noteam");
                        }
                    } else {
                        return perms.not();
                    }
                } else {
                    if (perms.has("essence.team.list")) {
                        if (team.exists(args[0])) {
                            msg.send("team", "name", new String[] { args[0] });
                            msg.send("team", "leader", new String[] { team.getTeamLeader(args[0]) });
                            msg.send("team", "members", new String[] { team.getTeamMembers(args[0]) });
                        } else {
                            msg.send("team", "malformed");
                        }
                    } else {
                        msg.send("team", "malformed");
                    }
                }
            } else {
                msg.send("team", "malformed");
            }
        }
        return true;
    }
}
