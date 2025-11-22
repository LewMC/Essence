package net.lewmc.essence.world;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.Parser;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * /world command.
 */
public class CommandWorld extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandWorld class.
     *
     * @param plugin References to the main plugin class
     */
    public CommandWorld(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     *
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.world";
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
        UtilMessage msg = new UtilMessage(this.plugin, cs);
        UtilPermission perms = new UtilPermission(this.plugin, cs);

        if (args.length < 2) {
            if (!(cs instanceof Player)) {
                msg.send("world", "usage");
            }
        } else {
            String name = args[1];
            if (name == null || name.isEmpty()) {
                msg.send("world", "error", new String[]{"World names cannot be empty."});
            } else if (args[0].equalsIgnoreCase("create")) {
                if (perms.has("essence.world.create")) {
                    Map<String, String> flags = new Parser().flags(args);
                    UtilWorld.WORLD_STATUS ws = new UtilWorld(this.plugin).create(name, flags);
                    if (ws == UtilWorld.WORLD_STATUS.LOADED) {
                        msg.send("world", "created", new String[]{name});
                    } else if (ws == UtilWorld.WORLD_STATUS.INVALID_S) {
                        msg.send("world", "error", new String[]{"Seed '"+flags.get("-s")+"' is invalid."});
                    } else if (ws == UtilWorld.WORLD_STATUS.INVALID_E) {
                        msg.send("world", "error", new String[]{"Environment '"+flags.get("-e")+"' is invalid."});
                    } else if (ws == UtilWorld.WORLD_STATUS.INVALID_H) {
                        msg.send("world", "error", new String[]{"Hardcore mode '"+flags.get("-h")+"' is invalid."});
                    } else if (ws == UtilWorld.WORLD_STATUS.INVALID_L) {
                        msg.send("world", "error", new String[]{"Load Spawn mode '"+flags.get("-l")+"' is invalid."});
                    } else if (ws == UtilWorld.WORLD_STATUS.INVALID_T) {
                        msg.send("world", "error", new String[]{"World Type '"+flags.get("-t")+"' is invalid."});
                    } else if (ws == UtilWorld.WORLD_STATUS.INVALID_N) {
                        msg.send("world", "error", new String[]{"Generate Structures '"+flags.get("-n")+"' is invalid."});
                    } else if (ws == UtilWorld.WORLD_STATUS.OTHER_ERROR) {
                        msg.send("world", "error", new String[]{"An unknown error occurred."});
                    } else if (ws == UtilWorld.WORLD_STATUS.EXISTS) {
                        msg.send("world", "error", new String[]{"A world called '"+name+"' already exists."});
                    } else if (ws == UtilWorld.WORLD_STATUS.INVALID_CHARS) {
                        msg.send("world", "error", new String[]{"World names cannot contain special characters."});
                    } else {
                        msg.send("world", "error", new String[]{"World creation resulted in an unhandled outcome."});
                    }
                    return true;
                } else {
                    return perms.not();
                }
            } else if (args[0].equalsIgnoreCase("unload")) {
                UtilWorld.WORLD_STATUS ws = new UtilWorld(this.plugin).unload(name);
                if (ws == UtilWorld.WORLD_STATUS.UNLOADED) {
                    msg.send("world", "unloaded", new String[]{name});
                } else if (ws == UtilWorld.WORLD_STATUS.NOT_FOUND) {
                    msg.send("world", "notfound", new String[]{name});
                } else if (ws == UtilWorld.WORLD_STATUS.OTHER_ERROR) {
                    msg.send("world", "error", new String[]{"Unable to unload world '"+name+"'."});
                } else {
                    msg.send("world", "error", new String[]{"World unloading resulted in an unhandled outcome."});
                }
            } else if (args[0].equalsIgnoreCase("load")) {
                UtilWorld.WORLD_STATUS ws = new UtilWorld(this.plugin).load(name);
                if (ws == UtilWorld.WORLD_STATUS.LOADED) {
                    msg.send("world", "loaded", new String[]{name});
                } else if (ws == UtilWorld.WORLD_STATUS.NOT_FOUND) {
                    msg.send("world", "notfound", new String[]{name});
                } else if (ws == UtilWorld.WORLD_STATUS.OTHER_ERROR) {
                    msg.send("world", "error", new String[]{"Unable to load world '"+name+"'."});
                } else {
                    msg.send("world", "error", new String[]{"World loading resulted in an unhandled outcome."});
                }
            } else {
                msg.send("world", "usage");
            }
        }
        return true;
    }
}