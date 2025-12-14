package net.lewmc.essence.teleportation.spawn;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandSetspawn extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the SetspawnCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandSetspawn(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.spawn.set";
    }

    /**
     * @param cs       Information about who sent the command - player or console.
     * @param command  Information about what command was sent.
     * @param s        Command label - not used here.
     * @param args     The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        Player p = (Player) cs;
        Location loc = p.getLocation();
        Files spawnFile = new Files(this.plugin.foundryConfig, this.plugin);

        UUID world = loc.getWorld().getUID();

        spawnFile.load("data/worlds.yml");

        spawnFile.set("world." + world + ".spawn.x", loc.getX());
        spawnFile.set("world." + world + ".spawn.y", loc.getY());
        spawnFile.set("world." + world + ".spawn.z", loc.getZ());
        spawnFile.set("world." + world + ".spawn.yaw", loc.getYaw());
        spawnFile.set("world." + world + ".spawn.pitch", loc.getPitch());

        // Save the configuration to the file
        spawnFile.save();
        spawnFile.close();

        new UtilMessage(this.plugin, cs).send("spawn", "set");

        return true;
    }
}