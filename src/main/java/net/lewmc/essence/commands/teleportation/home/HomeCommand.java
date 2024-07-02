package net.lewmc.essence.commands.teleportation.home;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
                    message.send("teleport", "tryagain", new String[] { String.valueOf(teleUtil.cooldownRemaining(player, "home")) });
                    return true;
                }

                FileUtil playerData = new FileUtil(this.plugin);
                playerData.load(playerData.playerDataFile(player));

                String homeName;
                String chatHomeName;

                if (args.length == 1) {
                    homeName = "homes." + args[0].toLowerCase();
                    chatHomeName = args[0].toLowerCase();
                    if (playerData.get(homeName) == null) {
                        playerData.close();
                        message.send("home", "notfound", new String[] { args[0].toLowerCase() });
                        return true;
                    }
                } else {
                    homeName = "homes.home";
                    chatHomeName = "home";
                    if (playerData.get(homeName) == null) {
                        if (permission.has("essence.home.list")) {
                            playerData.close();

                            HomeUtil hu = new HomeUtil(this.plugin);
                            StringBuilder setHomes = hu.getHomesList(player);

                            if (setHomes == null) {
                                message.send("home", "noneset");
                                return true;
                            }

                            message.send("home", "list", new String[] { setHomes.toString() });
                            return true;
                        } else {
                            message.send("home", "notfound", new String[] { "home" });
                        }
                    }
                }

                if (playerData.get(homeName) == null) {
                    playerData.close();
                    message.send("generic", "exception");
                    this.log.warn("Player " + player + " attempted to teleport home to " + chatHomeName + " but couldn't due to an error.");
                    this.log.warn("Error: Unable to load from configuration file, please check configuration file.");
                    return true;
                }

                if (playerData.getString(homeName + ".world") == null) {
                    playerData.close();
                    message.send("generic", "exception");
                    this.log.warn("Player " + player + " attempted to teleport home to " + chatHomeName + " but couldn't due to an error.");
                    this.log.warn("Error: world is null, please check configuration file.");
                    return true;
                }

                LocationUtil locationUtil = new LocationUtil(this.plugin);
                locationUtil.UpdateLastLocation(player);

                teleUtil.setCooldown(player, "home");

                if (Bukkit.getServer().getWorld(playerData.getString(homeName + ".world")) == null) {
                    WorldCreator creator = new WorldCreator(playerData.getString(homeName + ".world"));
                    creator.createWorld();
                }

                teleUtil.doTeleport(
                        player,
                        Bukkit.getServer().getWorld(Objects.requireNonNull(playerData.getString(homeName + ".world"))),
                        playerData.getDouble(homeName + ".X"),
                        playerData.getDouble(homeName + ".Y"),
                        playerData.getDouble(homeName + ".Z"),
                        (float) playerData.getDouble(homeName + ".yaw"),
                        (float) playerData.getDouble(homeName + ".pitch"),
                        waitTime
                );
                playerData.close();

                message.send("home", "teleporting", new String[] { chatHomeName, waitTime + "" });
                return true;
            } else {
                return permission.not();
            }
        }
        return false;
    }
}