package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Essence's player utility.
 */
public class UtilPlayer {
    private final Essence plugin;
    private final CommandSender cs;

    /**
     * The Player utility.
     *
     * @param plugin Reference to the main Essence class.
     * @param cs     CommandSender - The user who sent the command.
     */
    public UtilPlayer(Essence plugin, CommandSender cs) {
        this.plugin = plugin;
        this.cs = cs;
    }

    /**
     * Sets the player's gamemode.
     *
     * @param cs       CommandSender - The executor of the command.
     * @param p        Player - The target player (might be self).
     * @param gamemode GameMode - The gamemode to set the player to.
     * @return boolean - Success
     */
    public boolean setGamemode(CommandSender cs, Player p, GameMode gamemode) {
        UtilPermission permission = new UtilPermission(this.plugin, cs);
        UtilMessage message = new UtilMessage(this.plugin, cs);
        if (permission.has("essence.gamemode." + gamemode.toString().toLowerCase())) {
            if (cs == p) {
                message.send("gamemode", "done", new String[]{gamemode.toString().toLowerCase()});
                p.setGameMode(gamemode);
                return true;
            } else {
                if (permission.has("essence.gamemode.other")) {
                    message.send("gamemode", "doneother", new String[]{p.getName(), gamemode.toString().toLowerCase()});
                    message.sendTo(p, "gamemode", "doneby", new String[]{gamemode.toString().toLowerCase(), cs.getName()});
                    p.setGameMode(gamemode);
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
     * Creates a player data file for the given p.
     *
     * @return boolean - If the operation was successful.
     */
    public boolean createPlayerData() {
        if (this.cs instanceof Player p) {
            Files playerFile = new Files(this.plugin.config, this.plugin);

            if (!playerFile.exists(playerFile.playerDataFile(p.getUniqueId()))) {
                return this.updatePlayerData(
                        plugin.getConfig().getBoolean("teleportation.requests.default-enabled"),
                        plugin.getConfig().getDouble("economy.start-money"),
                        true
                );
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Updates a player data file for the given p.
     *
     * @return boolean - If the operation was successful.
     */
    public boolean updatePlayerData(
            boolean acceptingTeleportRequests,
            double balance,
            boolean acceptingPayments
    ) {
        if (this.cs instanceof Player p) {
            Files playerFile = new Files(this.plugin.config, this.plugin);

            Logger log = new Logger(this.plugin.config);

            if (!playerFile.exists(playerFile.playerDataFile(p.getUniqueId()))) {
                playerFile.create(playerFile.playerDataFile(p.getUniqueId()));
                if (this.plugin.verbose) {
                    log.info("Player data does not exist, creating...");
                }
            } else {
                if (this.plugin.verbose) {
                    log.info("Player data exists.");
                }
            }

            if (!playerFile.load(playerFile.playerDataFile(p.getUniqueId()))) {
                log.severe("Unable to load configuration file '" + playerFile.playerDataFile(p.getUniqueId()) + "'. Essence may be unable to teleport players to the correct spawn");
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
            playerFile.set("user.last-known-name", p.getName());

            playerFile.save();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Updates a player data file for the given p.
     *
     * @return boolean - If the operation was successful.
     */
    public boolean updatePlayerData() {
        if (this.cs instanceof Player p) {
            Files playerFile = new Files(this.plugin.config, this.plugin);

            Logger log = new Logger(this.plugin.config);

            if (!playerFile.exists(playerFile.playerDataFile(p.getUniqueId()))) {
                playerFile.create(playerFile.playerDataFile(p.getUniqueId()));
                if (this.plugin.verbose) {
                    log.info("Player data does not exist, creating...");
                }
            } else {
                if (this.plugin.verbose) {
                    log.info("Player data exists.");
                }
            }

            if (!playerFile.load(playerFile.playerDataFile(p.getUniqueId()))) {
                log.severe("Unable to load configuration file '" + playerFile.playerDataFile(p.getUniqueId()) + "'. Essence may be unable to teleport players to the correct spawn");
                return false;
            }

            playerFile.set("user.last-seen", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            playerFile.set("user.last-known-name", p.getName());

            playerFile.save();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Fetches a player's prefix.
     *
     * @return String - The player's prefix (might be blank).
     */
    public String getPlayerPrefix() {
        if (this.plugin.integrations.chat != null) {
            if (this.cs instanceof Player p) {
                String prefix = this.plugin.integrations.chat.getPlayerPrefix(p);
                if (prefix != null && !prefix.isEmpty()) {
                    return "[" + prefix + "]";
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
     *
     * @return String - The player's suffix (might be blank).
     */
    public String getPlayerSuffix() {
        if (this.plugin.integrations.chat != null) {
            if (this.cs instanceof Player p) {
                String suffix = this.plugin.integrations.chat.getPlayerSuffix(p);
                if (suffix != null && !suffix.isEmpty()) {
                    return " " + suffix;
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
     *
     * @param cs CommandSender - The player to check.
     * @return The display name.
     */
    public String getPlayerDisplayname(CommandSender cs) {
        if (cs instanceof Player p) {
            Files pf = new Files(this.plugin.config, this.plugin);
            pf.load(pf.playerDataFile(p));
            String nickname = pf.getString("user.nickname");
            pf.close();

            return Objects.requireNonNullElseGet(nickname, p::getName);
        } else {
            return cs.getName();
        }
    }

    /**
     * Sets a player's display name.
     *
     * @param cs       CommandSender - The p.
     * @param nickname String - The nickname
     * @return true/false success.
     */
    public boolean setPlayerDisplayname(CommandSender cs, String nickname) {
        if (cs instanceof Player p) {
            Files pf = new Files(this.plugin.config, this.plugin);
            pf.load(pf.playerDataFile(p));

            boolean success = pf.set("user.nickname", nickname);
            pf.save();
            pf.close();

            return success;
        } else {
            return false;
        }
    }

    /**
     * Sets a player's display name.
     *
     * @param cs CommandSender - The p.
     * @return true/false success.
     */
    public boolean removePlayerDisplayname(CommandSender cs) {
        if (cs instanceof Player p) {
            Files pf = new Files(this.plugin.config, this.plugin);
            pf.load(pf.playerDataFile(p));

            boolean success = pf.delete("user.nickname");
            pf.save();
            pf.close();

            return success;
        } else {
            return false;
        }
    }
}