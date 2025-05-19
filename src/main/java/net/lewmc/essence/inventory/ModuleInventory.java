package net.lewmc.essence.inventory;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ModuleInventory extends FoundryModule {
    public ModuleInventory(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    @Override
    public void registerCommands() {
        reg.command("anvil", new CommandAnvil((Essence) plugin));
        reg.command("cartography", new CommandCartography((Essence) plugin));
        reg.command("craft", new CommandCraft((Essence) plugin));
        reg.command("enderchest", new CommandEnderchest((Essence) plugin));
        reg.command("grindstone", new CommandGrindstone((Essence) plugin));
        reg.command("loom", new CommandLoom((Essence) plugin));
        reg.command("smithing", new CommandSmithing((Essence) plugin));
        reg.command("stonecutter", new CommandStonecutter((Essence) plugin));
        reg.command("trash", new CommandTrash((Essence) plugin));
    }

    @Override
    public void registerTabCompleters() {}

    @Override
    public void registerEvents() {}
}