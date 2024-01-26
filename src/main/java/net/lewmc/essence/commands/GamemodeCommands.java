package net.lewmc.essence.commands;

import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.PermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GamemodeCommands implements CommandExecutor {
    private final Essence plugin;
    private PermissionHandler permission;
    private MessageUtil message;
    private Player player;
    private boolean isPlayer;
    private String toPlayer;

    /**
     * Constructor for the GamemodeCommands class.
     * @param plugin References to the main plugin class.
     */
    public GamemodeCommands(Essence plugin) {
        this.plugin = plugin;
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

        if (!(commandSender instanceof Player)) {
            this.isPlayer = false;
        } else {
            this.player = (Player) commandSender;
            this.isPlayer = true;
        }

        this.message = new MessageUtil(commandSender, plugin);
        this.permission = new PermissionHandler(commandSender, this.message);

        if (command.getName().equalsIgnoreCase("gamemode")) {
            if (args.length == 2) {
                this.toPlayer = args[1];
            }
            if (args.length == 1 || args.length == 2) {
                if (args[0].equalsIgnoreCase("survival")) {
                    gamemodeSurvival();
                } else if (args[0].equalsIgnoreCase("creative")) {
                    gamemodeCreative();
                } else if (args[0].equalsIgnoreCase("adventure")) {
                    gamemodeAdventure();
                } else if (args[0].equalsIgnoreCase("spectator")) {
                    gamemodeSpectator();
                } else {
                    message.PrivateMessage("gamemode", "usage");
                }
                return true;
            } else {
                return noModeSet();
            }
        }

        if (command.getName().equalsIgnoreCase("gmc")) {
            if (args.length == 1) {
                this.toPlayer = args[0];
            }
            return gamemodeCreative();
        }

        if (command.getName().equalsIgnoreCase("gms")) {
            if (args.length == 1) {
                this.toPlayer = args[0];
            }
            return gamemodeSurvival();
        }

        if (command.getName().equalsIgnoreCase("gma")) {
            if (args.length == 1) {
                this.toPlayer = args[0];
            }
            return gamemodeAdventure();
        }

        if (command.getName().equalsIgnoreCase("gmsp")) {
            if (args.length == 1) {
                this.toPlayer = args[0];
            }
            return gamemodeSpectator();
        }

        return false;
    }

    public boolean gamemodeSurvival () {
        if (this.permission.has("essence.gamemode.survival")) {
            if (isPlayer) {
                this.player.setGameMode(GameMode.SURVIVAL);
                this.message.PrivateMessage("gamemode", "survival");
            } else {
                Player player = Bukkit.getPlayer(this.toPlayer);
                if (player != null) {
                    player.setGameMode(GameMode.SURVIVAL);
                    this.message.SendTo(player, "gamemode", "survival");
                }
            }
        } else {
            this.permission.not();
        }
        return true;
    }

    public boolean gamemodeCreative () {
        if (this.permission.has("essence.gamemode.creative")) {
            if (isPlayer) {
                this.player.setGameMode(GameMode.CREATIVE);
                this.message.PrivateMessage("gamemode", "creative");
            } else {
                Player player = Bukkit.getPlayer(this.toPlayer);
                if (player != null) {
                    player.setGameMode(GameMode.CREATIVE);
                    this.message.SendTo(player, "gamemode", "creative");
                }
            }
        } else {
            this.permission.not();
        }
        return true;
    }

    public boolean gamemodeSpectator () {
        if (this.permission.has("essence.gamemode.spectator")) {
            if (isPlayer) {
                this.player.setGameMode(GameMode.SPECTATOR);
                this.message.PrivateMessage("gamemode", "spectator");
            } else {
                Player player = Bukkit.getPlayer(this.toPlayer);
                if (player != null) {
                    player.setGameMode(GameMode.SPECTATOR);
                    this.message.SendTo(player, "gamemode", "spectator");
                }
            }
        } else {
            this.permission.not();
        }
        return true;
    }

    public boolean gamemodeAdventure () {
        if (this.permission.has("essence.gamemode.adventure")) {
            if (isPlayer) {
                this.player.setGameMode(GameMode.ADVENTURE);
                this.message.PrivateMessage("gamemode", "adventure");
            } else {
                Player player = Bukkit.getPlayer(this.toPlayer);
                if (player != null) {
                    player.setGameMode(GameMode.ADVENTURE);
                    this.message.SendTo(player, "gamemode", "adventure");
                }
            }
        } else {
            this.permission.not();
        }
        return true;
    }

    public boolean noModeSet () {
        this.message.PrivateMessage("gamemode", "specify");
        return true;
    }
}