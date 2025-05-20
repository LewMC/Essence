package net.lewmc.essence.gamemode;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The Gamemode Module
 */
public class ModuleGamemode extends FoundryModule {

    /**
     * Constructor for the module.
     * @param plugin    JavaPlugin - Reference to the main Essence class.
     * @param reg       Registry - Reference to the Foundry Registry.
     */
    public ModuleGamemode(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    /**
     * Registers Commands
     */
    @Override
    public void registerCommands() {
        reg.command(new String[] {"gamemode", "gmc", "gms", "gma", "gmsp"}, new CommandGamemode((Essence) plugin));
    }

    /**
     * Registers Tab Completers
     */
    @Override
    public void registerTabCompleters() {
        reg.tabCompleter(new String[] { "gamemode", "gm" }, new TabCompleterGamemode());
    }

    /**
     * Registers Events
     */
    @Override
    public void registerEvents() {}
}