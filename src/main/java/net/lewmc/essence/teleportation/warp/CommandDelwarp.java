package net.lewmc.essence.teleportation.warp;

import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.Essence;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDelwarp extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the DelwarpCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandDelwarp(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Required permissions.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.warp.delete";
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
        Player p = (Player) cs;
        UtilMessage msg = new UtilMessage(this.plugin, cs);

        if (args.length == 0) {
            msg.send("warp", "delusage");
            return true;
        }
        Files config = new Files(this.plugin.foundryConfig, this.plugin);
        config.load("data/warps.yml");

        String warpName = args[0].toLowerCase();

        if (config.get("warps."+warpName) == null) {
            config.close();
            msg.send("warp", "notfound", new String[] { warpName });
            return true;
        }

        if (!config.get("warps."+warpName+".creator").equals(p.getUniqueId().toString())
                && !p.hasPermission("essence.warp.delete.other")) {
            msg.send("warp", "nopermission", new String[] { warpName } );
            config.close();
            return true;
        }

        if (config.remove("warps."+warpName)) {
            msg.send("warp", "deleted", new String[] { warpName });
        } else {
            msg.send("generic", "exception");
        }

        config.save();

        return true;
    }
}
