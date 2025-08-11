package net.lewmc.essence.teleportation.tp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tab completer for the /tp command.
 */
public class TabCompleterTp implements TabCompleter {
    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            String[] args
    ) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.addAll(Arrays.asList("@a", "@s", "@p"));
            for (Player p : Bukkit.getOnlinePlayers()) {
                result.add(p.getName());
            }
        } else if (args.length == 2) {
            if (args[0].startsWith("@")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    result.add(p.getName());
                }
            } else {
                result.add("~");
            }
        } else if (args.length == 3 || args.length == 4) {
            result.add("~");
        }
        return result;
    }
} 