package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetspawnCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the SetspawnCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public SetspawnCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
    }

    /**
     * @param commandSender Information about who sent the command - player or console.
     * @param command       Information about what command was sent.
     * @param s             Command label - not used here.
     * @param args          The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
        @NotNull CommandSender commandSender,
        @NotNull Command command,
        @NotNull String s,
        String[] args
    ) {
        if (!(commandSender instanceof Player)) {
            this.log.noConsole();
            return true;
        }
        MessageUtil message = new MessageUtil(commandSender, this.plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("setspawn")) {
            if (permission.has("essence.spawn.set")) {
                Location loc = player.getLocation();
                DataUtil config = new DataUtil(this.plugin, message);

                String spawnName = loc.getWorld().getName();

                config.load("data/spawns.yml");

                if (!config.sectionExists("spawn." + spawnName)) {
                    config.createSection("spawn." + spawnName);
                }

                ConfigurationSection cs = config.getSection("spawn."+spawnName);
                cs.set("X", loc.getX());
                cs.set("Y", loc.getY());
                cs.set("Z", loc.getZ());
                cs.set("yaw", loc.getYaw());
                cs.set("pitch", loc.getPitch());

                // Save the configuration to the file
                config.save();

                message.PrivateMessage("spawn", "set");
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}