package net.lewmc.essence.team;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The Team Module
 */
public class ModuleTeam extends FoundryModule {

    /**
     * Constructor for the module.
     * @param plugin    JavaPlugin - Reference to the main Essence class.
     * @param reg       Registry - Reference to the Foundry Registry.
     */
    public ModuleTeam(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    /**
     * Registers Commands
     */
    @Override
    public void registerCommands() {
        reg.command("team", new CommandTeam((Essence) plugin));
        reg.command("thome", new CommandThome((Essence) plugin));
        reg.command("thomes", new CommandThomas((Essence) plugin));
        reg.command("setthome", new CommandSetthome((Essence) plugin));
        reg.command("delthome", new CommandDelthomes((Essence) plugin));
    }

    /**
     * Registers Tab Completers
     */
    @Override
    public void registerTabCompleters() {
        reg.tabCompleter("team", new TabCompleterTeam());
    }

    /**
     * Registers Events
     */
    @Override
    public void registerEvents() {}
}