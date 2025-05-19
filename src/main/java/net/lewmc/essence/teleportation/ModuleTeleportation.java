package net.lewmc.essence.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.teleportation.home.*;
import net.lewmc.essence.teleportation.spawn.CommandSetspawn;
import net.lewmc.essence.teleportation.spawn.CommandSpawn;
import net.lewmc.essence.teleportation.tp.*;
import net.lewmc.essence.teleportation.warp.*;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ModuleTeleportation extends FoundryModule {
    public ModuleTeleportation(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    @Override
    public void registerCommands() {
        reg.command("tp", new CommandTeleport((Essence) plugin));
        reg.command("tpa", new CommandTpa((Essence) plugin));
        reg.command("tpaccept", new CommandTpaccept((Essence) plugin));
        reg.command("tpdeny", new CommandTpdeny((Essence) plugin));
        reg.command("tptoggle", new CommandTptoggle((Essence) plugin));
        reg.command("tpahere", new CommandTpahere((Essence) plugin));
        reg.command("tpcancel", new CommandTpcancel((Essence) plugin));
        reg.command("tprandom", new CommandTprandom((Essence) plugin));

        reg.command("home", new CommandHome((Essence) plugin));
        reg.command("homes", new CommandHomes((Essence) plugin));
        reg.command("sethome", new CommandSethome((Essence) plugin));
        reg.command("delhome", new CommandDelhome((Essence) plugin));

        reg.command("warp", new CommandWarp((Essence) plugin));
        reg.command("warps", new CommandWarps((Essence) plugin));
        reg.command("setwarp", new CommandSetwarp((Essence) plugin));
        reg.command("delwarp", new CommandDelwarp((Essence) plugin));

        reg.command("spawn", new CommandSpawn((Essence) plugin));
        reg.command("setspawn", new CommandSetspawn((Essence) plugin));

        reg.command("back", new CommandBack((Essence) plugin));
    }

    @Override
    public void registerTabCompleters() {
        reg.tabCompleter(new String[] { "warp", "delwarp" }, new TabCompleterWarp((Essence) plugin));
        reg.tabCompleter(new String[] { "home", "delhome" }, new TabCompleterHome((Essence) plugin));
        reg.tabCompleter(new String[] { "tp" }, new TabCompleterTp());
    }

    @Override
    public void registerEvents() {}
}