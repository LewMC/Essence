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
        reg.command("gamemode", new CommandGamemode((Essence) plugin));
        reg.command("gma", new CommandGma((Essence) plugin));
        reg.command("gmc", new CommandGmc((Essence) plugin));
        reg.command("gms", new CommandGms((Essence) plugin));
        reg.command("gmsp", new CommandGmsp((Essence) plugin));
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