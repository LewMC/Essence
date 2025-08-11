package net.lewmc.essence.environment;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The Environment Module
 */
public class ModuleEnvironment extends FoundryModule {

    /**
     * Constructor for the module.
     * @param plugin    JavaPlugin - Reference to the main Essence class.
     * @param reg       Registry - Reference to the Foundry Registry.
     */
    public ModuleEnvironment(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    /**
     * Registers Commands
     */
    @Override
    public void registerCommands() {
        UtilCommand cmd = new UtilCommand((Essence) this.plugin, null);
        if (!cmd.isDisabled("time")) { reg.runtimeCommand("time", new CommandTime((Essence) plugin)); }
        if (!cmd.isDisabled("weather")) { reg.runtimeCommand("weather", new CommandWeather((Essence) plugin)); }
        if (!cmd.isDisabled("ptime")) { reg.runtimeCommand("ptime", new CommandPTime((Essence) plugin)); }
        if (!cmd.isDisabled("pweather")) { reg.runtimeCommand("pweather", new CommandPWeather((Essence) plugin)); }
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