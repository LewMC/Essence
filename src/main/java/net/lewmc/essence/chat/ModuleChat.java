package net.lewmc.essence.chat;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The Chat Module
 */
public class ModuleChat extends FoundryModule {

    /**
     * Constructor for the module.
     * @param plugin    JavaPlugin - Reference to the main Essence class.
     * @param reg       Registry - Reference to the Foundry Registry.
     */
    public ModuleChat(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    /**
     * Registers Commands
     */
    @Override
    public void registerCommands() {
        reg.command("broadcast", new CommandBroadcast((Essence) plugin));
        reg.command("msg", new CommandMsg((Essence) plugin));
        reg.command("nick", new CommandNick((Essence) plugin));
        reg.command("reply", new CommandReply((Essence) plugin));
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