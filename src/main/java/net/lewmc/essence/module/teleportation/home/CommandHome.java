package net.lewmc.essence.module.teleportation.home;

import net.lewmc.essence.Essence;
import net.lewmc.essence.global.UtilCommand;
import net.lewmc.essence.global.UtilLocation;
import net.lewmc.essence.global.UtilMessage;
import net.lewmc.essence.global.UtilPermission;
import net.lewmc.essence.module.teleportation.tp.UtilTeleport;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CommandHome implements CommandExecutor {
    private final Essence plugin;
    private final Logger log;

    /**
     * Constructor for the HomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandHome(Essence plugin) {
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
        if (command.getName().equalsIgnoreCase("home")) {
            UtilCommand cmd = new UtilCommand(this.plugin, cs);
            if (cmd.isDisabled("home")) { return cmd.disabled(); }

            if (!(cs instanceof Player p)) { return this.log.noConsole(); }

            UtilPermission perms = new UtilPermission(this.plugin, cs);
            if (perms.has("essence.home.use")) {

                UtilTeleport teleUtil = new UtilTeleport(this.plugin);
                UtilMessage msg = new UtilMessage(this.plugin, cs);

                int waitTime = plugin.getConfig().getInt("teleportation.home.wait");
                if (!teleUtil.cooldownSurpassed(p, "home")) {
                    msg.send("teleport", "tryagain", new String[] { String.valueOf(teleUtil.cooldownRemaining(p, "home")) });
                    return true;
                }

                Files playerData = new Files(this.plugin.config, this.plugin);
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

                            UtilHome hu = new UtilHome(this.plugin);
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

                UtilLocation locationUtil = new UtilLocation(this.plugin);
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