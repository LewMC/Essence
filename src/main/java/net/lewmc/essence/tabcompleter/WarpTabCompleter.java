package net.lewmc.essence.tabcompleter;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.FileUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Tab completer for the /warp command.
 */
public class WarpTabCompleter implements TabCompleter {

    private final Essence plugin;

    /**
     * Constructor for the WarpTabCompleter class.
     * @param plugin Essence - Reference to the main Essence class.
     */
    public WarpTabCompleter(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Tab completer for the /warp command.
     * @param sender CommandSender - The command executor.
     * @param cde Command - The command.
     * @param arg String - Command argument.
     * @param args String[] - Command arguments.
     * @return ArrayList<> - List of command autocomplete options.
     */
    @Override
    public List<String> onTabComplete(
        @NotNull CommandSender sender,
        @NotNull Command cde,
        @NotNull String arg,
        String[] args
    ) {
        FileUtil data = new FileUtil(this.plugin);
        data.load("data/warps.yml");
        Set<String> keys = data.getKeys("warps", false);

        data.close();

        return new ArrayList<>(Objects.requireNonNullElseGet(keys, () -> Arrays.asList("")));

    }
}
