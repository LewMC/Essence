package net.lewmc.essence.tabcompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Tab completer for the /gamemode command.
 */
public class GamemodeTabCompleter implements TabCompleter {

    /**
     * Tab completer for the /gamemode command.
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
            keys = new String[]{"creative", "survival", "adventure", "spectator"};
        } else if (args.length == 2) {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            keys = new String[onlinePlayers.size()];
            int i = 0;
            for (Player player : onlinePlayers) {
                keys[i++] = player.getName();
            }
        } else {
            return null;
        }

        return new ArrayList<>(Arrays.asList(keys));
    }
}
