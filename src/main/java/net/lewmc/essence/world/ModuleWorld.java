package net.lewmc.essence.world;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The World Module
 */
public class ModuleWorld extends FoundryModule {

    private final UtilCommand cmd;

    /**
     * Constructor for the module.
     * @param plugin    JavaPlugin - Reference to the main Essence class.
     * @param reg       Registry - Reference to the Foundry Registry.
     */
    public ModuleWorld(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
        this.cmd = new UtilCommand((Essence) this.plugin);
    }

    /**
     * Registers Commands
     */
    @Override
    public void registerCommands() {
        if (!this.cmd.isDisabled("world")) { reg.runtimeCommand("world", new CommandWorld((Essence) plugin), "worlds"); }
    }

    /**
     * Registers Tab Completers
     */
    @Override
    public void registerTabCompleters() {
        if (!this.cmd.isDisabled("world")) { reg.tabCompleter(new String[] { "world" }, new TabCompleterWorld((Essence) plugin)); }
    }

    /**
     * Registers Events
     */
    @Override
    public void registerEvents() {}
}