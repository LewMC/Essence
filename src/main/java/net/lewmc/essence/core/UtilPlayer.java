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
import java.util.List;
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
     * Sets a player's data value
     * @param p OfflinePlayer - The player
     * @param key PLAYER_KEYS - The item to change
     * @param value Object - The value
     * @return Success?
     */
    public boolean setPlayer(OfflinePlayer p, PLAYER_KEYS key, Object value) {
        if (this.plugin.players.containsKey(p.getUniqueId())) {
            TypePlayer player = this.plugin.players.get(p.getUniqueId());

            if (key == PLAYER_KEYS.USER_ACCEPTING_TELEPORT_REQUESTS && value instanceof Boolean) {
                player.user.acceptingTeleportRequests = (Boolean) value;
            } else if (key == PLAYER_KEYS.USER_LAST_SEEN && value instanceof String) {
                player.user.lastSeen = (String) value;
            } else if (key == PLAYER_KEYS.USER_LAST_KNOWN_NAME && value instanceof String) {
                player.user.lastKnownName = (String) value;
            } else if (key == PLAYER_KEYS.USER_NICKNAME && value instanceof String) {
                player.user.nickname = (String) value;
            } else if (key == PLAYER_KEYS.USER_IP_ADDRESS && value instanceof String) {
                player.user.ipAddress = (String) value;
            } else if (key == PLAYER_KEYS.USER_IGNORING_PLAYERS && value instanceof List<?>) {
                player.user.ignoringPlayers = (List<String>) value;
            } else if (key == PLAYER_KEYS.ECONOMY_BALANCE && value instanceof Double) {
                player.economy.balance = (Double) value;
            } else if (key == PLAYER_KEYS.ECONOMY_ACCEPTING_PAYMENTS && value instanceof Boolean) {
                player.economy.acceptingPayments = (Boolean) value;
            } else if (key == PLAYER_KEYS.LAST_KNOWN_LOCATION_WORLD && value instanceof String) {
                player.lastLocation.world = (String) value;
            } else if (key == PLAYER_KEYS.LAST_KNOWN_LOCATION_X && value instanceof Double) {
                player.lastLocation.x = (Double) value;
            } else if (key == PLAYER_KEYS.LAST_KNOWN_LOCATION_Y && value instanceof Double) {
                player.lastLocation.y = (Double) value;
            } else if (key == PLAYER_KEYS.LAST_KNOWN_LOCATION_Z && value instanceof Double) {
                player.lastLocation.z = (Double) value;
            } else if (key == PLAYER_KEYS.LAST_KNOWN_LOCATION_YAW && value instanceof Float) {
                player.lastLocation.yaw = (Float) value;
            } else if (key == PLAYER_KEYS.LAST_KNOWN_LOCATION_PITCH && value instanceof Float) {
                player.lastLocation.pitch = (Float) value;
            } else {
                return false;
            }

            this.plugin.players.put(p.getUniqueId(), player);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets a player's data value
     * @param p OfflinePlayer - The player
     * @param key PLAYER_KEYS - The item to change
     * @return Object - Value or Null
     */
    public Object getPlayer(OfflinePlayer p, PLAYER_KEYS key) {
        if (this.plugin.players.containsKey(p.getUniqueId())) {
            TypePlayer player = this.plugin.players.get(p.getUniqueId());

            if (key == PLAYER_KEYS.USER_ACCEPTING_TELEPORT_REQUESTS) {
                return player.user.acceptingTeleportRequests;
            } else if (key == PLAYER_KEYS.USER_LAST_SEEN) {
                return player.user.lastSeen;
            } else if (key == PLAYER_KEYS.USER_LAST_KNOWN_NAME) {
                return player.user.lastKnownName;
            } else if (key == PLAYER_KEYS.USER_NICKNAME) {
                return player.user.nickname;
            } else if (key == PLAYER_KEYS.USER_IP_ADDRESS) {
                return player.user.ipAddress;
            } else if (key == PLAYER_KEYS.USER_IGNORING_PLAYERS) {
                return player.user.ignoringPlayers;
            } else if (key == PLAYER_KEYS.ECONOMY_BALANCE) {
                return player.economy.balance;
            } else if (key == PLAYER_KEYS.ECONOMY_ACCEPTING_PAYMENTS) {
                return player.economy.acceptingPayments;
            } else if (key == PLAYER_KEYS.LAST_KNOWN_LOCATION_WORLD) {
                return player.lastLocation.world;
            } else if (key == PLAYER_KEYS.LAST_KNOWN_LOCATION_X) {
                return player.lastLocation.x;
            } else if (key == PLAYER_KEYS.LAST_KNOWN_LOCATION_Y) {
                return player.lastLocation.y;
            } else if (key == PLAYER_KEYS.LAST_KNOWN_LOCATION_Z) {
                return player.lastLocation.z;
            } else if (key == PLAYER_KEYS.LAST_KNOWN_LOCATION_YAW) {
                return player.lastLocation.yaw;
            } else if (key == PLAYER_KEYS.LAST_KNOWN_LOCATION_PITCH) {
                return player.lastLocation.pitch;
            } else {
                return null;
            }
        } else {
            return null;
        }
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
            f.load(f.playerDataFile(p));
            TypePlayer player = new TypePlayer();

            Boolean atr = f.getBoolean(PLAYER_KEYS.USER_ACCEPTING_TELEPORT_REQUESTS.toString());
            player.user.acceptingTeleportRequests = (atr == null) ? true : atr;

            player.user.lastSeen = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            player.user.lastKnownName = p.getName();

            String n = f.getString(PLAYER_KEYS.USER_NICKNAME.toString());
            player.user.nickname = (n == null) ? null : n;

            String t = f.getString(PLAYER_KEYS.USER_TEAM.toString());
            player.user.team = (t == null) ? null : t;

            if ((boolean) this.plugin.config.get("advanced.playerdata.store-ip-address")) {
                Player onlinePlayer = p.getPlayer();
                if (onlinePlayer != null && onlinePlayer.getAddress() != null) {
                    f.set(PLAYER_KEYS.USER_IP_ADDRESS.toString(), onlinePlayer.getAddress().getAddress().getHostAddress());
                }
            }

            Double b = f.getDouble(PLAYER_KEYS.ECONOMY_BALANCE.toString());
            player.economy.balance = (b == null) ? (double) this.plugin.config.get("economy.start-money") : b;

            Boolean ap = f.getBoolean(PLAYER_KEYS.ECONOMY_ACCEPTING_PAYMENTS.toString());
            player.economy.acceptingPayments = (ap == null) ? true : ap;

            String llw = f.getString(PLAYER_KEYS.LAST_KNOWN_LOCATION_WORLD.toString());
            player.lastLocation.world = (llw == null) ? null : llw;

            Double llx = f.getDouble(PLAYER_KEYS.LAST_KNOWN_LOCATION_X.toString());
            player.lastLocation.x = (llx == null) ? p.getPlayer().getLocation().getX() : llx;

            Double lly = f.getDouble(PLAYER_KEYS.LAST_KNOWN_LOCATION_Y.toString());
            player.lastLocation.y = (lly == null) ? p.getPlayer().getLocation().getX() : lly;

            Double llz = f.getDouble(PLAYER_KEYS.LAST_KNOWN_LOCATION_Z.toString());
            player.lastLocation.z = (llz == null) ? p.getPlayer().getLocation().getZ() : llz;

            Double llyaw = f.getDouble(PLAYER_KEYS.LAST_KNOWN_LOCATION_YAW.toString());
            player.lastLocation.yaw = (llyaw == null) ? p.getPlayer().getLocation().getYaw() : Float.parseFloat(String.valueOf(llyaw));

            Double llpitch = f.getDouble(PLAYER_KEYS.LAST_KNOWN_LOCATION_PITCH.toString());
            player.lastLocation.pitch = (llpitch == null) ? p.getPlayer().getLocation().getPitch() : Float.parseFloat(String.valueOf(llyaw));

            Boolean llib = f.getBoolean(PLAYER_KEYS.LAST_KNOWN_LOCATION_IS_BED.toString());
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
     * Saves a player's data, avoid using this unless required - it fires automatically when a player leaves the server.
     * @param p OfflinePlayer - The player
     * @return boolean - Success?
     * @since 1.11.0
     */
    public boolean savePlayer(OfflinePlayer p) {
        Files f = new Files(this.plugin.foundryConfig, this.plugin);

        if (f.exists(f.playerDataFile(p))) {
            TypePlayer player = this.plugin.players.get(p.getUniqueId());
            if (player == null) {
                return false;
            }

            f.load(f.playerDataFile(p));

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
     * Unloads a player's data from memory - WARNING: Does not save! Call savePlayer() first to persist changes.
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
     * @param cs CommandSender - The command sender.
     * @return String - The player's prefix (might be blank).
     */
    public String getPlayerPrefix(CommandSender cs) {
        if (this.plugin.integrations.chat != null && cs instanceof Player p) {
            String prefix = this.plugin.integrations.chat.getPlayerPrefix( p);
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
     * @param cs CommandSender - The command sender.
     * @return String - The player's suffix (might be blank).
     */
    public String getPlayerSuffix(CommandSender cs) {
        if (this.plugin.integrations.chat != null && cs instanceof Player p) {
            String suffix = this.plugin.integrations.chat.getPlayerSuffix( p);
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
            this.plugin.players.put(p.getUniqueId(), player);
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
            this.plugin.players.put(p.getUniqueId(), player);
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