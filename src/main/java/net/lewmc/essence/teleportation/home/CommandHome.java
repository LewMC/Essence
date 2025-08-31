package net.lewmc.essence.teleportation.home;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.teleportation.tp.UtilTeleport;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CommandHome extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the HomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandHome(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     *
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.home.use";
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
        UtilTeleport teleUtil = new UtilTeleport(this.plugin);
        UtilMessage msg = new UtilMessage(this.plugin, cs);

        int waitTime = (int) plugin.config.get("teleportation.home.wait");
        if (!teleUtil.cooldownSurpassed((Player) cs, "home")) {
            msg.send("teleport", "tryagain", new String[]{String.valueOf(teleUtil.cooldownRemaining((Player) cs, "home"))});
            return true;
        }

        Files playerData = new Files(this.plugin.foundryConfig, this.plugin);
        playerData.load(playerData.playerDataFile((Player) cs));

        String homeName;
        String chatHomeName;

        if (args.length == 1) {
            homeName = "homes." + args[0].toLowerCase();
            chatHomeName = args[0].toLowerCase();
            if (playerData.get(homeName) == null) {
                playerData.close();
                msg.send("home", "notfound", new String[]{args[0].toLowerCase()});
                return true;
            }
        } else {
            homeName = "homes.home";
            chatHomeName = "home";
            if (playerData.get(homeName) == null) {
                if (new UtilPermission(this.plugin, cs).has("essence.home.list")) {
                    playerData.close();

                    UtilHome hu = new UtilHome(this.plugin);
                    StringBuilder setHomes = hu.getHomesList((Player) cs);

                    if (setHomes == null) {
                        msg.send("home", "noneset");
                        return true;
                    }

                    msg.send("home", "list", new String[]{setHomes.toString()});
                    return true;
                } else {
                    msg.send("home", "notfound", new String[]{"home"});
                }
            }
        }

        if (playerData.get(homeName) == null) {
            playerData.close();
            msg.send("generic", "exception");
            Logger log = new Logger(this.plugin.foundryConfig);
            log.warn("Player " + cs + " attempted to teleport home to " + chatHomeName + " but couldn't due to an error.");
            log.warn("Error: Unable to load from configuration file, please check configuration file.");
            return true;
        }

        if (playerData.getString(homeName + ".world") == null) {
            playerData.close();
            msg.send("generic", "exception");
            Logger log = new Logger(this.plugin.foundryConfig);
            log.warn("Player " + cs + " attempted to teleport home to " + chatHomeName + " but couldn't due to an error.");
            log.warn("Error: world is null, please check configuration file.");
            return true;
        }

        teleUtil.setCooldown((Player) cs, "home");

        World world = Bukkit.getServer().getWorld(playerData.getString(homeName + ".world"));

        if (world == null) {
            world = new WorldCreator(playerData.getString(homeName + ".world")).createWorld();
        }

        if (waitTime > 0) {
            msg.send("warp", "teleportingin", new String[]{chatHomeName, waitTime + ""});
        } else {
            msg.send("warp", "teleportingnow", new String[]{chatHomeName});
        }

        teleUtil.doTeleport(
                (Player) cs,
                world,
                playerData.getDouble(homeName + ".X"),
                playerData.getDouble(homeName + ".Y"),
                playerData.getDouble(homeName + ".Z"),
                (float) playerData.getDouble(homeName + ".yaw"),
                (float) playerData.getDouble(homeName + ".pitch"),
                waitTime
        );
        playerData.close();

        return true;
    }
}
