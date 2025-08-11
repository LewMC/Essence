package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The Core Module
 */
public class ModuleCore extends FoundryModule {

    /**
     * Constructor for the module.
     * @param plugin    JavaPlugin - Reference to the main Essence class.
     * @param reg       Registry - Reference to the Foundry Registry.
     */
    public ModuleCore(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    /**
     * Registers Commands
     */
    @Override
    public void registerCommands() {
        UtilCommand cmd = new UtilCommand((Essence) this.plugin, null);
        if (!cmd.isDisabled("essence")) { reg.runtimeCommand("essence", new CommandEssence((Essence) plugin), "es", "ess"); }
        if (!cmd.isDisabled("rules")) { reg.runtimeCommand("rules", new CommandRules((Essence) plugin)); }
    }

    /**
     * Registers Tab Completers
     */
    @Override
    public void registerTabCompleters() {
        reg.tabCompleter("essence", new TabCompleterEssence());
    }

    /**
     * Registers Events
     */
    @Override
    public void registerEvents() {
        reg.event(new EventJoin((Essence) plugin));
        reg.event(new EventDeath((Essence) plugin));
        reg.event(new EventPlayerDamage((Essence) plugin));
        reg.event(new EventRespawn((Essence) plugin));
        reg.event(new EventPlayerBedEnter((Essence) plugin));
        reg.event(new EventLeave((Essence) plugin));
        reg.event(new EventPlayerMove((Essence) plugin));
        reg.event(new EventPlayerChat((Essence) plugin));
    }
}