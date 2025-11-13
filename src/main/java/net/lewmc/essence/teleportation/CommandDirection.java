package net.lewmc.essence.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDirection extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the DirectionCommand class.
     *
     * @param plugin References to the main plugin class
     */
    public CommandDirection(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     *
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.teleport.direction";
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

        Player p = (Player) cs;

        float yaw = p.getLocation().getYaw();
        // Normalize the yaw to 0-360
        yaw = (yaw + 360) % 360;
        
        // Format yaw to whole number
        int yawInt = Math.round(yaw);
        
        // Convert yaw to cardinal direction index (0-7)
        int index = (int) ((yaw + 22.5) / 45) % 8;
        
        // Map index to direction key
        String[] directionKeys = {
            "south", "south_west", "west", "north_west",
            "north", "north_east", "east", "south_east"
        };
        
        // Send the appropriate direction message with the yaw value
        msg.send("direction", "direction_" + directionKeys[index], new String[] { String.valueOf(yawInt) });
        return true;
    }
}
