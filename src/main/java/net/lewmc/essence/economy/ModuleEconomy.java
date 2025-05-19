package net.lewmc.essence.economy;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ModuleEconomy extends FoundryModule {
    public ModuleEconomy(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    @Override
    public void registerCommands() {
        reg.command("balance", new CommandBalance((Essence) plugin));
        reg.command("pay", new CommandPay((Essence) plugin));
    }

    @Override
    public void registerTabCompleters() {}

    @Override
    public void registerEvents() {}
}