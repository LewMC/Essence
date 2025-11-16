package net.lewmc.essence.teleportation.orientation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.teleportation.tp.UtilTeleport;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandNear extends FoundryPlayerCommand {
    private final Essence plugin;

    public CommandNear(Essence plugin) {
        this.plugin = plugin;
    }

    @Override
    protected String requiredPermission() {
        return "essence.teleport.near";
    }

    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage msg = new UtilMessage(plugin, cs);

        Player p = (Player) cs;

        int radius;

        Object configDefaultRadius = (plugin.config.get("teleportation.near.default-radius"));
        int defaultRadius = (configDefaultRadius != null) ? (int) configDefaultRadius : 200;

        if (args.length == 0) {
            radius = defaultRadius;
        } else {
            try {
                radius = Integer.parseInt(args[0]);
                if (radius <= 0) {
                    msg.send("near", "invalidradius", new String[] { args[0] });
                    return true;
                }

                Object configMaxRadius = (plugin.config.get("teleportation.near.max-radius"));
                int maxRadius = (configMaxRadius != null) ? (int) configMaxRadius : 50000;
                
                if (radius > maxRadius) {
                    msg.send("near", "greaterthanmax", new String[] { args[0], String.valueOf(maxRadius) });
                    return true;
                }
            } catch (NumberFormatException e) {
                msg.send("near", "invalidnumber", new String[] { args[0] });
                return true;
            }
        }

        UtilTeleport tp = new UtilTeleport(this.plugin);
        // Get nearby players
        List<Player> nearbyPlayers = p.getLocation().getNearbyPlayers(radius).stream()
                .filter(player -> !player.getUniqueId().equals(p.getUniqueId())) // Exclude self
                .filter(player -> !player.hasMetadata("vanish")) // Exclude vanished players
                .filter(player -> tp.teleportToggleCheck(p, player)) // Check if player can be teleported to
                .toList();

        // Send results to the player
        if (nearbyPlayers.isEmpty()) {
            msg.send("near", "noplayers", new String[] { String.valueOf(radius) });
        } else {
            StringBuilder playerList = new StringBuilder();
            for (Player nearby : nearbyPlayers) {
                int distance = (int) nearby.getLocation().distance(p.getLocation());
                if (!playerList.isEmpty()) {
                    playerList.append(", ");
                }
                playerList.append(nearby.getName())
                         .append(" (")
                         .append(distance)
                         .append("m)");
            }
            msg.send("near", "playersfound", new String[] { String.valueOf(nearbyPlayers.size()), playerList.toString() });
        }
        return true;
    }
}
