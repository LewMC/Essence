package net.lewmc.essence.stats;

import net.lewmc.essence.Essence;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Tab completer for the /speed command.
 */
public class TabCompleterSpeed implements TabCompleter {

    private final Essence plugin;

    /**
     * Constructor for the SpeedTabCompleter command.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public TabCompleterSpeed(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Tab completer for the /speed command.
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
            keys = new String[]{"reset", "off", "default", "1", "5", "10"};
        } else if (args.length == 2) {
            List<String> players = new ArrayList<>(List.of());
            for (Player p : Bukkit.getOnlinePlayers()) {
                players.add(p.getName());
            }
            keys = players.toArray(new String[0]);
        } else {
            keys = new String[]{};
        }
        return new ArrayList<>(Arrays.asList(keys));
    }
}
