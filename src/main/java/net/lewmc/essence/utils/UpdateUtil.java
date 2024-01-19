package net.lewmc.essence.utils;

import com.tchristofferson.configupdater.ConfigUpdater;
import net.lewmc.essence.Essence;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class UpdateUtil {
    private final Essence plugin;
    private final LogUtil log;

    public UpdateUtil(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
    }

    public void VersionCheck() {
        if (this.plugin.getConfig().getBoolean("update-check")) {
            try {
                URL url;
                if (this.plugin.getDescription().getVersion().contains("SNAPSHOT")) {
                    log.warn("You are running a snapshot build of Essence.");
                    log.warn("This build may include bugs and is not recommended on production servers.");
                    log.warn("If you find any issues please report them to github.com/lewmilburn/essence.");
                    url = new URL("https://service.lewmc.net/latest-version/?resource=essence-snapshot&format=simpleversion");
                } else {
                    url = new URL("https://service.lewmc.net/latest-version/?resource=essence&format=simpleversion");
                }
                Scanner s = new Scanner(url.openStream());
                if (s.hasNextLine()) {
                    String response = s.nextLine();
                    if (response.equals("")) {
                        log.severe("Unable to perform update check: There was no response from the server.");
                    } else if (response.equals(this.plugin.getDescription().getVersion())) {
                        log.info("You are running the latest version of Essence.");
                    } else {
                        log.severe("There's a new version of Essence available.");
                        log.severe("Your version: "+this.plugin.getDescription().getVersion()+" - latest version: "+response);
                    }
                } else {
                    log.severe("Unable to perform update check: There was no response from the server.");
                }
            } catch (MalformedURLException e) {
                log.severe("Unable to perform update check: MalformedURLException - "+e);
            } catch (IOException e) {
                log.warn("Unable to perform update check: IOException - "+e);
            }
        } else {
            log.warn("Unable to perform update check: Update checking is disabled.");
        }
    }

    public void UpdateConfig() {
        File configFile = new File(this.plugin.getDataFolder(), "config.yml");

        try {
            ConfigUpdater.update(plugin, "config.yml", configFile);
        } catch (IOException e) {
            this.log.warn("Unable to update configuration: "+e);
        }
    }
}
