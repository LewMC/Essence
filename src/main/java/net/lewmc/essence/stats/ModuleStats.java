package net.lewmc.essence.stats;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ModuleStats extends FoundryModule {
    public ModuleStats(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    @Override
    public void registerCommands() {
        reg.command("feed", new CommandFeed((Essence) plugin));
        reg.command("heal", new CommandHeal((Essence) plugin));
        reg.command("repair", new CommandRepair((Essence) plugin));
    }

    @Override
    public void registerTabCompleters() {}

    @Override
    public void registerEvents() {}
}