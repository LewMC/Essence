package net.lewmc.essence.world;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.teleportation.spawn.CommandSpawn;
import net.lewmc.foundry.Parser;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * /world command.
 */
public class CommandWorld extends FoundryCommand {
    private final Essence plugin;
    private UtilPermission perms;
    private UtilMessage msg;

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
        this.msg = new UtilMessage(this.plugin, cs);
        this.perms = new UtilPermission(this.plugin, cs);

        if (args.length == 0) {
            msg.send("world", "usage");
            msg.send("world", "usageactions");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                return this.listCommand();
            } else {
                msg.send("world", "usage");
                msg.send("world", "usageactions");
            }
        } else {
            String name = args[1];
            if (name == null || name.isEmpty()) {
                msg.send("generic", "customerror", new String[]{"World names cannot be empty."});
            } else if (args[0].equalsIgnoreCase("create")) {
                return this.createCommand(args, name);
            } else if (args[0].equalsIgnoreCase("delete")) {
                return this.deleteCommand(name);
            } else if (args[0].equalsIgnoreCase("load")) {
                return this.loadCommand(name);
            } else if (args[0].equalsIgnoreCase("unload")) {
                return this.unloadCommand(name);
            } else if (args[0].equalsIgnoreCase("tp")) {
                return new CommandSpawn(this.plugin).onCommand(cs, command, s, args);
            } else {
                msg.send("world", "usage");
                msg.send("world", "usageactions");
            }
        }
        return true;
    }

    /**
     * Processes the create command.
     * @param args String[] - Command arguments
     * @param name String - The world name
     * @return boolean - always true
     */
    private boolean createCommand(String[] args, String name) {
        if (!perms.has("essence.world.create")) { return perms.not(); }
        Map<String, String> flags = new Parser().flags(args);
        UtilWorld.WORLD_STATUS ws = new UtilWorld(this.plugin).create(name, flags);
        if (ws == UtilWorld.WORLD_STATUS.LOADED) {
            msg.send("world", "created", new String[]{name});
        } else if (ws == UtilWorld.WORLD_STATUS.INVALID_S) {
            msg.send("world", "notcreated", new String[]{name, "Seed '" + flags.get("-s") + "' is invalid."});
        } else if (ws == UtilWorld.WORLD_STATUS.INVALID_E) {
            msg.send("world", "notcreated", new String[]{name, "Environment '" + flags.get("-e") + "' is invalid."});
        } else if (ws == UtilWorld.WORLD_STATUS.INVALID_H) {
            msg.send("world", "notcreated", new String[]{name, "Hardcore mode '" + flags.get("-h") + "' is invalid."});
        } else if (ws == UtilWorld.WORLD_STATUS.INVALID_L) {
            msg.send("world", "notcreated", new String[]{name, "Load Spawn mode '" + flags.get("-l") + "' is invalid."});
        } else if (ws == UtilWorld.WORLD_STATUS.INVALID_T) {
            msg.send("world", "notcreated", new String[]{name, "World Type '" + flags.get("-t") + "' is invalid."});
        } else if (ws == UtilWorld.WORLD_STATUS.INVALID_N) {
            msg.send("world", "notcreated", new String[]{name, "Generate Structures '" + flags.get("-n") + "' is invalid."});
        } else if (ws == UtilWorld.WORLD_STATUS.INVALID_A) {
            msg.send("world", "notcreated", new String[]{name, "Autoload '" + flags.get("-n") + "' is invalid."});
        } else if (ws == UtilWorld.WORLD_STATUS.OTHER_ERROR) {
            msg.send("world", "notcreated", new String[]{name, "An unknown error occurred."});
        } else if (ws == UtilWorld.WORLD_STATUS.EXISTS) {
            msg.send("world", "notcreated", new String[]{name, "A world called '" + name + "' already exists."});
        } else if (ws == UtilWorld.WORLD_STATUS.INVALID_CHARS) {
            msg.send("world", "notcreated", new String[]{name, "World names cannot contain special characters."});
        } else {
            msg.send("world", "notcreated", new String[]{name, "World creation resulted in an unhandled outcome."});
        }
        return true;
    }

    /**
     * Processes the unload command.
     * @param name String - The world name
     * @return boolean - always true
     */
    private boolean unloadCommand(String name) {
        if (!perms.has("essence.world.unload")) { return perms.not(); }
        UtilWorld.WORLD_STATUS ws = new UtilWorld(this.plugin).unload(name);
        if (ws == UtilWorld.WORLD_STATUS.UNLOADED) {
            msg.send("world", "unloaded", new String[]{name});
        } else if (ws == UtilWorld.WORLD_STATUS.NOT_FOUND) {
            msg.send("world", "notfound", new String[]{name});
        } else if (ws == UtilWorld.WORLD_STATUS.OTHER_ERROR) {
            msg.send("generic", "customerror", new String[]{"Unable to unload world '"+name+"'."});
        } else {
            msg.send("generic", "customerror", new String[]{"World unloading resulted in an unhandled outcome."});
        }
        return true;
    }
    /**
     * Processes the delete command.
     * @param name String - The world name
     * @return boolean - always true
     */
    private boolean deleteCommand(String name) {
        if (!perms.has("essence.world.delete")) { return perms.not(); }
        UtilWorld.WORLD_STATUS ws = new UtilWorld(this.plugin).delete(name);
        if (ws == UtilWorld.WORLD_STATUS.LOADED) {
            msg.send("world", "deleteunloaded", new String[]{name});
        } else if (ws == UtilWorld.WORLD_STATUS.NOT_FOUND) {
            msg.send("world", "notfound", new String[]{name});
        } else if (ws == UtilWorld.WORLD_STATUS.OTHER_ERROR) {
            msg.send("generic", "customerror", new String[]{"Unable to delete world '"+name+"'."});
        } else if (ws == UtilWorld.WORLD_STATUS.DELETED) {
            msg.send("world", "deleted", new String[]{name});
        } else {
            msg.send("generic", "customerror", new String[]{"World deletion resulted in an unhandled outcome."});
        }
        return true;
    }

    /**
     * Processes the load command.
     * @param name String - The world name
     * @return boolean - always true
     */
    private boolean loadCommand(String name) {
        if (!perms.has("essence.world.load")) { return perms.not(); }
        UtilWorld.WORLD_STATUS ws = new UtilWorld(this.plugin).load(name);
        if (ws == UtilWorld.WORLD_STATUS.LOADED) {
            msg.send("world", "loaded", new String[]{name});
        } else if (ws == UtilWorld.WORLD_STATUS.NOT_FOUND) {
            msg.send("world", "notfound", new String[]{name});
        } else if (ws == UtilWorld.WORLD_STATUS.OTHER_ERROR) {
            msg.send("world", "notloaded", new String[]{name, "unable to load due to unknown error"});
        } else {
            msg.send("world", "notloaded", new String[]{name, "world loading resulted in an unhandled outcome."});
        }
        return true;
    }

    /**
     * Processes the list command.
     * @return boolean - always true
     */
    private boolean listCommand() {
        if (!perms.has("essence.world.list")) { return perms.not(); }
        List<UtilWorld.ESSENCE_WORLD> worlds = new UtilWorld(this.plugin).list();
        String ll = worlds.stream()
                .filter(world -> world.status == UtilWorld.WORLD_STATUS.LOADED)
                .map(world -> world.name)
                .collect(Collectors.joining(","));
        String lu = worlds.stream()
                .filter(world -> world.status == UtilWorld.WORLD_STATUS.UNLOADED)
                .map(world -> world.name)
                .collect(Collectors.joining(","));
        msg.send("world", "listloaded", new String[]{ll});
        msg.send("world", "listunloaded", new String[]{lu});
        return true;
    }
}