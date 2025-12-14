package net.lewmc.essence.world;

import net.lewmc.essence.Essence;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Tab completer for the /world command.
 */
public class TabCompleterWorld implements TabCompleter {
    private final Essence plugin;

    /**
     * Constructor for the TabCompleterWorld class.
     * @param plugin Essence - Reference to the main plugin class.
     */
    public TabCompleterWorld(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Tab completer for the /world command.
     * @param sender CommandSender - The command executor.
     * @param cde Command - The command.
     * @param arg String - Command argument.
     * @param args String[] - Command arguments.
     * @return ArrayList - List of command autocomplete options.
     */
    @Override
    public List<String> onTabComplete(
        @NotNull CommandSender sender,
        @NotNull Command cde,
        @NotNull String arg,
        String[] args
    ) {
        String[] keys;
        if (args.length == 1) {
            keys = new String[]{"create","delete","load","list","tp","unload"};
        } else if (args.length == 2 && !args[0].equalsIgnoreCase("create")) {
            keys = new UtilWorld(this.plugin).list().stream().map(UtilWorld.ESSENCE_WORLD::getName).toArray(String[]::new);
        } else {
            keys = new String[]{};
        }

        return new ArrayList<>(Arrays.asList(keys));

    }
}
