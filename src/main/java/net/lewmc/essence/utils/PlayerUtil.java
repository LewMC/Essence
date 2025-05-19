package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.Logger;
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
    private final CommandSender cs;

    /**
     * The Player utility.
     * @param plugin Reference to the main Essence class.
     * @param cs CommandSender - The user who sent the command.
     */
    public PlayerUtil(Essence plugin, CommandSender cs) {
        this.plugin = plugin;
        this.cs = cs;
    }

    /**
     * Sets the player's gamemode.
     * @param cs CommandSender - The executor of the command.
     * @param player Player - The target player (might be self).
     * @param gamemode GameMode - The gamemode to set the player to.
     * @return boolean - Success
     */
    public boolean setGamemode(CommandSender cs, Player player, GameMode gamemode) {
        PermissionHandler permission = new PermissionHandler(this.plugin, cs);
        MessageUtil message = new MessageUtil(this.plugin, cs);
        if (permission.has("essence.gamemode."+gamemode.toString().toLowerCase())) {
            if (cs == player) {
                message.send("gamemode", "done", new String[] { gamemode.toString().toLowerCase() });
                player.setGameMode(gamemode);
                return true;
            } else {
                if (permission.has("essence.gamemode.other")) {
                    message.send("gamemode", "doneother", new String[] { player.getName(), gamemode.toString().toLowerCase() });
                    message.sendTo(player, "gamemode", "doneby", new String[] { gamemode.toString().toLowerCase(), cs.getName() });
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
        Player player = (Player) this.cs;

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
        Player player = (Player) this.cs;

        Logger log = new Logger(this.plugin.config);

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
        Player player = (Player) this.cs;

        Logger log = new Logger(this.plugin.config);

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

    /**
     * Fetches a player's prefix.
     * @return String - The player's prefix (might be blank).
     */
    public String getPlayerPrefix() {
        if (this.plugin.chat != null) {
            if (this.cs instanceof Player p) {
                String prefix = this.plugin.chat.getPlayerPrefix(p);
                if (prefix != null && !prefix.isEmpty()) {
                    return "[" +  prefix + "]";
                } else {
                    return "";
                }
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    /**
     * Fetches a player's suffix.
     * @return String - The player's suffix (might be blank).
     */
    public String getPlayerSuffix() {
        if (this.plugin.chat != null) {
            if (this.cs instanceof Player p) {
                String suffix = this.plugin.chat.getPlayerSuffix(p);
                if (suffix != null && !suffix.isEmpty()) {
                    return " " +  suffix;
                } else {
                    return "";
                }
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    /**
     * Gets a player's display name.
     * @param p Player - The player to check.
     * @return The display name.
     */
    public String getPlayerDisplayname(Player p) {
        FileUtil pf = new FileUtil(this.plugin);
        pf.load(pf.playerDataFile(p));
        String nickname = pf.getString("user.nickname");
        pf.close();

        if (nickname == null) {
            return p.getName();
        } else {
            return nickname;
        }
    }

    /**
     * Sets a player's display name.
     * @param p Player - The player.
     * @param nickname String - The nickname
     * @return true/false success.
     */
    public boolean setPlayerDisplayname(Player p, String nickname) {
        FileUtil pf = new FileUtil(this.plugin);
        pf.load(pf.playerDataFile(p));

        boolean success = pf.set("user.nickname", nickname);
        pf.save();
        pf.close();

        return success;
    }

    /**
     * Sets a player's display name.
     * @param p Player - The player.
     * @return true/false success.
     */
    public boolean removePlayerDisplayname(Player p) {
        FileUtil pf = new FileUtil(this.plugin);
        pf.load(pf.playerDataFile(p));

        boolean success = pf.delete("user.nickname");
        pf.save();
        pf.close();

        return success;
    }
}
