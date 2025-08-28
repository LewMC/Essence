package net.lewmc.essence;

import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;

import java.util.HashMap;
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

    private final Logger log;

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

    public Map<String, Object> reload() {
        this.configFile.load("config.yml");

        putBoolean("advanced.verbose");
        putBoolean("advanced.playerdata.store-ip-address");
        putBoolean("advanced.update-check");

        putBoolean("admin.enabled");

        putBoolean("chat.enabled");
        putString("chat.name-format");
        putBoolean("chat.allow-message-formatting");
        putString("chat.broadcasts.first-join");
        putString("chat.broadcasts.join");
        putString("chat.broadcasts.leave");
        putString("chat.motd");
        putBoolean("chat.manage-chat");

        putBoolean("economy.enabled");
        putString("economy.mode");
        putDouble("economy.start-money");
        putString("economy.symbol");

        putBoolean("environment.enabled");

        putBoolean("gamemode.enabled");

        putBoolean("inventory.enabled");

        putBoolean("kit.enabled");
        putStringList("kit.spawn-kits");

        putBoolean("stats.enabled");

        putBoolean("team.enabled");

        putBoolean("teleportation.enabled");
        putInt("teleportation.home.wait");
        putInt("teleportation.home.cooldown");
        putInt("teleportation.warp.wait");
        putInt("teleportation.warp.cooldown");
        putInt("teleportation.randomtp.cooldown");
        putInt("teleportation.spawn.wait");
        putInt("teleportation.spawn.cooldown");
        putString("teleportation.spawn.main-spawn-world");
        putBoolean("teleportation.spawn.always-spawn");
        putInt("teleportation.requests.cooldown");
        putBoolean("teleportation.requests.default-enabled");
        putBoolean("teleportation.extended-toggle");
        putBoolean("teleportation.move-to-cancel");

        putString("language");

        putStringList("disabled-commands.list");
        putBoolean("disabled-commands.feedback-in-chat");

        putInt("config-version");

        this.configFile.close();
        return this.config;
    }

    /**
     * Updates the value of a string.
     *
     * @param key String - The key
     */
    private void putString(String key) {
        config.put(key, this.configFile.getString(key));
        verboseLog(key);
    }

    /**
     * Updates the value of a string list.
     *
     * @param key String - The key
     */
    private void putStringList(String key) {
        config.put(key, this.configFile.getStringList(key));
        verboseLog(key);
    }

    /**
     * Updates the value of an integer.
     *
     * @param key String - The key
     */
    private void putInt(String key) {
        config.put(key, this.configFile.getInt(key));
        verboseLog(key);
    }

    /**
     * Updates the value of a double.
     *
     * @param key String - The key
     */
    private void putDouble(String key) {
        config.put(key, this.configFile.getDouble(key));
        verboseLog(key);
    }

    /**
     * Updates the value of a boolean.
     *
     * @param key String - The key
     */
    private void putBoolean(String key) {
        config.put(key, this.configFile.getBoolean(key));
        verboseLog(key);
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