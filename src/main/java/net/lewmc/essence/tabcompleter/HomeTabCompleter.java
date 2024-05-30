package net.lewmc.essence.tabcompleter;

import net.lewmc.essence.Essence;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HomeTabCompleter implements TabCompleter {

    private final Essence plugin;

    public HomeTabCompleter(Essence plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command cde,
            @NotNull String arg,
            String[] args
    ) {
        // TODO: FIX THIS
        return null;
    }
}
