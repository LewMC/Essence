package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            playerFile.create(playerFile.playerDataFile(player.getUniqueId()));

            LogUtil log = new LogUtil(this.plugin);

            if (this.plugin.verbose) {
                log.info("Player data exists.");
            }
            if (!playerFile.load(playerFile.playerDataFile(player.getUniqueId()))) {
                log.severe("Unable to load configuration file '" + playerFile.playerDataFile(player.getUniqueId()) + "'. Essence may be unable to teleport players to the correct spawn");
                return false;
            }

            if (playerFile.get("user.accepting-teleport-requests") == null) {
                playerFile.set("user.accepting-teleport-requests", plugin.getConfig().getDouble("teleportation.requests.default-enabled"));
            }

            if (playerFile.get("economy.balance") == null) {
                playerFile.set("economy.balance", plugin.getConfig().getDouble("economy.start-money"));
            }

            if (playerFile.get("economy-accepting-payment") == null) {
                playerFile.set("economy.accepting-payments", true);
            }

            playerFile.set("user.last-known-name", player.getName());

            return true;
        } else {
            return false;
        }
    }
}
