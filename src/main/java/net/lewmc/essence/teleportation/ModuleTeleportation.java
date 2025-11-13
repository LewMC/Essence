package net.lewmc.essence.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.teleportation.home.*;
import net.lewmc.essence.teleportation.spawn.CommandSetspawn;
import net.lewmc.essence.teleportation.spawn.CommandSpawn;
import net.lewmc.essence.teleportation.tp.*;
import net.lewmc.essence.teleportation.warp.*;
import net.lewmc.foundry.FoundryModule;
import net.lewmc.foundry.Registry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The Teleportation Module
 */
public class ModuleTeleportation extends FoundryModule {

    /**
     * Constructor for the module.
     * @param plugin    JavaPlugin - Reference to the main Essence class.
     * @param reg       Registry - Reference to the Foundry Registry.
     */
    public ModuleTeleportation(@NotNull JavaPlugin plugin, @NotNull Registry reg) {
        super(plugin, reg);
    }

    /**
     * Registers Commands
     */
    @Override
    public void registerCommands() {
        UtilCommand cmd = new UtilCommand((Essence) this.plugin);
        if (!cmd.isDisabled("tp")) { reg.runtimeCommand("tp", new CommandTeleport((Essence) plugin), "teleport"); }
        if (!cmd.isDisabled("tpa")) { reg.runtimeCommand("tpa", new CommandTpa((Essence) plugin), "tprequest"); }
        if (!cmd.isDisabled("tpaccept")) { reg.runtimeCommand("tpaccept", new CommandTpaccept((Essence) plugin)); }
        if (!cmd.isDisabled("tpdeny")) { reg.runtimeCommand("tpdeny", new CommandTpdeny((Essence) plugin), "tpdecline"); }
        if (!cmd.isDisabled("tptoggle")) { reg.runtimeCommand("tptoggle", new CommandTptoggle((Essence) plugin), "toggletp"); }
        if (!cmd.isDisabled("tpahere")) { reg.runtimeCommand("tpahere", new CommandTpahere((Essence) plugin)); }
        if (!cmd.isDisabled("tpcancel")) { reg.runtimeCommand("tpcancel", new CommandTpcancel((Essence) plugin), "canceltp"); }
        if (!cmd.isDisabled("tprandom")) { reg.runtimeCommand("tprandom", new CommandTprandom((Essence) plugin), "tpr", "rtp", "randomtp"); }

        if (!cmd.isDisabled("home")) { reg.runtimeCommand("home", new CommandHome((Essence) plugin)); }
        if (!cmd.isDisabled("homes")) { reg.runtimeCommand("homes", new CommandHomes((Essence) plugin)); }
        if (!cmd.isDisabled("sethome")) { reg.runtimeCommand("sethome", new CommandSethome((Essence) plugin)); }
        if (!cmd.isDisabled("delhome")) { reg.runtimeCommand("delhome", new CommandDelhome((Essence) plugin)); }

        if (!cmd.isDisabled("warp")) { reg.runtimeCommand("warp", new CommandWarp((Essence) plugin)); }
        if (!cmd.isDisabled("warps")) { reg.runtimeCommand("warps", new CommandWarps((Essence) plugin)); }
        if (!cmd.isDisabled("setwarp")) { reg.runtimeCommand("setwarp", new CommandSetwarp((Essence) plugin)); }
        if (!cmd.isDisabled("delwarp")) { reg.runtimeCommand("delwarp", new CommandDelwarp((Essence) plugin)); }

        if (!cmd.isDisabled("spawn")) { reg.runtimeCommand("spawn", new CommandSpawn((Essence) plugin),"spawnpoint", "world", "worldspawn"); }
        if (!cmd.isDisabled("setspawn")) { reg.runtimeCommand("setspawn", new CommandSetspawn((Essence) plugin), "spawnset", "setworldspawn"); }

        if (!cmd.isDisabled("back")) { reg.runtimeCommand("back", new CommandBack((Essence) plugin)); }
        if (!cmd.isDisabled("top")) { reg.runtimeCommand("top", new CommandTop((Essence) plugin)); }
        if (!cmd.isDisabled("bottom")) { reg.runtimeCommand("bottom", new CommandBottom((Essence) plugin)); }
        if (!cmd.isDisabled("direction")) { reg.runtimeCommand("direction", new CommandDirection((Essence) plugin), "compass"); }
    }

    /**
     * Registers Tab Completers
     */
    @Override
    public void registerTabCompleters() {
        reg.tabCompleter(new String[] { "warp", "delwarp" }, new TabCompleterWarp((Essence) plugin));
        reg.tabCompleter(new String[] { "home", "delhome" }, new TabCompleterHome((Essence) plugin));
        reg.tabCompleter(new String[] { "tp" }, new TabCompleterTp());
    }

    /**
     * Registers Events
     */
    @Override
    public void registerEvents() {}
}