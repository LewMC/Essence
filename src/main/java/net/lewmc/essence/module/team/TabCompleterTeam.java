package net.lewmc.essence.module.team;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tab completer for the /team command.
 */
public class TabCompleterTeam implements TabCompleter {

    /**
     * Tab completer for the /team command.
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
            keys = new String[]{"create", "join", "requests", "accept", "decline", "leave", "changeleader", "kick", "disband"};
        } else if (args.length == 2 && args[0].equalsIgnoreCase("rule")) {
            keys = new String[]{"allow-friendly-fire","allow-team-homes"};
        } else if (args.length == 3 && args[0].equalsIgnoreCase("rule")) {
            keys = new String[]{"true", "false"};
        } else {
            keys = new String[]{};
        }

        return new ArrayList<>(Arrays.asList(keys));
    }
}