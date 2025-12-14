package net.lewmc.essence.teleportation.spawn;

import net.lewmc.essence.Essence;
import net.lewmc.essence.world.UtilWorld;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Tab completer for the /spawn command.
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
        List<UtilWorld.ESSENCE_WORLD> worlds = new UtilWorld(plugin).list();

        Set<String> keys = new HashSet<>();
        for (UtilWorld.ESSENCE_WORLD world : worlds) {
            if (world.status == UtilWorld.WORLD_STATUS.LOADED) {
                keys.add(world.name);
            }
        }

        return new ArrayList<>(Objects.requireNonNullElseGet(keys, () -> List.of("")));
    }
}
