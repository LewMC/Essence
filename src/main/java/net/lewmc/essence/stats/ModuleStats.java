package net.lewmc.essence.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The Stats Module
 */
public class ModuleStats extends FoundryModule {

    /**
     * Constructor for the module.
     * @param plugin    JavaPlugin - Reference to the main Essence class.
     * @param reg       Registry - Reference to the Foundry Registry.
     */
    public ModuleStats(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    /**
     * Registers Commands
     */
    @Override
    public void registerCommands() {
        UtilCommand cmd = new UtilCommand((Essence) this.plugin, null);
        if (!cmd.isDisabled("feed")) { reg.runtimeCommand("feed", new CommandFeed((Essence) plugin)); }
        if (!cmd.isDisabled("fly")) { reg.runtimeCommand("fly", new CommandFly((Essence) plugin)); }
        if (!cmd.isDisabled("heal")) { reg.runtimeCommand("heal", new CommandHeal((Essence) plugin)); }
        if (!cmd.isDisabled("repair")) { reg.runtimeCommand("repair", new CommandRepair((Essence) plugin), "fix"); }
        if (!cmd.isDisabled("speed")) { reg.runtimeCommand("speed", new CommandSpeed((Essence) plugin)); }
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