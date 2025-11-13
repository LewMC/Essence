package net.lewmc.essence.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.teleportation.tp.UtilTeleport;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.lewmc.essence.teleportation.tp.UtilTeleport.findLevelLocation;

/**
 * /ascend command.
 */
public class CommandAscend extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the AscendCommand class.
     *
     * @param plugin References to the main plugin class
     */
    public CommandAscend(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     *
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.teleport.ascend";
    }

    /**
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean  true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage msg = new UtilMessage(this.plugin, cs);

        int levels = 1;
        if (args.length > 0) {
            try {
                levels = Integer.parseInt(args[0]);
                return this.ascend((Player) cs, levels, msg);
            } catch (NumberFormatException e) {
                msg.send("ascend", "invalidnumber", new String[] { args[0] });
                return true;
            }
        }
        return this.ascend((Player) cs, levels, msg);
    }

    /**
     * Handles the teleportation to the next or specified level for a player
     * @param cs The player to teleport
     * @param levels The number of levels to ascend
     * @param msg Message utility
     * @return true
     */
    private boolean ascend(Player cs, Integer levels, UtilMessage msg) {
        UtilTeleport.LevelLocation result = findLevelLocation(cs.getLocation(), UtilTeleport.Direction.UP, levels, cs);

        if (result == null) {
            msg.send("ascend", "nosafelocation");
            return true;
        }

        Location ascendLocation = result.location();
        int finalLevels = result.finalLevels();

        int waitTime = plugin.config.get("teleportation.ascend.wait") != null ?
                (int) plugin.config.get("teleportation.ascend.wait") : 0;

        if (ascendLocation == null) {
            msg.send("ascend", "nosafelocation");
            return true;
        }

        new UtilTeleport(this.plugin).doTeleport(cs, ascendLocation, waitTime);

        if (waitTime == 0) {
            msg.send("ascend", "going", new String[] { String.valueOf(finalLevels) });
            return true;
        }
        return true;
    }
}
