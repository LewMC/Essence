package net.lewmc.essence.gamemode;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
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
        UtilCommand cmd = new UtilCommand((Essence) this.plugin, null);
        if (!cmd.isDisabled("gamemode")) { reg.runtimeCommand("gamemode", new CommandGamemode((Essence) plugin), "gm"); }
        if (!cmd.isDisabled("gma")) { reg.runtimeCommand("gma", new CommandGma((Essence) plugin)); }
        if (!cmd.isDisabled("gmc")) { reg.runtimeCommand("gmc", new CommandGmc((Essence) plugin)); }
        if (!cmd.isDisabled("gms")) { reg.runtimeCommand("gms", new CommandGms((Essence) plugin)); }
        if (!cmd.isDisabled("gmsp")) { reg.runtimeCommand("gmsp", new CommandGmsp((Essence) plugin)); }
    }

    /**
     * Registers Tab Completers
     */
    @Override
    public void registerTabCompleters() {
        reg.tabCompleter("gamemode", new TabCompleterGamemode());
    }

    /**
     * Registers Events
     */
    @Override
    public void registerEvents() {}
}