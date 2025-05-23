package net.lewmc.essence.inventory;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Essence's Inventory module.
 */
public class ModuleInventory extends FoundryModule {

    /**
     * Constructor for the module.
     * @param plugin    JavaPlugin - Reference to the main Essence class.
     * @param reg       Registry - Reference to the Foundry Registry.
     */
    public ModuleInventory(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    /**
     * Registers commands.
     */
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

    /**
     * Registers Tab Completers
     */
    @Override
    public void registerTabCompleters() {}

    /**
     * Registers Events
     */
    @Override
    public void registerEvents() {}
}