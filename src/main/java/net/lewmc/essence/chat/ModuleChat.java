package net.lewmc.essence.chat;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ModuleChat extends FoundryModule {
    public ModuleChat(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    @Override
    public void registerCommands() {
        reg.command("broadcast", new CommandBroadcast((Essence) plugin));
        reg.command("msg", new CommandMsg((Essence) plugin));
        reg.command("nick", new CommandNick((Essence) plugin));
        reg.command("reply", new CommandReply((Essence) plugin));
    }

    @Override
    public void registerTabCompleters() {

    }

    @Override
    public void registerEvents() {

    }
}