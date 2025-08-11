package net.lewmc.essence.admin;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The Admin module.
 */
public class ModuleAdmin extends FoundryModule {

    /**
     * Constructor for the module.
     * @param plugin    JavaPlugin - Reference to the main Essence class.
     * @param reg       Registry - Reference to the Foundry Registry.
     */
    public ModuleAdmin(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    /**
     * Registers Commands
     */
    @Override
    public void registerCommands() {
        UtilCommand cmd = new UtilCommand((Essence) this.plugin, null);
        if (!cmd.isDisabled("info")) { reg.runtimeCommand("info", new CommandInfo((Essence) plugin)); }
        if (!cmd.isDisabled("invisible")) { reg.runtimeCommand("invisible", new CommandInvisible((Essence) plugin)); }
        if (!cmd.isDisabled("seen")) { reg.runtimeCommand("seen", new CommandSeen((Essence) plugin)); }
    }

    /**
     * Registers Tab Completers
     */
    @Override
    public void registerTabCompleters() {

    }

    /**
     * Registers Events
     */
    @Override
    public void registerEvents() {

    }
}