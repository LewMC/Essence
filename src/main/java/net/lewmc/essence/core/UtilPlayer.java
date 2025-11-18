package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.Files;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

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
     * @param uuid UUID - The player's UUID
     * @param key KEYS - The item to change
     * @param value Object - The value
     * @return Success?
     * @since 1.11.0
     */
    public boolean setPlayer(UUID uuid, KEYS key, Object value) {
        if (this.plugin.players.containsKey(uuid)) {
            TypePlayer player = this.plugin.players.get(uuid);

            switch (value) {
                case Boolean b when key == KEYS.USER_ACCEPTING_TELEPORT_REQUESTS -> player.user.acceptingTeleportRequests = b;
                case String s when key == KEYS.USER_LAST_SEEN -> player.user.lastSeen = s;
                case String s when key == KEYS.USER_LAST_KNOWN_NAME -> player.user.lastKnownName = s;
                case String s when key == KEYS.USER_NICKNAME -> player.user.nickname = s;
                case String s when key == KEYS.USER_IP_ADDRESS -> player.user.ipAddress = s;
                case List<?> objects when key == KEYS.USER_IGNORING_PLAYERS -> {
                    if (objects.stream().allMatch(o -> o instanceof String)) {
                        player.user.ignoringPlayers = (List<String>) objects;
                    } else {
                        return false;
                    }
                }
                case Double v when key == KEYS.ECONOMY_BALANCE -> player.economy.balance = v;
                case Boolean b when key == KEYS.ECONOMY_ACCEPTING_PAYMENTS -> player.economy.acceptingPayments = b;
                case String s when key == KEYS.LAST_LOCATION_WORLD -> player.lastLocation.world = s;
                case Double v when key == KEYS.LAST_LOCATION_X -> player.lastLocation.x = v;
                case Double v when key == KEYS.LAST_LOCATION_Y -> player.lastLocation.y = v;
                case Double v when key == KEYS.LAST_LOCATION_Z -> player.lastLocation.z = v;
                case Float v when key == KEYS.LAST_LOCATION_YAW -> player.lastLocation.yaw = v;
                case Float v when key == KEYS.LAST_LOCATION_PITCH -> player.lastLocation.pitch = v;
                case Boolean b when key == KEYS.LAST_LOCATION_IS_BED -> player.lastLocation.isBed = b;
                case null, default -> {
                    return false;
                }
            }

            this.plugin.players.put(uuid, player);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets a player's data value
     * @param uuid UUID - The player's UUID
     * @param key KEYS - The item to change
     * @return Object - Value or Null
     * @since 1.11.0
     */
    public Object getPlayer(UUID uuid, KEYS key) {
        if (this.plugin.players.containsKey(uuid)) {
            TypePlayer player = this.plugin.players.get(uuid);

            return switch (key) {
                case USER_ACCEPTING_TELEPORT_REQUESTS -> player.user.acceptingTeleportRequests;
                case USER_LAST_SEEN -> player.user.lastSeen;
                case USER_LAST_KNOWN_NAME -> player.user.lastKnownName;
                case USER_NICKNAME -> player.user.nickname;
                case USER_IP_ADDRESS -> player.user.ipAddress;
                case USER_IGNORING_PLAYERS -> player.user.ignoringPlayers;
                case ECONOMY_BALANCE -> player.economy.balance;
                case ECONOMY_ACCEPTING_PAYMENTS -> player.economy.acceptingPayments;
                case LAST_LOCATION_WORLD -> player.lastLocation.world;
                case LAST_LOCATION_X -> player.lastLocation.x;
                case LAST_LOCATION_Y -> player.lastLocation.y;
                case LAST_LOCATION_Z -> player.lastLocation.z;
                case LAST_LOCATION_YAW -> player.lastLocation.yaw;
                case LAST_LOCATION_PITCH -> player.lastLocation.pitch;
                case LAST_LOCATION_IS_BED -> player.lastLocation.isBed;
                case null, default -> null;
            };
        } else {
            return null;
        }
    }

    /**
     * Loads a player into memory.
     * @param uuid UUID - The player's UUID.
     * @return boolean - Success?
     * @since 1.11.0
     */
    public boolean loadPlayer(UUID uuid) {
        Files f = new Files(this.plugin.foundryConfig, this.plugin);

        if (f.exists(f.playerDataFile(uuid))) {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null || p.getPlayer() == null) {
                return false;
            }

            f.load(f.playerDataFile(uuid));
            TypePlayer player = new TypePlayer();

            Boolean atr = f.getBoolean(KEYS.USER_ACCEPTING_TELEPORT_REQUESTS.toString());
            player.user.acceptingTeleportRequests = (atr == null) ? true : atr;

            player.user.lastSeen = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            player.user.lastKnownName = p.getName();

            String n = f.getString(KEYS.USER_NICKNAME.toString());
            player.user.nickname = (n == null) ? null : n;

            String t = f.getString(KEYS.USER_TEAM.toString());
            player.user.team = (t == null) ? null : t;

            if ((boolean) this.plugin.config.get("advanced.playerdata.store-ip-address")) {
                Player onlinePlayer = p.getPlayer();
                if (onlinePlayer != null && onlinePlayer.getAddress() != null) {
                    f.set(KEYS.USER_IP_ADDRESS.toString(), onlinePlayer.getAddress().getAddress().getHostAddress());
                }
            }

            Double b = f.getDouble(KEYS.ECONOMY_BALANCE.toString());
            player.economy.balance = (b == null) ? (double) this.plugin.config.get("economy.start-money") : b;

            Boolean ap = f.getBoolean(KEYS.ECONOMY_ACCEPTING_PAYMENTS.toString());
            player.economy.acceptingPayments = (ap == null) ? true : ap;

            String llw = f.getString(KEYS.LAST_LOCATION_WORLD.toString());
            player.lastLocation.world = (llw == null) ? null : llw;

            Double llx = f.getDouble(KEYS.LAST_LOCATION_X.toString());
            player.lastLocation.x = (llx == null) ? p.getPlayer().getLocation().getX() : llx;

            Double lly = f.getDouble(KEYS.LAST_LOCATION_Y.toString());
            player.lastLocation.y = (lly == null) ? p.getPlayer().getLocation().getY() : lly;

            Double llz = f.getDouble(KEYS.LAST_LOCATION_Z.toString());
            player.lastLocation.z = (llz == null) ? p.getPlayer().getLocation().getZ() : llz;

            Double llyaw = f.getDouble(KEYS.LAST_LOCATION_YAW.toString());
            player.lastLocation.yaw = (llyaw == null) ? p.getPlayer().getLocation().getYaw() : Float.parseFloat(String.valueOf(llyaw));

            Double llpitch = f.getDouble(KEYS.LAST_LOCATION_PITCH.toString());
            player.lastLocation.pitch = (llpitch == null) ? p.getPlayer().getLocation().getPitch() : Float.parseFloat(String.valueOf(llpitch));

            Boolean llib = f.getBoolean(KEYS.LAST_LOCATION_IS_BED.toString());
            player.lastLocation.isBed = (llib == null) ? false : llib;

            this.plugin.players.put(uuid,player);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Saves a player's data, avoid using this unless required - it fires automatically when a player leaves the server.
     * @param uuid UUID - The player's UUID
     * @return boolean - Success?
     * @since 1.11.0
     */
    public boolean savePlayer(UUID uuid) {
        Files f = new Files(this.plugin.foundryConfig, this.plugin);

        if (f.exists(f.playerDataFile(uuid))) {
            TypePlayer player = this.plugin.players.get(uuid);
            if (player == null) {
                return false;
            }

            f.load(f.playerDataFile(uuid));

            f.set(KEYS.USER_ACCEPTING_TELEPORT_REQUESTS.toString(), player.user.acceptingTeleportRequests);
            f.set(KEYS.USER_LAST_SEEN.toString(), player.user.lastSeen);
            f.set(KEYS.USER_LAST_KNOWN_NAME.toString(), player.user.lastKnownName);
            f.set(KEYS.USER_NICKNAME.toString(), player.user.nickname);
            f.set(KEYS.USER_IP_ADDRESS.toString(), player.user.ipAddress);
            f.set(KEYS.USER_IGNORING_PLAYERS.toString(), player.user.ignoringPlayers);
            f.set(KEYS.ECONOMY_BALANCE.toString(), player.economy.balance);
            f.set(KEYS.ECONOMY_ACCEPTING_PAYMENTS.toString(), player.economy.acceptingPayments);
            f.set(KEYS.LAST_LOCATION_WORLD.toString(), player.lastLocation.world);
            f.set(KEYS.LAST_LOCATION_X.toString(), player.lastLocation.x);
            f.set(KEYS.LAST_LOCATION_Y.toString(), player.lastLocation.y);
            f.set(KEYS.LAST_LOCATION_Z.toString(), player.lastLocation.z);
            f.set(KEYS.LAST_LOCATION_YAW.toString(), player.lastLocation.yaw);
            f.set(KEYS.LAST_LOCATION_PITCH.toString(), player.lastLocation.pitch);
            f.set(KEYS.LAST_LOCATION_IS_BED.toString(), player.lastLocation.isBed);
            f.set(KEYS.USER_TEAM.toString(), player.user.team);

            return f.save();
        } else {
            return false;
        }
    }

    /**
     * Unloads a player's data from memory - WARNING: Does not save! Call savePlayer() first to persist changes.
     * @param uuid UUID - The player's UUID
     * @since 1.11.0
     */
    public void unloadPlayer(UUID uuid) {
        this.plugin.players.remove(uuid);
    }

    /**
     * Creates a player's data file. Does not load any data into it - default data is loaded in by loadPlayer()
     * @param uuid UUID - The player's UUID
     * @return boolean - Success?
     * @since 1.11.0
     */
    public boolean createPlayer(UUID uuid) {
        Files f = new Files(this.plugin.foundryConfig, this.plugin);

        if (!f.exists(f.playerDataFile(uuid))) {
            return f.create(f.playerDataFile(uuid));
        } else {
            return false;
        }
    }

    /**
     * Player data keys
     * @since 1.11.0
     */
    public enum KEYS {
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
        LAST_LOCATION_WORLD {
            @Override
            public String toString() { return "location.last-known.world"; }
        },
        LAST_LOCATION_X {
            @Override
            public String toString() { return "location.last-known.x"; }
        },
        LAST_LOCATION_Y {
            @Override
            public String toString() { return "location.last-known.y"; }
        },
        LAST_LOCATION_Z {
            @Override
            public String toString() { return "location.last-known.z"; }
        },
        LAST_LOCATION_YAW {
            @Override
            public String toString() { return "location.last-known.yaw"; }
        },
        LAST_LOCATION_PITCH {
            @Override
            public String toString() { return "location.last-known.pitch"; }
        },
        LAST_LOCATION_IS_BED {
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
            TypePlayer player = this.plugin.players.get(p.getUniqueId());
            if (player != null && player.user.nickname != null) {
                return player.user.nickname;
            }
            return cs.getName();
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
     * @since 1.11.0
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