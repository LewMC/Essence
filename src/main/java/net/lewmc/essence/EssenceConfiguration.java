package net.lewmc.essence;

import com.tchristofferson.configupdater.ConfigUpdater;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Essence Configuration handler.
 */
public class EssenceConfiguration {
    /**
     * Reference to the main Essence class.
     */
    public Essence plugin;

    /**
     * The configuration in HashMap format.
     */
    public Map<String, Object> config = new HashMap<>();

    /**
     * The config file handler.
     */
    private final Files configFile;

    /**
     * Logging system.
     */
    private final Logger log;

    /**
     * Checks if changes were made - should save or close?
     */
    private boolean changesMade = false;

    /**
     * Constructs the EssenceConfiguration class.
     *
     * @param plugin Reference to the main Essence class.
     */
    public EssenceConfiguration(Essence plugin) {
        this.plugin = plugin;
        this.configFile = new Files(plugin.foundryConfig, plugin);
        this.log = new Logger(plugin.foundryConfig);
    }

    /**
     * Handles Essence's configuration during startup.
     * @return Map (String, Object) - The new configuration.
     * @since 1.10.1
     */
    public Map<String, Object> startup() {
        File configFile = new File(this.plugin.getDataFolder(), "config.yml");

        try {
            ConfigUpdater.update(plugin, "config.yml", configFile);
        } catch (IOException e) {
            this.log.warn("Unable to update configuration: "+e);
        }

        return this.reload();
    }

    /**
     * Reload's Essence's configuration.
     * @return Map (String, Object) - The new configuration.
     * @since 1.10.1
     */
    public Map<String, Object> reload() {
        if (!this.configFile.exists("config.yml")) {
            this.plugin.saveDefaultConfig();
            if (!this.configFile.exists("config.yml")) {
                Logger log = new Logger(plugin.foundryConfig);
                log.severe("The server was unable to create Essence's configuration file!");
                log.severe("You can download a blank config file from the link below.");
                log.severe("https://github.com/LewMC/Essence/blob/main/src/main/resources/config.yml");
                log.severe("Please contact lewmc.net/help for help and to report the issue.");
            }
        }

        this.configFile.load("config.yml");

        putBoolean("advanced.verbose", (boolean) getValue("advanced.verbose", true, Boolean.class));
        putBoolean("advanced.playerdata.store-ip-address", (boolean) getValue("advanced.playerdata.store-ip-address", true, Boolean.class));
        putBoolean("advanced.update-check", (boolean) getValue("advanced.update-check", true, Boolean.class));

        putBoolean("admin.enabled", (boolean) getValue("admin.enabled", true, Boolean.class));

        putBoolean("chat.enabled", (boolean) getValue("chat.enabled", true, Boolean.class));
        putString("chat.name-format", (String) getValue("chat.name-format", "%essence_combined_prefix% %essence_player%%essence_player_suffix%:", String.class));
        putBoolean("chat.allow-message-formatting", (boolean) getValue("chat.allow-message-formatting", true, Boolean.class));
        putString("chat.broadcasts.first-join", (String) getValue("chat.broadcasts.first-join", "§a%essence_player% joined the server for the first time!", String.class));
        putString("chat.broadcasts.join", (String) getValue("chat.broadcasts.join", "§e%essence_player% joined the server!", String.class));
        putString("chat.broadcasts.leave", (String) getValue("chat.broadcasts.leave", "§c%essence_player% left the server!", String.class));
        putString("chat.motd", (String) getValue("chat.motd", "§2§lWelcome to the server!", String.class));
        putBoolean("chat.manage-chat", (boolean) getValue("chat.manage-chat", true, Boolean.class));

        putBoolean("economy.enabled", (boolean) getValue("economy.enabled", true, Boolean.class));
        putString("economy.mode", (String) getValue("economy.mode", "VAULT", String.class));
        putDouble("economy.start-money", (Double) getValue("economy.start-money", 100.00, Double.class));
        putString("economy.symbol", (String) getValue("economy.symbol", "$", String.class));

        putBoolean("environment.enabled", (boolean) getValue("environment.enabled", true, Boolean.class));

        putBoolean("gamemode.enabled", (boolean) getValue("gamemode.enabled", true, Boolean.class));

        putBoolean("inventory.enabled", (boolean) getValue("inventory.enabled", true, Boolean.class));

        putBoolean("kit.enabled", (boolean) getValue("kit.enabled", true, Boolean.class));
        putStringList("kit.spawn-kits", (List<String>) getValue("kit.spawn-kits", List.of("wooden-tools"), List.class));

        putBoolean("stats.enabled", (boolean) getValue("stats.enabled", true, Boolean.class));

        putBoolean("team.enabled", (boolean) getValue("team.enabled", true, Boolean.class));

        putBoolean("teleportation.enabled", (boolean) getValue("teleportation.enabled", true, Boolean.class));
        putInt("teleportation.back.wait", (Integer) getValue("teleportation.back.wait", 3, Integer.class));
        putInt("teleportation.top.wait", (Integer) getValue("teleportation.top.wait", 3, Integer.class));
        putInt("teleportation.bottom.wait", (Integer) getValue("teleportation.bottom.wait", 3, Integer.class));
        putInt("teleportation.home.wait", (Integer) getValue("teleportation.home.wait", 3, Integer.class));
        putInt("teleportation.home.cooldown", (Integer) getValue("teleportation.home.cooldown", 10, Integer.class));
        putInt("teleportation.warp.wait", (Integer) getValue("teleportation.warp.wait", 3, Integer.class));
        putInt("teleportation.warp.cooldown", (Integer) getValue("teleportation.warp.cooldown", 10, Integer.class));
        putInt("teleportation.randomtp.cooldown", (Integer) getValue("teleportation.randomtp.cooldown", 60, Integer.class));
        putInt("teleportation.spawn.wait", (Integer) getValue("teleportation.spawn.wait", 3, Integer.class));
        putInt("teleportation.spawn.cooldown", (Integer) getValue("teleportation.spawn.cooldown", 10, Integer.class));
        putString("teleportation.spawn.main-spawn-world", (String) getValue("teleportation.spawn.main-spawn-world", "world", String.class));
        putBoolean("teleportation.spawn.always-spawn", (boolean) getValue("teleportation.spawn.always-spawn", false, Boolean.class));
        putInt("teleportation.requests.cooldown", (Integer) getValue("teleportation.requests.cooldown", 10, Integer.class));
        putBoolean("teleportation.requests.default-enabled", (boolean) getValue("teleportation.requests.default-enabled", true, Boolean.class));
        putBoolean("teleportation.extended-toggle", (boolean) getValue("teleportation.extended-toggle", false, Boolean.class));
        putBoolean("teleportation.move-to-cancel", (boolean) getValue("teleportation.move-to-cancel", true, Boolean.class));

        putString("language", (String) getValue("language", "en-GB", String.class));

        putStringList("disabled-commands", (List<String>) getValue("disabled-commands", List.of("example"), List.class));

        putStringList("item-blacklist", (List<String>) getValue("item-blacklist", List.of("example"), List.class));

        putInt("config-version", (Integer) getValue("config-version", 3, Integer.class));

        if (this.changesMade) {
            this.configFile.save();
        } else {
            this.configFile.close();
        }
        return this.config;
    }

