package net.lewmc.essence.teleportation.warp;

import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.Essence;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Security;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetwarp extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the SetwarpCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandSetwarp(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Required permissions.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.warp.create";
    }

    /**
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean  true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("setwarp")) {
            return cmd.disabled();
        }

        Player p = (Player) cs;

        UtilMessage msg = new UtilMessage(this.plugin, cs);

        if (args.length == 0) {
            msg.send("warp", "setusage");
            return true;
        }
        Location loc = p.getLocation();
        Files warpsData = new Files(this.plugin.config, this.plugin);
        warpsData.load("data/warps.yml");

        String warpName = args[0].toLowerCase();

        if (new Security(this.plugin.config).hasSpecialCharacters(warpName)) {
            warpsData.close();
            msg.send("warp", "specialchars");
            return true;
        }

        UtilWarp wu = new UtilWarp(this.plugin);
        int warpLimit = new UtilPermission(this.plugin, cs).getWarpsLimit(p);
        if (wu.getWarpCount(p) >= warpLimit && warpLimit != -1) {
            msg.send("warp", "hitlimit");
            return true;
        }

        if (wu.create(warpName, p.getUniqueId(), loc)) {
            msg.send("warp", "created", new String[]{args[0]});
        } else {
            msg.send("warp", "cantcreate", new String[]{args[0]});
        }

        warpsData.close();
        return true;
    }
}