package net.lewmc.essence.kit;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ModuleKit extends FoundryModule {
    public ModuleKit(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    @Override
    public void registerCommands() {
        reg.command("kit", new CommandKit((Essence) plugin));
    }

    @Override
    public void registerTabCompleters() {}

    @Override
    public void registerEvents() {}
}