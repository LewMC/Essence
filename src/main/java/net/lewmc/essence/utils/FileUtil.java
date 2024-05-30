package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileUtil {
    private final Essence plugin;
    private final LogUtil log;
    private YamlConfiguration config;
    private String file;

    /**
     * The constructor for the FileUtil class.
     * @param plugin Reference to the main Essence class.
     */
    public FileUtil(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
        this.config = new YamlConfiguration();
    }

    /**
     * Creates a file.
     * @return boolean - If the file and directories (if required) were created successfully.
     */
    public boolean create(String name) {
        File createFile = new File(this.plugin.getDataFolder()+name);
        try {
            if (createFile.getParentFile().mkdirs()) {
                return createFile.createNewFile();
            } else {
                this.log.severe("Failed to create file " + name);
                this.log.severe("Unable to create parent directory.");
                return false;
            }
        } catch (IOException e) {
            this.log.severe("Failed to create file " + name);
            this.log.severe(e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a file exists.
     * @return boolean - If the file exists
     */
    public boolean exists(String name) {
        File file = new File(this.plugin.getDataFolder()+name);
        return file.exists();
    }

    /**
     * Opens a configuration file.
     * @return boolean - If the operation was successful
     */
    public boolean load(String name) {
        if (!this.isOpen()) {
            this.config = new YamlConfiguration();
            try {
                if (this.exists(name)) {
                    this.config.load(new File(this.plugin.getDataFolder() + name));
                    this.file = this.plugin.getDataFolder() + name;
                    return true;
                } else {
                    return false;
                }
            } catch (IOException | InvalidConfigurationException e) {
                this.log.severe("Failed to read file " + name);
                this.log.severe(e.getMessage());
                return false;
            }
        } else {
            this.log.warn("Tried to open a file when another file was already open.");
            return false;
        }
    }

    /**
     * Saves and closes the current configuration file to a custom location.
     * @return boolean - If the operation was successful
     */
    public boolean save(String name) {
        if (this.isOpen()) {
            try {
                this.config.save(new File(this.plugin.getDataFolder() + name));
                this.close();
                return true;
            } catch (IOException e) {
                this.log.severe("Failed to save file " + name);
                this.log.severe(e.getMessage());
                return false;
            }
        } else {
            this.log.warn("Tried to save a file without opening a file first.");
            return false;
        }
    }

    /**
     * Saves and closes the current configuration file.
     * @return boolean - If the operation was successful
     */
    public boolean save() {
        if (this.isOpen()) {
            try {
                this.config.save(new File(this.file));
                this.close();
                return true;
            } catch (IOException e) {
                this.log.severe("Failed to save file " + this.file);
                this.log.severe(e.getMessage());
                this.close();
                return false;
            }
        } else {
            this.log.warn("Tried to save a file without opening a file first.");
            return false;
        }
    }

    /**
     * Sets a value in the configuration file.
     * @param key The location of the value.
     * @param value The value to set.
     * @return boolean - If the operation was successful
     */
    public boolean set(String key, Object value) {
        if (this.isOpen()) {
            if (this.config.get(key) != null) {
                this.config.set(key, value);
                return true;
            } else {
                return false;
            }
        } else {
            this.log.warn("Tried to set a value in a file without opening a file first.");
            return false;
        }
    }

    /**
     * Gets an object from the configuration file.
     * @param key The location of the value.
     * @return Object - the value.
     */
    public Object get(String key) {
        if (this.isOpen()) {
            return this.config.get(key);
        } else {
            this.log.warn("Tried to get a value from a file without opening a file first.");
            return null;
        }
    }

    /**
     * Gets a boolean from the configuration file.
     * @param key The location of the value.
     * @return boolean - the value.
     */
    public boolean getBoolean(String key) {
        if (this.isOpen()) {
            return this.config.getBoolean(key);
        } else {
            this.log.warn("Tried to get a boolean from a file without opening a file first.");
            return false;
        }
    }

    /**
     * Gets a string from the configuration file.
     * @param key The location of the value.
     * @return String - the value.
     */
    public String getString(String key) {
        if (this.isOpen()) {
            return this.config.getString(key);
        } else {
            this.log.warn("Tried to get a string from a file without opening a file first.");
            return null;
        }
    }

    /**
     * Gets an integer from the configuration file.
     * @param key The location of the value.
     * @return int - the value.
     */
    public int getInt(String key) {
        if (this.isOpen()) {
            return this.config.getInt(key);
        } else {
            this.log.warn("Tried to get an integer from a file without opening a file first.");
            return 0;
        }
    }

    /**
     * Gets a double from the configuration file.
     * @param key The location of the value.
     * @return double - the value.
     */
    public double getDouble(String key) {
        if (this.isOpen()) {
            return this.config.getDouble(key);
        } else {
            this.log.warn("Tried to get double from a file without opening a file first.");
            return 0.0;
        }
    }

    /**
     * Gets a List from the configuration file.
     * @param key The location of the value.
     * @return List - the value.
     */
    public List<?> getList(String key) {
        if (this.isOpen()) {
            return this.config.getList(key);
        } else {
            this.log.warn("Tried to get list from a file without opening a file first.");
            return null;
        }
    }

    /**
     * Closes the configuration file without saving it.
     * Not needed if save() has been used.
     * @return boolean - If the operation was successful
     */
    public boolean close() {
        if (this.isOpen()) {
            this.config = null;
            this.file = null;
            return true;
        } else {
            this.log.warn("Tried to close a file before one had been opened.");
            return false;
        }
    }

    /**
     * Checks if a file is open.
     * @return boolean - If a file is open
     */
    public boolean isOpen() {
        return this.config != null && this.file != null;
    }

    /**
     * Return the location of the player's data file.
     * @param player The player.
     * @return The data file URI inside the /plugin/essence folder.
     */
    public String playerDataFile(Player player) {
        return this.plugin.getDataFolder()+"data/"+player.getUniqueId()+".yml";
    }
}
