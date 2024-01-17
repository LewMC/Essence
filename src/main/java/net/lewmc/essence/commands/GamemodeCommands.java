package net.lewmc.essence.commands;

import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.PermissionHandler;
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
            plugin.getLogger().warning("[Essence] Sorry, you need to be an in-game player to use this command.");
            return true;
        }
        Player player = (Player) commandSender;

        this.permission = new PermissionHandler(player, message);
        this.message = new MessageUtil(commandSender, plugin);
        this.player = player;

        if (command.getName().equalsIgnoreCase("gamemode") || command.getName().equalsIgnoreCase("gm")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("survival")) {
                    gamemodeSurvival();
                } else if (args[0].equalsIgnoreCase("creative")) {
                    gamemodeCreative();
                } else if (args[0].equalsIgnoreCase("adventure")) {
                    gamemodeAdventure();
                } else if (args[0].equalsIgnoreCase("spectator")) {
                    gamemodeSpectator();
                }
            } else {
                return noModeSet();
            }
        }

        if (command.getName().equalsIgnoreCase("gmc")) {
            return gamemodeCreative();
        }

        if (command.getName().equalsIgnoreCase("gms")) {
            return gamemodeSurvival();
        }

        if (command.getName().equalsIgnoreCase("gma")) {
            return gamemodeAdventure();
        }

        if (command.getName().equalsIgnoreCase("gmsp")) {
            return gamemodeSpectator();
        }

        return false;
    }

    public boolean gamemodeSurvival () {
        if (this.permission.has("essence.gamemode.survival")) {
            this.player.setGameMode(GameMode.SURVIVAL);
            this.message.PrivateMessage("Switched to survival mode.", false);
        } else {
            this.permission.not();
        }
        return true;
    }

    public boolean gamemodeCreative () {
        if (this.permission.has("essence.gamemode.creative")) {
            this.player.setGameMode(GameMode.CREATIVE);
            this.message.PrivateMessage("Switched to creative mode.", false);
        } else {
            this.permission.not();
        }
        return true;
    }

    public boolean gamemodeSpectator () {
        if (this.permission.has("essence.gamemode.spectator")) {
            this.player.setGameMode(GameMode.SPECTATOR);
            this.message.PrivateMessage("Switched to spectator mode.", false);
        } else {
            this.permission.not();
        }
        return true;
    }

    public boolean gamemodeAdventure () {
        if (this.permission.has("essence.gamemode.adventure")) {
            this.player.setGameMode(GameMode.ADVENTURE);
            this.message.PrivateMessage("Switched to adventure mode.", false);
        } else {
            this.permission.not();
        }
        return true;
    }

    public boolean noModeSet () {
        this.message.PrivateMessage("Please specify a gamemode (example: /gamemode creative)", true);
        return true;
    }
}