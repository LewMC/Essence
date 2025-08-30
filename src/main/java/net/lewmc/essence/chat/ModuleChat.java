package net.lewmc.essence.chat;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
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
        UtilCommand cmd = new UtilCommand((Essence) this.plugin);
        if (!cmd.isDisabled("broadcast")) { reg.runtimeCommand("broadcast", new CommandBroadcast((Essence) plugin), "shout"); }
        if (!cmd.isDisabled("msg")) { reg.runtimeCommand("msg", new CommandMsg((Essence) plugin), "message","pm","tell","w"); }
        if (!cmd.isDisabled("nick")) { reg.runtimeCommand("nick", new CommandNick((Essence) plugin), "nickname", "displayname"); }
        if (!cmd.isDisabled("reply")) { reg.runtimeCommand("reply", new CommandReply((Essence) plugin), "r"); }
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
        reg.event(new EventPlayerChat((Essence) plugin));
    }
}