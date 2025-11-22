package net.lewmc.essence.teleportation.spawn;

import net.lewmc.essence.Essence;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Tab completer for the /home command.
 */
public class TabCompleterSpawn implements TabCompleter {

    private final Essence plugin;

    /**
     * Constructor for the HomeTabCompleter command.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public TabCompleterSpawn(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Tab completer for the /home command.
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
        List<World> worlds = Bukkit.getWorlds();

        Set<String> keys = new HashSet<>();
        for (World world : worlds) {
            keys.add(world.getName());
        }

        return new ArrayList<>(Objects.requireNonNullElseGet(keys, () -> List.of("")));
    }
}
