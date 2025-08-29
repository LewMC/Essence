package net.lewmc.essence.kit;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The Kit Module
 */
public class ModuleKit extends FoundryModule {

    /**
     * Constructor for the module.
     * @param plugin    JavaPlugin - Reference to the main Essence class.
     * @param reg       Registry - Reference to the Foundry Registry.
     */
    public ModuleKit(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    /**
     * Registers Commands
     */
    @Override
    public void registerCommands() {
        UtilCommand cmd = new UtilCommand((Essence) this.plugin);
        if (!cmd.isDisabled("kit")) { reg.runtimeCommand("kit", new CommandKit((Essence) plugin), "kits"); }
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