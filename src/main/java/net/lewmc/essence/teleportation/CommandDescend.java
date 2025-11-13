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
 * /descend command.
 */
public class CommandDescend extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the DescendCommand class.
     *
     * @param plugin References to the main plugin class
     */
    public CommandDescend(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     *
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.teleport.descend";
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
                return this.descend((Player) cs, levels, msg);
            } catch (NumberFormatException e) {
                msg.send("descend", "invalidnumber", new String[] { args[0] });
                return true;
            }
        }
        return this.descend((Player) cs, levels, msg);
    }

    /**
     * Handles the teleportation to the next or specified level for a player
     * @param cs The player to teleport
     * @param levels The number of levels to ascend
     * @param msg Message utility
     * @return true
     */
    private boolean descend(Player cs, Integer levels, UtilMessage msg) {
        UtilTeleport.LevelLocation result = findLevelLocation(cs.getLocation(), UtilTeleport.Direction.DOWN, levels, cs);
        Location descendLocation = result.location();
        int finalLevels = result.finalLevels();

        int waitTime = plugin.config.get("teleportation.descend.wait") != null ?
                (int) plugin.config.get("teleportation.descend.wait") : 0;

        if (descendLocation == null) {
            msg.send("descend", "nosafelocation");
            return true;
        }

        new UtilTeleport(this.plugin).doTeleport(cs, descendLocation, waitTime);

        if (waitTime == 0) {
            msg.send("descend", "going", new String[] { String.valueOf(finalLevels) });
            return true;
        }
        return true;
    }
}