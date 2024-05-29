package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HomeCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the HomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public HomeCommand(Essence plugin) {
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
        MessageUtil message = new MessageUtil(commandSender, plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);
        TeleportUtil teleUtil = new TeleportUtil(this.plugin);

        if (command.getName().equalsIgnoreCase("home")) {
            if (permission.has("essence.home.use")) {
                int waitTime = plugin.getConfig().getInt("teleportation.home.wait");
                if (!teleUtil.cooldownSurpassed(player, "home")) {
                    message.PrivateMessage("teleport", "tryagain", String.valueOf(teleUtil.cooldownRemaining(player, "home")));
                    return true;
                }

                DataUtil config = new DataUtil(this.plugin, message);
                config.load(config.playerDataFile(player));

                HomeUtil homeUtil = new HomeUtil();

                String homeName;
                String chatHomeName;

                if (args.length == 1) {
                    homeName = homeUtil.HomeWrapper(args[0].toLowerCase());
                    chatHomeName = args[0].toLowerCase();
                    if (config.getSection(homeName) == null) {
                        config.close();
                        message.PrivateMessage("home", "notfound", args[0].toLowerCase());
                        return true;
                    }
                } else {
                    homeName = homeUtil.HomeWrapper("home");
                    chatHomeName = "home";
                    if (config.getSection(homeName) == null) {
                        config.close();
                        message.PrivateMessage("home", "noneset");
                        return true;
                    }
                }

                ConfigurationSection cs = config.getSection(homeName);

                if (cs == null) {
                    config.close();
                    message.PrivateMessage("generic", "exception");
                    this.log.warn("Player " + player + " attempted to teleport home to " + chatHomeName + " but couldn't due to an error.");
                    this.log.warn("Error: Unable to load from configuration file, please check configuration file.");
                    return true;
                }

                if (cs.getString("world") == null) {
                    config.close();
                    message.PrivateMessage("generic", "exception");
                    this.log.warn("Player " + player + " attempted to teleport home to " + chatHomeName + " but couldn't due to an error.");
                    this.log.warn("Error: world is null, please check configuration file.");
                    return true;
                }

                LocationUtil locationUtil = new LocationUtil(this.plugin, message);
                locationUtil.UpdateLastLocation(player);

                if (waitTime > 0) {
                    message.PrivateMessage("teleport", "wait", String.valueOf(waitTime));
                }

                teleUtil.setCooldown(player, "home");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        TeleportUtil tp = new TeleportUtil(plugin);
                        tp.doTeleport(
                                player,
                                Bukkit.getServer().getWorld(Objects.requireNonNull(cs.getString("world"))),
                                cs.getDouble("X"),
                                cs.getDouble("Y"),
                                cs.getDouble("Z"),
                                (float) cs.getDouble("yaw"),
                                (float) cs.getDouble("pitch")
                        );
                        config.close();

                        message.PrivateMessage("home", "teleporting", chatHomeName);
                    }
                }.runTaskLater(plugin, waitTime * 20L);
            }
        } else {
            permission.not();
        }
        return true;
    }
}