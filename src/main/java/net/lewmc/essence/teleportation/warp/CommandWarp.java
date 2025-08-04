package net.lewmc.essence.teleportation.warp;

import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.teleportation.tp.UtilTeleport;
import net.lewmc.essence.Essence;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CommandWarp extends FoundryPlayerCommand {
    private final Essence plugin;
    private final Logger log;

    /**
     * Constructor for the WarpCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandWarp(Essence plugin) {
        this.plugin = plugin;
        this.log = new Logger(plugin.config);
    }

    /**
     * Required permissions.
     *
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.warp.use";
    }

    /**
     * @param cs      Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s       Command label - not used here.
     * @param args    The command's arguments.
     * @return boolean  true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("warp")) { return cmd.disabled(); }

        Player p = (Player) cs;

        int waitTime = plugin.getConfig().getInt("teleportation.warp.wait");
        UtilMessage msg = new UtilMessage(this.plugin, cs);

        if (args.length > 0) {
            UtilTeleport teleUtil = new UtilTeleport(this.plugin);

            if (!teleUtil.cooldownSurpassed(p, "warp")) {
                msg.send("teleport", "tryagain", new String[]{String.valueOf(teleUtil.cooldownRemaining(p, "warp"))});
                return true;
            }

            Files config = new Files(this.plugin.config, this.plugin);
            config.load("data/warps.yml");

            if (config.get("warps." + args[0].toLowerCase()) == null) {
                msg.send("warp", "notfound", new String[]{args[0].toLowerCase()});
                return true;
            }

            if (config.getString("warps." + args[0].toLowerCase() + ".world") == null) {
                config.close();
                msg.send("generic", "exception");
                this.log.warn("Player " + p + " attempted to warp to " + args[0].toLowerCase() + " but couldn't due to an error.");
                this.log.warn("Error: world is null, please check configuration file.");
                return true;
            }

            teleUtil.setCooldown(p, "warp");

            if (Bukkit.getServer().getWorld(config.getString("warps." + args[0].toLowerCase() + ".world")) == null) {
                WorldCreator creator = new WorldCreator(config.getString("warps." + args[0].toLowerCase() + ".world"));
                creator.createWorld();
            }

            if (waitTime > 0) {
                msg.send("warp", "teleportingin", new String[]{args[0], waitTime + ""});
            } else {
                msg.send("warp", "teleportingnow", new String[]{args[0]});
            }

            teleUtil.doTeleport(
                    p,
                    Bukkit.getServer().getWorld(Objects.requireNonNull(config.getString("warps." + args[0].toLowerCase() + ".world"))),
                    config.getDouble("warps." + args[0].toLowerCase() + ".X"),
                    config.getDouble("warps." + args[0].toLowerCase() + ".Y"),
                    config.getDouble("warps." + args[0].toLowerCase() + ".Z"),
                    (float) config.getDouble("warps." + args[0].toLowerCase() + ".yaw"),
                    (float) config.getDouble("warps." + args[0].toLowerCase() + ".pitch"),
                    waitTime
            );

            config.close();

            return true;
        } else {
            msg.send("warp", "usage");
        }
        return true;
    }
}