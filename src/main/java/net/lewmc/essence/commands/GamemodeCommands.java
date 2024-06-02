package net.lewmc.essence.commands;

import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Gamemode commands class.
 */
public class GamemodeCommands implements CommandExecutor {
    private final Essence plugin;
    private MessageUtil message;
    private CommandSender commandSender;
    private final LogUtil log;

    /**
     * Constructor for the GamemodeCommands class.
     * @param plugin References to the main plugin class.
     */
    public GamemodeCommands(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(this.plugin);
    }

    /**
     * @param commandSender Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
        @NotNull CommandSender commandSender,
        @NotNull Command command,
        @NotNull String s,
        String[] args
    ) {
        PlayerUtil playerUtil = new PlayerUtil(this.plugin, commandSender);
        this.message = new MessageUtil(commandSender, this.plugin);
        this.commandSender = commandSender;

        Player player;
        if (command.getName().equalsIgnoreCase("gamemode")) {
            GameMode gamemode;
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("creative")) { gamemode = GameMode.CREATIVE; }
                else if (args[0].equalsIgnoreCase("c")) { gamemode = GameMode.CREATIVE; }
                else if (args[0].equalsIgnoreCase("1")) { gamemode = GameMode.CREATIVE; }
                else if (args[0].equalsIgnoreCase("survival")) { gamemode = GameMode.SURVIVAL; }
                else if (args[0].equalsIgnoreCase("s")) { gamemode = GameMode.SURVIVAL; }
                else if (args[0].equalsIgnoreCase("0")) { gamemode = GameMode.SURVIVAL; }
                else if (args[0].equalsIgnoreCase("adventure")) { gamemode = GameMode.ADVENTURE; }
                else if (args[0].equalsIgnoreCase("a")) { gamemode = GameMode.ADVENTURE; }
                else if (args[0].equalsIgnoreCase("2")) { gamemode = GameMode.ADVENTURE; }
                else if (args[0].equalsIgnoreCase("spectator")) { gamemode = GameMode.SPECTATOR; }
                else if (args[0].equalsIgnoreCase("sp")) { gamemode = GameMode.SPECTATOR; }
                else if (args[0].equalsIgnoreCase("3")) { gamemode = GameMode.SPECTATOR; }
                else { return this.noModeSet(); }
            } else {
                return this.noModeSet();
            }

            if (args.length == 2) {
                player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    message.PrivateMessage("generic", "playernotfound");
                    return true;
                }
            } else {
                if (console()) {
                    log.warn("Usage: /gamemode "+gamemode.toString().toLowerCase()+" <player>");
                    return true;
                } else {
                    player = (Player) commandSender;
                }
            }

            return playerUtil.setGamemode(this.commandSender, player, gamemode);
        }

        if (command.getName().equalsIgnoreCase("gmc")) {
            if (args.length == 1) {
                player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    message.PrivateMessage("generic", "playernotfound");
                    return true;
                }
            } else {
                if (console()) {
                    log.warn("Usage: /gmc <player>");
                    return true;
                } else {
                    player = (Player) commandSender;
                }
            }

            return playerUtil.setGamemode(this.commandSender, player, GameMode.CREATIVE);
        }

        if (command.getName().equalsIgnoreCase("gms")) {
            if (args.length == 1) {
                player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    message.PrivateMessage("generic", "playernotfound");
                    return true;
                }
            } else {
                if (console()) {
                    log.warn("Usage: /gms <player>");
                    return true;
                } else {
                    if (console()) {
                        return true;
                    } else {
                        player = (Player) commandSender;
                    }
                }
            }

            return playerUtil.setGamemode(this.commandSender, player, GameMode.SURVIVAL);
        }

        if (command.getName().equalsIgnoreCase("gma")) {
            if (args.length == 1) {
                player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    message.PrivateMessage("generic", "playernotfound");
                    return true;
                }
            } else {
                if (console()) {
                    log.warn("Usage: /gma <player>");
                    return true;
                } else {
                    player = (Player) commandSender;
                }
            }

            return playerUtil.setGamemode(this.commandSender, player, GameMode.ADVENTURE);
        }

        if (command.getName().equalsIgnoreCase("gmsp")) {
            if (args.length == 1) {
                player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    message.PrivateMessage("generic", "playernotfound");
                    return true;
                }
            } else {
                if (console()) {
                    log.warn("Usage: /gmsp <player>");
                    return true;
                } else {
                    player = (Player) commandSender;
                }
            }

            return playerUtil.setGamemode(this.commandSender, player, GameMode.SPECTATOR);
        }
        return false;
    }

    public boolean noModeSet() {
        this.message.PrivateMessage("gamemode", "specify");
        return true;
    }

    public boolean console() {
        return !(this.commandSender instanceof Player);
    }
}