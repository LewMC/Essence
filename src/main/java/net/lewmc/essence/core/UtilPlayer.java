package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.Files;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Essence's player utility.
 */
public class UtilPlayer {
    private final Essence plugin;

    /**
     * The Player utility.
     *
     * @param plugin Reference to the main Essence class.
     * @since 1.11.0
     */
    public UtilPlayer(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads a player into memory.
     * @param p OfflinePlayer - The player.
     * @return boolean - Success?
     * @since 1.11.0
     */
    public boolean loadPlayer(OfflinePlayer p) {
        Files f = new Files(this.plugin.foundryConfig, this.plugin);

        if (f.exists(f.playerDataFile(p.getUniqueId()))) {
            TypePlayer player = new TypePlayer();

            Object atr = f.get(PLAYER_KEYS.USER_ACCEPTING_TELEPORT_REQUESTS.toString());
            player.user.acceptingTeleportRequests = (atr == null) ? true : (boolean) atr;

            player.user.lastSeen = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            player.user.lastKnownName = p.getName();
            Object n = f.get(PLAYER_KEYS.USER_NICKNAME.toString());
            player.user.nickname = (n == null) ? null : (String) n;

            Object t = f.get(PLAYER_KEYS.USER_TEAM.toString());
            player.user.team = (t == null) ? null : t.toString();

            if ((boolean) this.plugin.config.get("advanced.playerdata.store-ip-address")) {
                f.set(PLAYER_KEYS.USER_IP_ADDRESS.toString(), Objects.requireNonNull(p.getPlayer().getAddress()).getAddress().getHostAddress());
            }

            Object b = f.get(PLAYER_KEYS.ECONOMY_BALANCE.toString());
            player.economy.balance = (b == null) ? (double) this.plugin.config.get("economy.start-money") : (double) b;

            Object ap = f.get(PLAYER_KEYS.ECONOMY_ACCEPTING_PAYMENTS.toString());
            player.economy.acceptingPayments = (ap == null) ? true : (boolean) ap;

            player.lastLocation.world = f.get(PLAYER_KEYS.LAST_KNOWN_LOCATION_WORLD.toString()).toString();

            Object llx = f.get(PLAYER_KEYS.LAST_KNOWN_LOCATION_WORLD.toString());
            player.lastLocation.x = (llx == null) ? p.getPlayer().getLocation().getX() : (double) llx;

            Object lly = f.get(PLAYER_KEYS.LAST_KNOWN_LOCATION_WORLD.toString());
            player.lastLocation.y = (lly == null) ? p.getPlayer().getLocation().getX() : (double) lly;

            Object llz = f.get(PLAYER_KEYS.LAST_KNOWN_LOCATION_WORLD.toString());
            player.lastLocation.z = (llz == null) ? p.getPlayer().getLocation().getZ() : (double) llz;

            Object llyaw = f.get(PLAYER_KEYS.LAST_KNOWN_LOCATION_WORLD.toString());
            player.lastLocation.yaw = (llyaw == null) ? p.getPlayer().getLocation().getYaw() : (float) llyaw;

            Object llpitch = f.get(PLAYER_KEYS.LAST_KNOWN_LOCATION_WORLD.toString());
            player.lastLocation.pitch = (llpitch == null) ? p.getPlayer().getLocation().getPitch() : (float) llpitch;

            Object llib = f.get(PLAYER_KEYS.LAST_KNOWN_LOCATION_IS_BED.toString());
            player.lastLocation.isBed = (llib == null) ? false : (boolean) llib;

            this.plugin.players.put(
                p.getUniqueId(),
                player
            );
            return true;
        } else {
            return false;
        }
    }

    /**
     * Saves a player's data - WARNING: DOES NOT SAVE IT
     * @param p OfflinePlayer - The player
     * @return boolean - Success?
     * @since 1.11.0
     */
    public boolean savePlayer(OfflinePlayer p) {
        Files f = new Files(this.plugin.foundryConfig, this.plugin);

        if (f.exists(f.playerDataFile(p.getUniqueId()))) {
            TypePlayer player = this.plugin.players.get(p.getUniqueId());

            f.set(PLAYER_KEYS.USER_ACCEPTING_TELEPORT_REQUESTS.toString(), player.user.acceptingTeleportRequests);
            f.set(PLAYER_KEYS.USER_LAST_SEEN.toString(), player.user.lastSeen);
            f.set(PLAYER_KEYS.USER_LAST_KNOWN_NAME.toString(), player.user.lastKnownName);
            f.set(PLAYER_KEYS.USER_NICKNAME.toString(), player.user.nickname);
            f.set(PLAYER_KEYS.USER_IP_ADDRESS.toString(), player.user.ipAddress);
            f.set(PLAYER_KEYS.USER_IGNORING_PLAYERS.toString(), player.user.ignoringPlayers);
            f.set(PLAYER_KEYS.ECONOMY_BALANCE.toString(), player.economy.balance);
            f.set(PLAYER_KEYS.ECONOMY_ACCEPTING_PAYMENTS.toString(), player.economy.acceptingPayments);
            f.set(PLAYER_KEYS.LAST_KNOWN_LOCATION_WORLD.toString(), player.lastLocation.world);
            f.set(PLAYER_KEYS.LAST_KNOWN_LOCATION_X.toString(), player.lastLocation.x);
            f.set(PLAYER_KEYS.LAST_KNOWN_LOCATION_Y.toString(), player.lastLocation.y);
            f.set(PLAYER_KEYS.LAST_KNOWN_LOCATION_Z.toString(), player.lastLocation.z);
            f.set(PLAYER_KEYS.LAST_KNOWN_LOCATION_YAW.toString(), player.lastLocation.yaw);
            f.set(PLAYER_KEYS.LAST_KNOWN_LOCATION_PITCH.toString(), player.lastLocation.pitch);
            f.set(PLAYER_KEYS.LAST_KNOWN_LOCATION_IS_BED.toString(), player.lastLocation.isBed);
            f.set(PLAYER_KEYS.USER_TEAM.toString(), player.user.team);

            f.save();

            return true;
        } else {
            return false;
        }
    }

    /**
     * Unloads a player's data - WARNING: DOES NOT SAVE IT
     * @param p OfflinePlayer - The player
     * @return boolean - Success?
     * @since 1.11.0
     */
    public boolean unloadPlayer(OfflinePlayer p) {
        this.plugin.players.remove(p.getUniqueId());
        return true;
    }

    /**
     * Creates a player's data file. Does not load any data into it - default data is loaded in by loadPlayer()
     * @param p OfflinePlayer - The player
     * @return boolean - Success?
     * @since 1.11.0
     */
    public boolean createPlayer(OfflinePlayer p) {
        Files f = new Files(this.plugin.foundryConfig, this.plugin);

        if (!f.exists(f.playerDataFile(p.getUniqueId()))) {
            return f.create(f.playerDataFile(p.getUniqueId()));
        } else {
            return false;
        }
    }

    /**
     * Player data keys
     */
    public enum PLAYER_KEYS {
        USER_ACCEPTING_TELEPORT_REQUESTS {
            @Override
            public String toString() { return "user.accepting-teleport-requests"; }
        },
        USER_LAST_SEEN {
            @Override
            public String toString() { return "user.last-seen"; }
        },
        USER_LAST_KNOWN_NAME {
            @Override
            public String toString() { return "user.last-known-name"; }
        },
        USER_NICKNAME {
            @Override
            public String toString() { return "user.nickname"; }
        },
        USER_IP_ADDRESS {
            @Override
            public String toString() { return "user.ip-address"; }
        },
        USER_IGNORING_PLAYERS {
            @Override
            public String toString() { return "user.ignoring-players"; }
        },
        USER_TEAM {
            @Override
            public String toString() { return "user.team"; }
        },
        ECONOMY_BALANCE {
            @Override
            public String toString() { return "economy.balance"; }
        },
        ECONOMY_ACCEPTING_PAYMENTS {
            @Override
            public String toString() { return "economy.accepting-payments"; }
        },
        LAST_KNOWN_LOCATION_WORLD {
            @Override
            public String toString() { return "location.last-known.world"; }
        },
        LAST_KNOWN_LOCATION_X {
            @Override
            public String toString() { return "location.last-known.x"; }
        },
        LAST_KNOWN_LOCATION_Y {
            @Override
            public String toString() { return "location.last-known.y"; }
        },
        LAST_KNOWN_LOCATION_Z {
            @Override
            public String toString() { return "location.last-known.z"; }
        },
        LAST_KNOWN_LOCATION_YAW {
            @Override
            public String toString() { return "location.last-known.yaw"; }
        },
        LAST_KNOWN_LOCATION_PITCH {
            @Override
            public String toString() { return "location.last-known.pitch"; }
        },
        LAST_KNOWN_LOCATION_IS_BED {
            @Override
            public String toString() { return "location.is-bed"; }
        }
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
     * Fetches a player's prefix.
     *
     * @param p Player - The player
     * @return String - The player's prefix (might be blank).
     */
    public String getPlayerPrefix(OfflinePlayer p) {
        if (this.plugin.integrations.chat != null) {
            String prefix = this.plugin.integrations.chat.getPlayerPrefix((Player) p);
            if (prefix != null && !prefix.isEmpty()) {
                return "[" + prefix + "]";
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
     * @param p OfflinePlayer - The player
     * @return String - The player's suffix (might be blank).
     */
    public String getPlayerSuffix(OfflinePlayer p) {
        if (this.plugin.integrations.chat != null) {
            String suffix = this.plugin.integrations.chat.getPlayerSuffix((Player) p);
            if (suffix != null && !suffix.isEmpty()) {
                return " " + suffix;
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
    public String getDisplayname(CommandSender cs) {
        if (cs instanceof Player p) {
            if (this.plugin.players.get(p.getUniqueId()).user.nickname != null) {
                return this.plugin.players.get(p.getUniqueId()).user.nickname;
            } else {
                return cs.getName();
            }
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
    public boolean setDisplayname(CommandSender cs, String nickname) {
        if (cs instanceof Player p) {
            TypePlayer player = this.plugin.players.get(p.getUniqueId());
            player.user.nickname = nickname;
            this.plugin.players.replace(p.getUniqueId(), player);
            return true;
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
    public boolean removeDisplayname(CommandSender cs) {
        if (cs instanceof Player p) {
            TypePlayer player = this.plugin.players.get(p.getUniqueId());
            player.user.nickname = null;
            this.plugin.players.replace(p.getUniqueId(), player);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gives a player X amount of an item.
     * @param p Player - The player
     * @param item String - The item material name
     * @param amount int - The amount requested
     * @return boolean - true/false success.
     */
    public boolean givePlayerItem(Player p, String item, int amount) {
        try {
            PlayerInventory inventory = p.getInventory();
            Material mat = Material.matchMaterial(item);
            if (mat == null) {
                return false;
            } else {
                inventory.addItem(new ItemStack(mat, amount));
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
}