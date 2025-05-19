package net.lewmc.essence.module.teleportation.warp;

import net.lewmc.essence.global.UtilCommand;
import net.lewmc.essence.global.UtilMessage;
import net.lewmc.essence.global.UtilPermission;
import net.lewmc.essence.Essence;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import net.lewmc.foundry.Security;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandSetwarp implements CommandExecutor {
    private final Essence plugin;
    private final Logger log;

    /**
     * Constructor for the SetwarpCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandSetwarp(Essence plugin) {
        this.plugin = plugin;
        this.log = new Logger(plugin.config);
    }

    /**
     * @param cs            Information about who sent the command - player or console.
     * @param command       Information about what command was sent.
     * @param s             Command label - not used here.
     * @param args          The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
        @NotNull CommandSender cs,
        @NotNull Command command,
        @NotNull String s,
        String[] args
    ) {
        if (command.getName().equalsIgnoreCase("setwarp")) {
            UtilCommand cmd = new UtilCommand(this.plugin, cs);
            if (cmd.isDisabled("setwarp")) { return cmd.disabled(); }

            if (!(cs instanceof Player p)) { return this.log.noConsole(); }

            UtilPermission permission = new UtilPermission(this.plugin, cs);

            if (permission.has("essence.warp.create")) {
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
                int warpLimit = permission.getWarpsLimit(p);
                if (wu.getWarpCount(p) >= warpLimit && warpLimit != -1) {
                    msg.send("warp", "hitlimit");
                    return true;
                }

                if (wu.create(warpName, p.getUniqueId(), loc)) {
                    msg.send("warp", "created", new String[] { args[0] });
                } else {
                    msg.send("warp", "cantcreate", new String[] { args[0] });
                }

                warpsData.close();
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}