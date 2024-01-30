package net.lewmc.essence.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeamTabCompleter implements TabCompleter {
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
            keys = new String[]{"allow-friendly-fire"};
        } else if (args.length == 3) {
            keys = new String[]{"true", "false"};
        } else {
            keys = new String[]{};
        }

        return new ArrayList<>(Arrays.asList(keys));
    }
}