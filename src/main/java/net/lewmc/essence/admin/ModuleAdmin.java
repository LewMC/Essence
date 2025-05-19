package net.lewmc.essence.admin;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ModuleAdmin extends FoundryModule {
    public ModuleAdmin(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    @Override
    public void registerCommands() {
        reg.command("info", new CommandInfo((Essence) plugin));
        reg.command("invisible", new CommandInvisible((Essence) plugin));
        reg.command("seen", new CommandSeen((Essence) plugin));
    }

    @Override
    public void registerTabCompleters() {

    }

    @Override
    public void registerEvents() {

    }
}