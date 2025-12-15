package net.lewmc.essence.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.teleportation.home.*;
import net.lewmc.essence.teleportation.orientation.*;
import net.lewmc.essence.teleportation.spawn.*;
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

    private UtilCommand cmd;

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
        this.cmd = new UtilCommand((Essence) this.plugin);
        if (!this.cmd.isDisabled("tp")) { reg.runtimeCommand("tp", new CommandTeleport((Essence) plugin), "teleport"); }
        if (!this.cmd.isDisabled("tpa")) { reg.runtimeCommand("tpa", new CommandTpa((Essence) plugin), "tprequest"); }
        if (!this.cmd.isDisabled("tpaccept")) { reg.runtimeCommand("tpaccept", new CommandTpaccept((Essence) plugin)); }
        if (!this.cmd.isDisabled("tpdeny")) { reg.runtimeCommand("tpdeny", new CommandTpdeny((Essence) plugin), "tpdecline"); }
        if (!this.cmd.isDisabled("tptoggle")) { reg.runtimeCommand("tptoggle", new CommandTptoggle((Essence) plugin), "toggletp"); }
        if (!this.cmd.isDisabled("tpahere")) { reg.runtimeCommand("tpahere", new CommandTpahere((Essence) plugin)); }
        if (!this.cmd.isDisabled("tpcancel")) { reg.runtimeCommand("tpcancel", new CommandTpcancel((Essence) plugin), "canceltp"); }
        if (!this.cmd.isDisabled("tprandom")) { reg.runtimeCommand("tprandom", new CommandTprandom((Essence) plugin), "tpr", "rtp", "randomtp"); }

        if (!this.cmd.isDisabled("home")) { reg.runtimeCommand("home", new CommandHome((Essence) plugin)); }
        if (!this.cmd.isDisabled("homes")) { reg.runtimeCommand("homes", new CommandHomes((Essence) plugin)); }
        if (!this.cmd.isDisabled("sethome")) { reg.runtimeCommand("sethome", new CommandSethome((Essence) plugin)); }
        if (!this.cmd.isDisabled("delhome")) { reg.runtimeCommand("delhome", new CommandDelhome((Essence) plugin)); }

        if (!this.cmd.isDisabled("warp")) { reg.runtimeCommand("warp", new CommandWarp((Essence) plugin)); }
        if (!this.cmd.isDisabled("warps")) { reg.runtimeCommand("warps", new CommandWarps((Essence) plugin)); }
        if (!this.cmd.isDisabled("setwarp")) { reg.runtimeCommand("setwarp", new CommandSetwarp((Essence) plugin)); }
        if (!this.cmd.isDisabled("delwarp")) { reg.runtimeCommand("delwarp", new CommandDelwarp((Essence) plugin)); }

        if (!this.cmd.isDisabled("spawn")) { reg.runtimeCommand("spawn", new CommandSpawn((Essence) plugin),"spawnpoint", "worldspawn"); }
        if (!this.cmd.isDisabled("setspawn")) { reg.runtimeCommand("setspawn", new CommandSetspawn((Essence) plugin), "spawnset", "setworldspawn"); }

        if (!this.cmd.isDisabled("back")) { reg.runtimeCommand("back", new CommandBack((Essence) plugin)); }
        if (!cmd.isDisabled("top")) { reg.runtimeCommand("top", new CommandTop((Essence) plugin)); }
        if (!cmd.isDisabled("bottom")) { reg.runtimeCommand("bottom", new CommandBottom((Essence) plugin)); }
        if (!cmd.isDisabled("ascend")) { reg.runtimeCommand("ascend", new CommandAscend((Essence) plugin), "asc"); }
        if (!cmd.isDisabled("descend")) { reg.runtimeCommand("descend", new CommandDescend((Essence) plugin), "desc"); }

        if (!cmd.isDisabled("direction")) { reg.runtimeCommand("direction", new CommandDirection((Essence) plugin), "compass"); }
        if (!cmd.isDisabled("near")) { reg.runtimeCommand("near", new CommandNear((Essence) plugin)); }
    }

    /**
     * Registers Tab Completers
     */
    @Override
    public void registerTabCompleters() {
        if (!this.cmd.isDisabled("warp") || !this.cmd.isDisabled("delwarp")) { reg.tabCompleter(new String[] { "warp", "delwarp" }, new TabCompleterWarp((Essence) plugin)); }
        if (!this.cmd.isDisabled("home") || !this.cmd.isDisabled("delhome")) { reg.tabCompleter(new String[] { "home", "delhome" }, new TabCompleterHome((Essence) plugin)); }
        if (!this.cmd.isDisabled("tp")) { reg.tabCompleter(new String[] { "tp" }, new TabCompleterTp()); }

        if (!this.cmd.isDisabled("spawn")) { reg.tabCompleter(new String[] { "spawn" }, new TabCompleterSpawn((Essence) this.plugin)); }
    }

    /**
     * Registers Events
     */
    @Override
    public void registerEvents() {}
}