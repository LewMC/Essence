package net.lewmc.essence.utils;

import com.tchristofferson.configupdater.ConfigUpdater;
import net.lewmc.essence.Essence;

import java.io.File;
import java.io.IOException;

public class ConfigUtil {
    private final Essence plugin;

    public ConfigUtil(Essence plugin) {
        this.plugin = plugin;
    }
    public void UpdateConfig() {
        File configFile = new File(this.plugin.getDataFolder(), "config.yml");

        try {
            ConfigUpdater.update(plugin, "config.yml", configFile);
        } catch (IOException e) {
            LogUtil log = new LogUtil(this.plugin);
            log.warn("Unable to update configuration: "+e);
        }
    }
}
