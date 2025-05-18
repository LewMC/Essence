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
        if (command.getName().equalsIgnoreCase("home")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("home")) { return cmd.disabled(); }

            if (!(cs instanceof Player p)) { return this.log.noConsole(); }

            PermissionHandler perms = new PermissionHandler(this.plugin, cs);
            if (perms.has("essence.home.use")) {

                TeleportUtil teleUtil = new TeleportUtil(this.plugin);
                MessageUtil msg = new MessageUtil(this.plugin, cs);

                int waitTime = plugin.getConfig().getInt("teleportation.home.wait");
                if (!teleUtil.cooldownSurpassed(p, "home")) {
                    msg.send("teleport", "tryagain", new String[] { String.valueOf(teleUtil.cooldownRemaining(p, "home")) });
                    return true;
                }

                FileUtil playerData = new FileUtil(this.plugin);
                playerData.load(playerData.playerDataFile(p));

                String homeName;
                String chatHomeName;

                if (args.length == 1) {
                    homeName = "homes." + args[0].toLowerCase();
                    chatHomeName = args[0].toLowerCase();
                    if (playerData.get(homeName) == null) {
                        playerData.close();
                        msg.send("home", "notfound", new String[] { args[0].toLowerCase() });
                        return true;
                    }
                } else {
                    homeName = "homes.home";
                    chatHomeName = "home";
                    if (playerData.get(homeName) == null) {
                        if (perms.has("essence.home.list")) {
                            playerData.close();

                            HomeUtil hu = new HomeUtil(this.plugin);
                            StringBuilder setHomes = hu.getHomesList(p);

                            if (setHomes == null) {
                                msg.send("home", "noneset");
                                return true;
                            }

                            msg.send("home", "list", new String[] { setHomes.toString() });
                            return true;
                        } else {
                            msg.send("home", "notfound", new String[] { "home" });
                        }
                    }
                }

                if (playerData.get(homeName) == null) {
                    playerData.close();
                    msg.send("generic", "exception");
                    this.log.warn("Player " + p + " attempted to teleport home to " + chatHomeName + " but couldn't due to an error.");
                    this.log.warn("Error: Unable to load from configuration file, please check configuration file.");
                    return true;
                }

                if (playerData.getString(homeName + ".world") == null) {
                    playerData.close();
                    msg.send("generic", "exception");
                    this.log.warn("Player " + p + " attempted to teleport home to " + chatHomeName + " but couldn't due to an error.");
                    this.log.warn("Error: world is null, please check configuration file.");
                    return true;
                }

                LocationUtil locationUtil = new LocationUtil(this.plugin);
                locationUtil.UpdateLastLocation(p);

                teleUtil.setCooldown(p, "home");

                if (Bukkit.getServer().getWorld(playerData.getString(homeName + ".world")) == null) {
                    WorldCreator creator = new WorldCreator(playerData.getString(homeName + ".world"));
                    creator.createWorld();
                }

                msg.send("home", "teleporting", new String[] { chatHomeName, waitTime + "" });

                teleUtil.doTeleport(
                        p,
                        Bukkit.getServer().getWorld(Objects.requireNonNull(playerData.getString(homeName + ".world"))),
                        playerData.getDouble(homeName + ".X"),
                        playerData.getDouble(homeName + ".Y"),
                        playerData.getDouble(homeName + ".Z"),
                        (float) playerData.getDouble(homeName + ".yaw"),
                        (float) playerData.getDouble(homeName + ".pitch"),
                        waitTime
                );
                playerData.close();

                return true;
            } else {
                return perms.not();
            }
        }
        return false;
    }
}