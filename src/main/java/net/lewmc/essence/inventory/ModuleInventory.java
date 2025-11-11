package net.lewmc.essence.inventory;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
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
        UtilCommand cmd = new UtilCommand((Essence) this.plugin);
        if (!cmd.isDisabled("anvil")) { reg.runtimeCommand("anvil", new CommandAnvil()); }
        if (!cmd.isDisabled("cartography")) { reg.runtimeCommand("cartography", new CommandCartography()); }
        if (!cmd.isDisabled("craft")) { reg.runtimeCommand("craft", new CommandCraft(), "workbench"); }
        if (!cmd.isDisabled("enderchest")) { reg.runtimeCommand("enderchest", new CommandEnderchest(), "echest"); }
        if (!cmd.isDisabled("grindstone")) { reg.runtimeCommand("grindstone", new CommandGrindstone()); }
        if (!cmd.isDisabled("loom")) { reg.runtimeCommand("loom", new CommandLoom()); }
        if (!cmd.isDisabled("smithing")) { reg.runtimeCommand("smithing", new CommandSmithing()); }
        if (!cmd.isDisabled("stonecutter")) { reg.runtimeCommand("stonecutter", new CommandStonecutter()); }
        if (!cmd.isDisabled("trash")) { reg.runtimeCommand("trash", new CommandTrash(), "disposal", "garbage"); }
        if (!cmd.isDisabled("give")) { reg.runtimeCommand("give", new CommandGive((Essence) this.plugin), "item", "i"); }
        if (!cmd.isDisabled("skull")) { reg.runtimeCommand("skull", new CommandSkull((Essence) this.plugin), "head"); }
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