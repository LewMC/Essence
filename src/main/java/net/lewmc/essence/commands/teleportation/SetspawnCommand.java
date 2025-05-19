package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import net.lewmc.foundry.Logger;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetspawnCommand implements CommandExecutor {
    private final Essence plugin;
    private final Logger log;

    /**
     * Constructor for the SetspawnCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public SetspawnCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new Logger(plugin.config);
    }

    /**
     * @param cs Information about who sent the command - player or console.
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
        if (command.getName().equalsIgnoreCase("setspawn")) {
            if (!(cs instanceof Player p)) { return this.log.noConsole(); }

            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("setspawn")) { return cmd.disabled(); }

            PermissionHandler perms = new PermissionHandler(this.plugin, cs);
            if (perms.has("essence.spawn.set")) {
                Location loc = p.getLocation();
                FileUtil spawnFile = new FileUtil(this.plugin);

                String spawnName = loc.getWorld().getName();

                spawnFile.load("data/spawns.yml");

                spawnFile.set("spawn."+spawnName+".X", loc.getX());
                spawnFile.set("spawn."+spawnName+".Y", loc.getY());
                spawnFile.set("spawn."+spawnName+".Z", loc.getZ());
                spawnFile.set("spawn."+spawnName+".yaw", loc.getYaw());
                spawnFile.set("spawn."+spawnName+".pitch", loc.getPitch());

                // Save the configuration to the file
                spawnFile.save();
                spawnFile.close();

                new MessageUtil(this.plugin, cs).send("spawn", "set");
            } else {
                return perms.not();
            }
            return true;
        }

        return false;
    }
}