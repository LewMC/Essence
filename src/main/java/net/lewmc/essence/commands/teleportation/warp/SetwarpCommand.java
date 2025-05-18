package net.lewmc.essence.commands.teleportation.warp;

import net.lewmc.essence.utils.*;
import net.lewmc.essence.Essence;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetwarpCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the SetwarpCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public SetwarpCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
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
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("setwarp")) { return cmd.disabled(); }

            if (!(cs instanceof Player p)) { return this.log.noConsole(); }

            PermissionHandler permission = new PermissionHandler(this.plugin, cs);

            if (permission.has("essence.warp.create")) {
                MessageUtil msg = new MessageUtil(this.plugin, cs);

                if (args.length == 0) {
                    msg.send("warp", "setusage");
                    return true;
                }
                Location loc = p.getLocation();
                FileUtil warpsData = new FileUtil(this.plugin);
                warpsData.load("data/warps.yml");

                String warpName = args[0].toLowerCase();

                SecurityUtil securityUtil = new SecurityUtil();
                if (securityUtil.hasSpecialCharacters(warpName)) {
                    warpsData.close();
                    msg.send("warp", "specialchars");
                    return true;
                }

                WarpUtil wu = new WarpUtil(this.plugin);
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