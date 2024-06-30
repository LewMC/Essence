package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Essence's player utility.
 */
public class PlayerUtil {
    private final Essence plugin;
    private final CommandSender commandSender;

    /**
     * The Player utility.
     * @param plugin Reference to the main Essence class.
     * @param cs CommandSender - The user who sent the command.
     */
    public PlayerUtil(Essence plugin, CommandSender cs) {
        this.plugin = plugin;
        this.commandSender = cs;
    }

    /**
     * Sets the player's gamemode.
     * @param cs CommandSender - The executor of the command.
     * @param player Player - The target player (may be self).
     * @param gamemode GameMode - The gamemode to set the player to.
     * @return boolean - Success
     */
    public boolean setGamemode(CommandSender cs, Player player, GameMode gamemode) {
        PermissionHandler permission = new PermissionHandler(cs, new MessageUtil(this.commandSender, this.plugin));
        MessageUtil message = new MessageUtil(this.commandSender, this.plugin);
        if (permission.has("essence.gamemode."+gamemode.toString().toLowerCase())) {
            if (cs == player) {
                message.PrivateMessage("gamemode", "done", gamemode.toString().toLowerCase());
                player.setGameMode(gamemode);
                return true;
            } else {
                if (permission.has("essence.gamemode.other")) {
                    message.PrivateMessage("gamemode", "doneother", player.getName(), gamemode.toString().toLowerCase());
                    message.SendTo(player, "gamemode", "doneby", gamemode.toString().toLowerCase(), cs.getName());
                    player.setGameMode(gamemode);
                    return true;
                } else {
                    return permission.not();
                }
            }
        } else {
            return permission.not();
        }
    }

    /**
     * Creates a player data file for the given player.
     * @return boolean - If the operation was successful.
     */
    public boolean createPlayerData() {
        FileUtil playerFile = new FileUtil(this.plugin);
        Player player = (Player) this.commandSender;

        if (!playerFile.exists(playerFile.playerDataFile(player.getUniqueId()))) {
            return this.updatePlayerData(
                    plugin.getConfig().getBoolean("teleportation.requests.default-enabled"),
                    plugin.getConfig().getDouble("economy.start-money"),
                    true
            );
        } else {
            return false;
        }
    }

    /**
     * Updates a player data file for the given player.
     * @return boolean - If the operation was successful.
     */
    public boolean updatePlayerData(
            boolean acceptingTeleportRequests,
            double balance,
            boolean acceptingPayments
    ) {
        FileUtil playerFile = new FileUtil(this.plugin);
        Player player = (Player) this.commandSender;

        LogUtil log = new LogUtil(this.plugin);

        if (!playerFile.exists(playerFile.playerDataFile(player.getUniqueId()))) {
            playerFile.create(playerFile.playerDataFile(player.getUniqueId()));
            if (this.plugin.verbose) {
                log.info("Player data does not exist, creating...");
            }
        } else {
            if (this.plugin.verbose) {
                log.info("Player data exists.");
            }
        }

        if (!playerFile.load(playerFile.playerDataFile(player.getUniqueId()))) {
            log.severe("Unable to load configuration file '" + playerFile.playerDataFile(player.getUniqueId()) + "'. Essence may be unable to teleport players to the correct spawn");
            return false;
        }

        if (playerFile.get("user.accepting-teleport-requests") == null) {
            playerFile.set("user.accepting-teleport-requests", acceptingTeleportRequests);
        }

        if (playerFile.get("economy.balance") == null) {
            playerFile.set("economy.balance", balance);
        }

        if (playerFile.get("economy-accepting-payment") == null) {
            playerFile.set("economy.accepting-payments", acceptingPayments);
        }

        playerFile.set("user.last-seen", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        playerFile.set("user.last-known-name", player.getName());

        playerFile.save();
        return true;
    }

    /**
     * Updates a player data file for the given player.
     * @return boolean - If the operation was successful.
     */
    public boolean updatePlayerData() {
        FileUtil playerFile = new FileUtil(this.plugin);
        Player player = (Player) this.commandSender;

        LogUtil log = new LogUtil(this.plugin);

        if (!playerFile.exists(playerFile.playerDataFile(player.getUniqueId()))) {
            playerFile.create(playerFile.playerDataFile(player.getUniqueId()));
            if (this.plugin.verbose) {
                log.info("Player data does not exist, creating...");
            }
        } else {
            if (this.plugin.verbose) {
                log.info("Player data exists.");
            }
        }

        if (!playerFile.load(playerFile.playerDataFile(player.getUniqueId()))) {
            log.severe("Unable to load configuration file '" + playerFile.playerDataFile(player.getUniqueId()) + "'. Essence may be unable to teleport players to the correct spawn");
            return false;
        }

        playerFile.set("user.last-seen", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        playerFile.set("user.last-known-name", player.getName());

        playerFile.save();
        return true;
    }
}
