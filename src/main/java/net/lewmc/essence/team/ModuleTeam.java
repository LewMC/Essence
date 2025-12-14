package net.lewmc.essence.team;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The Team Module
 */
public class ModuleTeam extends FoundryModule {

    private UtilCommand cmd;

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
        this.cmd = new UtilCommand((Essence) this.plugin);
        if (!this.cmd.isDisabled("team")) { reg.runtimeCommand("team", new CommandTeam((Essence) plugin), "group"); }
        if (!this.cmd.isDisabled("thome")) { reg.runtimeCommand("thome", new CommandThome((Essence) plugin), "teamhome","ghome","grouphome"); }
        if (!this.cmd.isDisabled("thomes")) { reg.runtimeCommand("thomes", new CommandThomes((Essence) plugin), "teamhomes","ghomes","grouphomes"); }
        if (!this.cmd.isDisabled("setthome")) { reg.runtimeCommand("setthome", new CommandSetthome((Essence) plugin), "setteamhome","setghome","setgrouphome"); }
        if (!this.cmd.isDisabled("delthome")) { reg.runtimeCommand("delthome", new CommandDelthome((Essence) plugin), "delteamhome","delghome","delgrouphome"); }

    }

    /**
     * Registers Tab Completers
     */
    @Override
    public void registerTabCompleters() {
        if (!this.cmd.isDisabled("team")) { reg.tabCompleter("team", new TabCompleterTeam()); }
    }

    /**
     * Registers Events
     */
    @Override
    public void registerEvents() {}
}