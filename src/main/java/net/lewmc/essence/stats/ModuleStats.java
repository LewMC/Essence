package net.lewmc.essence.stats;

import net.lewmc.essence.Essence;
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
        reg.command("feed", new CommandFeed((Essence) plugin));
        reg.command("fly", new CommandFly((Essence) plugin));
        reg.command("heal", new CommandHeal((Essence) plugin));
        reg.command("repair", new CommandRepair((Essence) plugin));
        reg.command("speed", new CommandSpeed((Essence) plugin));
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