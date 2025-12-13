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

    private final UtilCommand cmd;

    /**
     * Constructor for the module.
     * @param plugin    JavaPlugin - Reference to the main Essence class.
     * @param reg       Registry - Reference to the Foundry Registry.
     */
    public ModuleGamemode(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
        this.cmd = new UtilCommand((Essence) this.plugin);
    }

    /**
     * Registers Commands
     */
    @Override
    public void registerCommands() {
        if (!this.cmd.isDisabled("gamemode")) { reg.runtimeCommand("gamemode", new CommandGamemode((Essence) plugin), "gm"); }
        if (!this.cmd.isDisabled("gma")) { reg.runtimeCommand("gma", new CommandGma((Essence) plugin)); }
        if (!this.cmd.isDisabled("gmc")) { reg.runtimeCommand("gmc", new CommandGmc((Essence) plugin)); }
        if (!this.cmd.isDisabled("gms")) { reg.runtimeCommand("gms", new CommandGms((Essence) plugin)); }
        if (!this.cmd.isDisabled("gmsp")) { reg.runtimeCommand("gmsp", new CommandGmsp((Essence) plugin)); }
    }

    /**
     * Registers Tab Completers
     */
    @Override
    public void registerTabCompleters() {
        if (!cmd.isDisabled("gamemode")) { reg.tabCompleter("gamemode", new TabCompleterGamemode()); }
    }

    /**
     * Registers Events
     */
    @Override
    public void registerEvents() {}
}