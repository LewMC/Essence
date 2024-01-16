package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import net.lewmc.essence.MessageHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ConfigUtil {
    private Essence plugin;
    private MessageHandler message;
    private File configFile;

    /**
     * Starts a configuration instance.
     * @param plugin Essence instance.
     * @param message MessageHandler instance.
     */
    public ConfigUtil(Essence plugin, MessageHandler message) {
        this.plugin = plugin;
        this.message = message;
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
            this.plugin.getLogger().warning("[Essence] InvalidConfigurationException loading configuration: " + e);
            this.message.PrivateMessage("Unable to create warp due to an error, see server console for more information.", true);
        } catch (FileNotFoundException e) {
            this.plugin.getLogger().warning("[Essence] FileNotFoundException loading configuration: " + e);
            this.message.PrivateMessage("Unable to create warp due to an error, see server console for more information.", true);
        } catch (IOException e) {
            this.plugin.getLogger().warning("[Essence] IOException loading configuration: " + e);
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
            this.plugin.getLogger().warning("[Essence] Error saving configuration: " + e);
            message.PrivateMessage("Unable to create warp due to an error, see server console for more information.", true);
        }
    }
}
