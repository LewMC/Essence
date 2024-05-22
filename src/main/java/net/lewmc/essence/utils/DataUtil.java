package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

public class DataUtil {
    private final Essence plugin;
    private final MessageUtil message;
    private File configFile;
    private final LogUtil log;
    private final String defaultConfig;

    /**
     * Starts a configuration instance.
     * @param plugin Essence instance.
     * @param message MessageUtil instance.
     */
    public DataUtil(Essence plugin, MessageUtil message) {
        this.plugin = plugin;
        this.message = message;
        this.log = new LogUtil(plugin);
        this.defaultConfig = this.plugin.getDataFolder()+"/config.yml";
    }

    /**
     * Loads a configuration file into the instance's memory.
     * @param data The file to load
     */
    public void load(String data) {
        try {
            this.configFile = new File(this.plugin.getDataFolder(), data);
            this.plugin.getConfig().load(configFile);
        } catch (InvalidConfigurationException e) {
            this.log.warn("InvalidConfigurationException loading configuration: " + e + " (File requested: '"+data+"')");
            this.message.PrivateMessage("generic", "configexception");
        } catch (FileNotFoundException e) {
            this.log.warn("FileNotFoundException loading configuration: " + e + " (File requested: '"+data+"')");
            this.message.PrivateMessage("generic", "configexception");
        } catch (IOException e) {
            this.log.warn("IOException loading configuration: " + e + " (File requested: '"+data+"')");
            this.message.PrivateMessage("generic", "configexception");
        } catch (Exception e) {
            this.log.warn("Exception loading configuration: " + e + " (File requested: '"+data+"')");
            this.message.PrivateMessage("generic", "configexception");
        }
    }

    /**
     * Creates a new section in the configuration file.
     * @param section The section to create.
     */
    public void createSection(String section) {
        this.plugin.getConfig().createSection(section);
    }

    /**
     * Retrieves a section in the configuration file.
     * @param section The section to retrieve.
     * @return A ConfigurationSection instance of the section.
     */
    public ConfigurationSection getSection(String section) {
        return this.plugin.getConfig().getConfigurationSection(section);
    }

    public boolean sectionExists(String section) {
        return this.plugin.getConfig().isConfigurationSection(section);
    }

    /**
     * Saves the configuration to disk.
     */
    public void save() {
        try {
            this.plugin.getConfig().save(this.configFile);
        } catch (IOException e) {
            this.log.warn("IOException saving configuration: " + e);
            this.message.PrivateMessage("generic", "configexception");
        } catch (Exception e) {
            this.log.warn("Exception saving configuration: " + e);
            this.message.PrivateMessage("generic", "configexception");
        }

        this.configFile = null;

        try {
            this.plugin.getConfig().load(this.defaultConfig);
        } catch (IOException | InvalidConfigurationException e) {
            this.log.warn("IOException/InvalidConfigurationException loading configuration: " + e);
            this.message.PrivateMessage("generic", "configexception");
        } catch (Exception e) {
            this.log.warn("Exception loading configuration: " + e);
            this.message.PrivateMessage("generic", "configexception");
        }
    }

    /**
     * Closes the custom config file.
     */
    public void close() {
        this.configFile = null;
        try {
            this.plugin.getConfig().load(this.defaultConfig);
        } catch (IOException | InvalidConfigurationException e) {
            this.log.warn("IOException/InvalidConfigurationException loading configuration: " + e);
            this.message.PrivateMessage("generic", "configexception");
        } catch (Exception e) {
            this.log.warn("Exception loading configuration: " + e);
            this.message.PrivateMessage("generic", "configexception");
        }
    }

    /**
     * Get the keys from a specific section.
     * @param section The section to search, pass "" if you'd like to search the root.
     * @return null or the keys.
     */
    public Set<String> getKeys(String section) {
        if (section.isEmpty()) {
            return this.plugin.getConfig().getKeys(false);
        } else {
            ConfigurationSection cs = this.plugin.getConfig().getConfigurationSection(section);
            if (cs == null) {
                return null;
            } else {
                return cs.getKeys(false);
            }
        }
    }

    public boolean createFile(String file) {
        File fsFile = new File(plugin.getDataFolder(), file);

        if (!fsFile.exists()) {
            try {
                fsFile.getParentFile().mkdirs();
                return fsFile.createNewFile();
            } catch (IOException e) {
                this.log.severe("IOException whilst creating data file '"+file+"': "+e);
                return false;
            } catch (Exception e) {
                this.log.warn("Exception whilst creating data file. File: "+file+" Exception: "+e);
                return false;
            }
        } else {
            this.log.warn("Attempted to create file that already exists.");
            return false;
        }
    }

    public boolean fileExists(String file) {
        File fsFile = new File(plugin.getDataFolder(), file);

        return fsFile.exists();
    }

    /**
     * Return the location of the player's data file.
     * @param player The player.
     * @return The data file URI inside the /plugin/essence folder.
     */
    public String playerDataFile(Player player) {
        return "/data/players/" +player.getUniqueId()+".yml";
    }

    /**
     * Return the location of the player's data file.
     * @param player The UUID of the player.
     * @return The data file URI inside the /plugin/essence folder.
     */
    public String playerDataFile(UUID player) {
        return "/data/players/" +player+".yml";
    }

    public boolean deleteFile(String filePath) {
        this.close();

        Path path = Path.of(this.plugin.getDataFolder() + filePath);

        try {
            Files.delete(path);
        } catch (NoSuchFileException e) {
            this.log.warn("Attempted to delete a file that does not exist. File: "+path+" Exception: "+e);
            return false;
        } catch (DirectoryNotEmptyException e) {
            this.log.warn("Attempted to delete a directory that isn't empty. File: "+path+" Exception: "+e);
            return false;
        } catch (IOException e) {
            this.log.warn("Attempted to delete a file with IOException. File: "+path+" Exception: "+e);
            return false;
        } catch (Exception e) {
            this.log.warn("Attempted to delete a file with Exception. File: "+path+" Exception: "+e);
        }

        return true;
    }
}

