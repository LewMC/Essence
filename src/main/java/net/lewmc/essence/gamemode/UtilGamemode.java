package net.lewmc.essence.gamemode;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPlayer;
import net.lewmc.foundry.Logger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The gamemode utility.
 */
public class UtilGamemode {

    /**
     * Tells the user they haven't set a mode.
     * @param plugin    Reference to the main Essence class.
     * @param cs        CommandSender - The command sender.
     * @return          true/false - Success?
     */
    public boolean noModeSet(Essence plugin, CommandSender cs) {
        new UtilMessage(plugin, cs).send("gamemode", "specify");
        return true;
    }

    /**
     * Processes one of the short gamemode commands.
     * @param command   String - The command's label.
     * @param gm        GameMode - The requested gamemode.
     * @param cs        CommandSender - The sender of the command.
     * @param args      String[] - The arguments sent
     * @param plugin    Reference to the main Essence class.
     * @return          true/false - Success?
     */
    public boolean processShortCommand(String command, GameMode gm, CommandSender cs, String[] args, Essence plugin) {
        UtilCommand cmd = new UtilCommand(plugin, cs);

        Player player;

        if (args.length == 1) {
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                new UtilMessage(plugin, cs).send("generic", "playernotfound");
                return true;
            }
        } else {
            if (cmd.console(cs)) {
                new Logger(plugin.config).warn("Usage: "+command+" <player>");
                return true;
            } else {
                player = (Player) cs;
            }
        }

        return new UtilPlayer(plugin, cs).setGamemode(cs, player, gm);
    }
}
