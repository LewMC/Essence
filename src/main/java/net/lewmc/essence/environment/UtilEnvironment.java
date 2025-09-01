package net.lewmc.essence.environment;

import com.tcoded.folialib.FoliaLib;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * The environment utility.
 */
public class UtilEnvironment {
    private final Plugin plugin;
    
    /**
     * Constructor for UtilEnvironment with plugin instance
     * @param plugin The plugin instance
     */
    public UtilEnvironment(Plugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Default constructor for backward compatibility
     */
    public UtilEnvironment() {
        this.plugin = null;
    }
    /**
     * Stores preset weather.
     */
    public enum Weather {
        CLEAR {@Override public String toString() { return "clear"; }},
        RAIN {@Override public String toString() { return "storm"; }},
        THUNDER {@Override public String toString() { return "thunder"; }},
        RESET {@Override public String toString() { return "reset"; }},
        UNKNOWN {@Override public String toString() { return "Unknown"; }}
    }

    /**
     * Stores preset times
     */
    public enum Time { DAY, MIDDAY, EVENING, SUNSET, NIGHT, MIDNIGHT, RESET, SUNRISE }

    /**
     * Gets the weather of the requested world.
     * @param w World - The world requested.
     * @return Weather - The active weather type.
     */
    public Weather getWeather(World w) {
        if (w != null) {
            if (w.isClearWeather()) {
                return Weather.CLEAR;
            } else if (w.isThundering()) {
                return Weather.THUNDER;
            } else if (w.hasStorm()) {
                return Weather.RAIN;
            } else {
                return Weather.UNKNOWN;
            }
        } else {
            return Weather.UNKNOWN;
        }
    }

    /**
     * Sets the weather in the requested world
     * @param wo World - The world
     * @param we Weather - The weather
     * @return boolean - Success?
     */
    public boolean setWeather(World wo, Weather we) {
        if (wo != null) {
            if (we == Weather.CLEAR) {
                wo.setStorm(false);
                wo.setThundering(false);
                return true;
            } else if (we == Weather.RAIN) {
                wo.setThundering(false);
                wo.setStorm(true);
                return true;
            } else if (we == Weather.THUNDER) {
                wo.setStorm(false);
                wo.setThundering(true);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Gets the weather of the requested player.
     * @param p Player - The player requested.
     * @return Weather - The active weather type.
     */
    public Weather getPlayerWeather(Player p) {
        if (p != null) {
            if (p.getPlayerWeather() == WeatherType.CLEAR) {
                return Weather.CLEAR;
            } else if (p.getPlayerWeather() == WeatherType.DOWNFALL) {
                return Weather.RAIN;
            } else {
                return Weather.UNKNOWN;
            }
        } else {
            return Weather.UNKNOWN;
        }
    }

    /**
     * Sets the weather in the requested world
     * @param p Player - The player
     * @param we Weather - The weather
     * @return boolean - Success?
     */
    public boolean setPlayerWeather(Player p, Weather we) {
        if (p != null) {
            if (we == Weather.CLEAR) {
                p.setPlayerWeather(WeatherType.CLEAR);
            } else if (we == Weather.RAIN) {
                p.setPlayerWeather(WeatherType.DOWNFALL);
            } else if (we == Weather.THUNDER) {
                p.setPlayerWeather(WeatherType.DOWNFALL);
            } else if (we == Weather.RESET) {
                p.resetPlayerWeather();
            } else {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the time of the requested world.
     * @param w World - The world requested.
     * @return long - The time.
     */
    public long getTime(World w) {
        if (w != null) {
            return w.getTime();
        } else {
            return -1;
        }
    }

    /**
     * Sets the time in the requested world
     * @param wo World - The world
     * @param t Time - The preset time
     * @return boolean - Success?
     */
    public boolean setTime(World wo, Time t) {
        if (wo != null) {
            if (t == Time.DAY) { return setTime(wo, 1000); }
            else if (t == Time.MIDDAY) { return setTime(wo, 6000); }
            else if (t == Time.EVENING) { return setTime(wo, 10000); }
            else if (t == Time.SUNSET) { return setTime(wo, 12000); }
            else if (t == Time.NIGHT) { return setTime(wo, 13000); }
            else if (t == Time.MIDNIGHT) { return setTime(wo, 18000); }
            else if (t == Time.SUNRISE) { return setTime(wo, 23000); }
            else { return false; }
        } else {
            return false;
        }
    }

    /**
     * Sets the time in the requested world
     * @param wo World - The world
     * @param t long - The numeric time
     * @return boolean - Success?
     */
    public boolean setTime(World wo, long t) {
        if (wo != null) {
            Plugin pluginInstance = getPlugin();
            if (pluginInstance != null) {
                FoliaLib flib = new FoliaLib(pluginInstance);
                // Use FoliaLib to handle both Folia and non-Folia servers
                 if (flib.isFolia()) {
                     // Use FoliaLib's global region scheduler for world time changes on Folia
                     flib.getImpl().runNextTick(task -> wo.setTime(t));
                } else {
                    // Direct call for non-Folia servers
                    wo.setTime(t);
                }
                return true;
            } else {
                // Fallback to direct call if plugin instance is null
                wo.setTime(t);
                return true;
            }
        } else {
            return false;
        }
    }
    

    
    /**
     * Get the plugin instance
     * @return Plugin - plugin instance
     */
    private Plugin getPlugin() {
        if (this.plugin != null) {
            return this.plugin;
        }
        
        // Fallback: try to get Essence plugin from plugin manager
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        for (Plugin plugin : plugins) {
            if (plugin.getName().equals("Essence")) {
                return plugin;
            }
        }
        // Last resort: return first available plugin
        return plugins.length > 0 ? plugins[0] : null;
    }

    /**
     * Gets the time of the requested player.
     * @param p Player - The player requested.
     * @return long - The player time.
     */
    public long getPlayerTime(Player p) {
        if (p != null) {
            return p.getPlayerTime();
        } else {
            return -1;
        }
    }

    /**
     * Sets the time of the requested player
     * @param p Player - The player
     * @param t Time - The preset time
     * @return boolean - Success?
     */
    public boolean setPlayerTime(Player p, Time t) {
        if (p != null) {
            if (t == Time.DAY) { return setPlayerTime(p, 1000); }
            else if (t == Time.MIDDAY) { return setPlayerTime(p, 6000); }
            else if (t == Time.EVENING) { return setPlayerTime(p, 10000); }
            else if (t == Time.SUNSET) { return setPlayerTime(p, 12000); }
            else if (t == Time.NIGHT) { return setPlayerTime(p, 13000); }
            else if (t == Time.MIDNIGHT) { return setPlayerTime(p, 18000); }
            else if (t == Time.SUNRISE) { return setPlayerTime(p, 23000); }
            else if (t == Time.RESET) {
                p.resetPlayerTime();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Sets the time of the requested player
     * @param p Player - The player
     * @param t long - The numeric time
     * @return boolean - Success?
     */
    public boolean setPlayerTime(Player p, long t) {
        if (p != null) {
            p.setPlayerTime(t, true);
            return true;
        } else {
            return false;
        }
    }
}
