package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

public class DataUtil {
    private Essence plugin;
    private MessageUtil message;
    private File configFile;
    private LogUtil log;

    /**
     * Starts a configuration instance.
     * @param plugin Essence instance.
     * @param message MessageUtil instance.
     */
    public DataUtil(Essence plugin, MessageUtil message) {
        this.plugin = plugin;
        this.message = message;
        this.log = new LogUtil(plugin);
    }

    /**
     * Loads a configuration file into the instance's memory.
     * @param config The file to load
     */
    public void load(String config) {
        try {
            this.configFile = new File(this.plugin.getDataFolder(), config);
            this.plugin.getConfig().load(configFile);
        } catch (InvalidConfigurationException e) {
            this.log.warn("InvalidConfigurationException loading configuration: " + e);
            this.message.PrivateMessage("Unable to create warp due to an error, see server console for more information.", true);
        } catch (FileNotFoundException e) {
            this.log.warn("FileNotFoundException loading configuration: " + e);
            this.message.PrivateMessage("Unable to create warp due to an error, see server console for more information.", true);
        } catch (IOException e) {
            this.log.warn("IOException loading configuration: " + e);
            this.message.PrivateMessage("Unable to create warp due to an error, see server console for more information.", true);
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

    /**
     * Saves the configuration to disk.
     */
    public void save() {
        try {
            this.plugin.getConfig().save(configFile);
        } catch (IOException e) {
            this.log.warn("Error saving configuration: " + e);
            message.PrivateMessage("The server was unable to process configuration data, see the console for more information.", true);
        }
    }

    /**
     * Get the keys from a specific section.
     * @param section The section to search, pass "" if you'd like to search the root.
     * @return
     */
    public Set<String> getKeys(String section) {
        if (section.equals("")) {
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
                return fsFile.createNewFile();
            } catch (IOException e) {
                this.log.severe("IOException whilst creating data file '"+file+"': "+e);
                return false;
            }
        } else {
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
}

