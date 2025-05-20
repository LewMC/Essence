package net.lewmc.essence.gamemode;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPlayer;
import net.lewmc.foundry.Logger;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The Gamemode command.
 */
public class CommandGamemode extends FoundryCommand {

    private final Essence plugin;

    /**
     * Constructor for the CommandGamemode class.
     * @param plugin References to the main plugin class.
     */
    public CommandGamemode(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return null;
    }

    /**
     * Runs the command.
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean  true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("gamemode")) { return cmd.disabled(); }

        Player player;
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
            else { return new UtilGamemode().noModeSet(plugin, cs); }
        } else {
            return new UtilGamemode().noModeSet(plugin, cs);
        }

        if (args.length == 2) {
            player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                new UtilMessage(this.plugin, cs).send("generic", "playernotfound");
                return true;
            }
        } else {
            if (cmd.console(cs)) {
                new Logger(this.plugin.config).warn("Usage: /gamemode "+gamemode.toString().toLowerCase()+" <player>");
                return true;
            } else {
                player = (Player) cs;
            }
        }

        return new UtilPlayer(this.plugin, cs).setGamemode(cs, player, gamemode);
    }
}
