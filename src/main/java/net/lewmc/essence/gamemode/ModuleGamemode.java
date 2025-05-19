package net.lewmc.essence.gamemode;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ModuleGamemode extends FoundryModule {
    public ModuleGamemode(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    @Override
    public void registerCommands() {
        reg.command(new String[] {"gamemode", "gmc", "gms", "gma", "gmsp"}, new CommandGamemode((Essence) plugin));
    }

    @Override
    public void registerTabCompleters() {
        reg.tabCompleter(new String[] { "gamemode", "gm" }, new TabCompleterGamemode());
    }

    @Override
    public void registerEvents() {}
}