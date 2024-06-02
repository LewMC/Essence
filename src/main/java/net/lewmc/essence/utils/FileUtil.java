package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Essence File Utility.
 */
public class FileUtil {
    private final Essence plugin;
    private final LogUtil log;
    private YamlConfiguration config;
    private File file;

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
     * @param path String - Path to the file.
     * @return boolean - If the file and directories (if required) were created successfully.
     */
    public boolean create(String path) {
        File createFile = new File(this.plugin.getDataFolder(), this.parseFileName(path));
        try {
            if (createFile.getParentFile().mkdirs() || createFile.getParentFile().exists()) {
                if (this.plugin.verbose) {
                    this.log.info("Created file at "+new File(this.plugin.getDataFolder(),this.parseFileName(path)));
                }
                return createFile.createNewFile();
            } else {
                this.log.severe("Failed to create file " + path);
                this.log.severe("Unable to create parent directory.");
                return false;
            }
        } catch (IOException e) {
            this.log.severe("Failed to create file " + path);
            this.log.severe(e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a file exists.
     * @param path - Path to the file.
     * @return boolean - If the file exists
     */
    public boolean exists(String path) {
        File file = new File(this.plugin.getDataFolder(), this.parseFileName(path));
        return file.exists();
    }

    /**
     * Opens a configuration file.
     * @param path String - Path to the file
     * @return boolean - If the operation was successful
     */
    public boolean load(String path) {
        if (!this.isOpen()) {
            this.config = new YamlConfiguration();
            File file = new File(this.plugin.getDataFolder(), this.parseFileName(path));
            try {
                if (file.exists()) {
                    this.config.load(file);
                    this.file = file;
                    if (this.plugin.verbose) {
                        this.log.info("Opened file at "+file);
                    }
                    return true;
                } else {
                    this.log.warn("Unable to open file at '" + file.getAbsolutePath() + "'.");
                    return false;
                }
            } catch (IOException | InvalidConfigurationException e) {
                this.log.severe("Failed to read file " + file.getAbsolutePath());
                this.log.severe(e.getMessage());
                return false;
            }
        } else {
            this.log.warn("Tried to open a file when another file was already open.");
            return false;
        }
    }

    /**
     * Deletes a file.
     * @param path String - The path of the file
     * @return boolean - If the operation was successful
     */
    public boolean delete(String path) {
        if (this.isOpen()) {
            try {
                File file = new File(path);
                return file.delete();
            } catch (SecurityException e) {
                this.log.severe("Failed to delete file " + path);
                this.log.severe(e.getMessage());
                return false;
            }
        } else {
            this.log.warn("Tried to delete a file without opening a file first.");
            return false;
        }
    }

    /**
     * Saves and closes the current configuration file to a custom location.
     * @param file String - The file.
     * @return boolean - If the operation was successful
     */
    public boolean save(File file) {
        if (this.isOpen()) {
            try {
                this.config.save(file);
                this.close();
                return true;
            } catch (IOException e) {
                this.log.severe("Failed to save file " + file);
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
                this.config.save(this.file);
                if (this.plugin.verbose) {
                    this.log.info("Saved file to "+this.file);
                }
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
            this.config.set(key, value);
            if (this.plugin.verbose) {
                this.log.info("Set "+key+" to "+value+" in file "+this.file);
            }
            return true;
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
     * Gets a String List from the configuration file.
     * @param key The location of the value.
     * @return List - the value.
     */
    public List<String> getStringList(String key) {
        if (this.isOpen()) {
            return this.config.getStringList(key);
        } else {
            this.log.warn("Tried to get list from a file without opening a file first.");
            return null;
        }
    }

    /**
     * Gets a List from the configuration file if is root.
     * @param deep Should do deep search?
     * @return List - the value.
     */
    public Set<String> getKeys(boolean deep) {
        if (this.isOpen()) {
            return this.config.getKeys(deep);
        } else {
            this.log.warn("Tried to get list from a file without opening a file first.");
            return null;
        }
    }

    /**
     * Gets a List from the configuration file if not root.
     * @param section The section of the file.
     * @param deep Should do deep search?
     * @return List - the value.
     */
    public Set<String> getKeys(String section, boolean deep) {
        if (this.isOpen()) {
            ConfigurationSection cs = this.config.getConfigurationSection(section);
            if (cs != null) {
                return cs.getKeys(deep);
            } else {
                return null;
            }
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
     * Return the location of the player's data file from an instance of the player.
     * @param player Player - The player.
     * @return The data file URI inside the /plugin/essence folder.
     */
    public String playerDataFile(Player player) {
        return "data/players/"+player.getUniqueId()+".yml";
    }

    /**
     * Return the location of the player's data file from the player's UUID.
     * @param uuid UUID - The player's UUID.
     * @return The data file URI inside the /plugin/essence folder.
     */
    public String playerDataFile(UUID uuid) {
        return "data/players/"+uuid+".yml";
    }

    /**
     * Parses the filename to ensure leading slashes are correct.
     * @param fileName String - The name of the file to be opened.
     * @return The correctly parsed filename.
     */
    private String parseFileName(String fileName) {
        if (fileName != null) {
            // Replace backslashes with forward slashes
            fileName = fileName.replace("\\", "/");

            // Ensure the file name starts with a forward slash
            if (!fileName.startsWith("/")) {
                fileName = "/" + fileName;
            }
        }
        return fileName;
    }

    /**
     * Removes a key and it's associated data.
     * @param key String - The item to remove.
     * @return If the operation was successful
     */
    public boolean remove(String key) {
        if (this.isOpen()) {
            this.set(key,null);
            if (this.plugin.verbose) {
                this.log.info("Removed "+key+" from file "+this.file);
            }
            return true;
        } else {
            this.log.warn("Tried to remove an item from a file before one had been opened.");
            return false;
        }
    }
}