    /**
     * Updates the value of a string.
     *
     * @param key String - The key
     * @param value String - The value
     */
    private void putString(String key, String value) {
        config.put(key, value);
        verboseLog(key);
    }

    /**
     * Updates the value of a string list.
     *
     * @param key String - The key
     * @param value List of Strings - The string list value
     */
    private void putStringList(String key, List<String> value) {
        config.put(key, value);
        verboseLog(key);
    }

    /**
     * Updates the value of an integer.
     *
     * @param key String - The key
     * @param value Integer - The integer value.
     */
    private void putInt(String key, Integer value) {
        config.put(key, value);
        verboseLog(key);
    }

    /**
     * Updates the value of a double.
     *
     * @param key String - The key
     * @param value Double - The double value.
     */
    private void putDouble(String key, Double value) {
        config.put(key, value);
        verboseLog(key);
    }

    /**
     * Updates the value of a boolean.
     *
     * @param key String - The key
     * @param value Boolean - The boolean value
     */
    private void putBoolean(String key, Boolean value) {
        config.put(key, value);
        verboseLog(key);
    }

    /**
     * Gets the current value of a configuration item and type checks it.
     * @param key String - The key
     * @param defaultValue Object - The default value if type checking fails.
     * @param clazz Class(?) - The class it should be.
     * @return Object - The value (will match type of clazz)
     */
    private Object getValue(String key, Object defaultValue, Class<?> clazz) {
        if (this.configFile.get(key) != null) {
            Object value = this.configFile.get(key);
            if (clazz.isInstance(value)) {
                return clazz.cast(value);
            } else {
                this.changesMade = true;
                this.configFile.set(key, defaultValue);
                this.log.warn("Config > Value '"+key+"' had invalid type.");
                this.log.warn("Config > Value '"+key+"' was reset to '"+defaultValue+"'.");
                this.log.warn("Config > Please double-check your configuration is correct.");
                return clazz.cast(defaultValue);
            }
        } else {
            this.changesMade = true;
            this.configFile.set(key, defaultValue);
            this.log.warn("Config > Value '"+key+"' did not exist in the config file.");
            this.log.warn("Config > Value '"+key+"' was reset to '"+defaultValue+"'.");
            return defaultValue;
        }
    }

    /**
     * Logs the new config file to console if verbose mode is enabled.
     *
     * @param key String - The key
     */
    private void verboseLog(String key) {
        if ((boolean) config.get("advanced.verbose")) {
            log.info("Config > " + key + " = " + config.get(key));
        }
    }
}