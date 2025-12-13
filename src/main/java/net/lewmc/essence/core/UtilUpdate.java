package net.lewmc.essence.core;

import com.tchristofferson.configupdater.ConfigUpdater;
import net.lewmc.essence.Essence;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Essence's update utility.
 */
public class UtilUpdate {
    private final Essence plugin;
    private final Logger log;

    /**
     * Constructor for UpdateUtil class.
     * @param plugin Reference to the main Essence class.
     */
    public UtilUpdate(Essence plugin) {
        this.plugin = plugin;
        this.log = new Logger(plugin.foundryConfig);
    }

    /**
     * Compares two version strings to determine if the first version is newer than the second.
     * Supports semantic versioning format (major.minor.patch).
     * 
     * @param version1 The first version string (potential newer version)
     * @param version2 The second version string (current version)
     * @return true if version1 is newer than version2, false otherwise
     */
    private boolean isNewerVersion(String version1, String version2) {
        if (version1 == null || version2 == null) {
            return false;
        }
        
        // Remove any non-numeric suffixes (like -SNAPSHOT)
        String cleanVersion1 = version1.split("-")[0];
        String cleanVersion2 = version2.split("-")[0];
        
        String[] parts1 = cleanVersion1.split("\\.");
        String[] parts2 = cleanVersion2.split("\\.");
        
        // Compare each part of the version number
        int maxLength = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < maxLength; i++) {
            int part1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int part2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            
            if (part1 > part2) {
                return true;
            } else if (part1 < part2) {
                return false;
            }
        }
        
        return false; // Versions are equal
    }

    /**
     * Checks Essence's version.
     */
    public void VersionCheck() {
        if ((boolean) this.plugin.config.get("advanced.update-check")) {
            try {
                URL url;
                if (this.plugin.getDescription().getVersion().contains("SNAPSHOT")) {
                    log.warn("SNAPSHOT > You are running a snapshot build of Essence.");
                    log.warn("SNAPSHOT > This build may include bugs and is not recommended on production servers.");
                    log.warn("SNAPSHOT > If you find any issues please report them to github.com/lewmc/essence.");
                    this.log.info("");
                    url = new URL("https://service.lewmc.net/latest-version/?resource=essence-snapshot&format=simpleversion");
                } else {
                    url = new URL("https://service.lewmc.net/latest-version/?resource=essence&format=simpleversion");
                }
                Scanner s = new Scanner(url.openStream());
                if (s.hasNextLine()) {
                    String response = s.nextLine();
                    if (response.isEmpty()) {
                        log.severe("Unable to perform update check: There was no response from the server.");
                        this.log.info("");
                    } else if (response.equals(this.plugin.getDescription().getVersion())) {
                        log.info("You are running the latest version of Essence.");
                        this.log.info("");
                    } else if (isNewerVersion(response, this.plugin.getDescription().getVersion())) {
                        log.warn("UPDATE > There's a new version of Essence available.");
                        log.warn("UPDATE > Your version: "+this.plugin.getDescription().getVersion()+" - latest version: "+response);
                        log.warn("UPDATE > You can download the latest version from lewmc.net/essence");
                        this.log.info("");
                        this.plugin.hasPendingUpdate = true;
                    } else {
                        log.warn("DEVELOPMENT > You are running a development version ahead of the official release.");
                        log.warn("DEVELOPMENT > Your version: "+this.plugin.getDescription().getVersion()+" - latest stable: "+response);
                        log.warn("DEVELOPMENT > Please be aware of potential stability risks and report any issues.");
                        this.log.info("");
                    }
                } else {
                    log.severe("Unable to perform update check: There was no response from the server.");
                    this.log.info("");
                }
            } catch (MalformedURLException e) {
                log.severe("Unable to perform update check: MalformedURLException - "+e);
                this.log.info("");
            } catch (IOException e) {
                log.warn("Unable to perform update check: IOException - "+e);
                this.log.info("");
            }
        } else {
            log.warn("Unable to perform update check: Update checking is disabled.");
            this.log.info("");
        }
    }

    /**
     * Updates Essence's language files.
     */
    public void UpdateLanguage() {
        // WHEN ADDING MORE HERE, ALSO PUT IN COMMANDESSENCE.RESTORE.

        // en-GB
        File enGB = new File(this.plugin.getDataFolder() + File.separator + "language" + File.separator + "en-GB.yml");
        if (!enGB.exists()) {
            this.plugin.saveResource("language/en-GB.yml", true);
        } else {
            try {
                ConfigUpdater.update(plugin, "language/en-GB.yml", enGB);
            } catch (IOException e) {
                this.log.warn("Unable to update en-GB language file: "+e);
            }
        }

        // zh-CN
        File zhCN = new File(this.plugin.getDataFolder() + File.separator + "language" + File.separator + "zh-CN.yml");
        if (!zhCN.exists()) {
            this.plugin.saveResource("language/zh-CN.yml", true);
        } else {
            try {
                ConfigUpdater.update(plugin, "language/zh-CN.yml", zhCN);
            } catch (IOException e) {
                this.log.warn("Unable to update zh-CN language file: "+e);
            }
        }

        // fr-FR
        File frFR = new File(this.plugin.getDataFolder() + File.separator + "language" + File.separator + "fr-FR.yml");
        if (!frFR.exists()) {
            this.plugin.saveResource("language/fr-FR.yml", true);
        } else {
            try {
                ConfigUpdater.update(plugin, "language/fr-FR.yml", frFR);
            } catch (IOException e) {
                this.log.warn("Unable to update fr-FR language file: "+e);
            }
        }

        // es-ES
        File esES = new File(this.plugin.getDataFolder() + File.separator + "language" + File.separator + "es-ES.yml");
        if (!esES.exists()) {
            this.plugin.saveResource("language/es-ES.yml", true);
        } else {
            try {
                ConfigUpdater.update(plugin, "language/es-ES.yml", frFR);
            } catch (IOException e) {
                this.log.warn("Unable to update es-ES language file: "+e);
            }
        }

        // ko-KR
        File koKR = new File(this.plugin.getDataFolder() + File.separator + "language" + File.separator + "ko-KR.yml");
        if (!koKR.exists()) {
            this.plugin.saveResource("language/ko-KR.yml", true);
        } else {
            try {
                ConfigUpdater.update(plugin, "language/ko-KR.yml", frFR);
            } catch (IOException e) {
                this.log.warn("Unable to update ko-KR language file: "+e);
            }
        }
    }

    /**
     * Migrates old Essence stuff (calls migrateFiles and migrateValues).
     */
    public void migrate() {
        this.migrateValues();
        this.migrateFiles();
    }

    /**
     * Migrates old Essence config values.
     */
    private void migrateValues() {
        // ECONOMY MODE (1.10.1)
        Files config = new Files(this.plugin.foundryConfig, this.plugin);
        config.load("config.yml");

        if (Objects.equals(config.get("economy.mode"), "OFF") || Objects.equals(config.get("economy.mode"), false)) {
            config.set("economy.mode", "ESSENCE");
            config.set("economy.enabled", false);
            config.save();
        }
    }

    /**
     * Migrates old Essence files.
     */
    private void migrateFiles() {
        Files f = new Files(this.plugin.foundryConfig, this.plugin);
        f.load("config.yml");

        // NEW LANGUAGE FILES.
        if (f.getInt("config-version") == 1) {
            Files lf = new Files(this.plugin.foundryConfig, this.plugin);
            lf.load("config.yml");

            if (Objects.equals(lf.get("language"), "en-gb")) {
                Files config = new Files(this.plugin.foundryConfig, this.plugin);
                config.load("config.yml");
                if (config.exists("language/en-gb.yml")) {
                    config.delete("language/en-gb.yml");
                }
                config.set("language", "en-GB");
                config.save();
                this.plugin.reloadConfig();
            }

            lf.close();
        }

        Logger log = new Logger(this.plugin.foundryConfig);

        // NEW PLACEHOLDER SYSTEM (1.9.0+)
        if (f.getInt("config-version") == 1) {
            log.info("Essence is updating your configuration file, please wait...");

            log.info("[1/4] Updating first join message...");
            String firstJoin = f.getString("chat.broadcasts.first-join");
            firstJoin = firstJoin.replace("{{essence-version}}", "%essence_version%");
            firstJoin = firstJoin.replace("{{minecraft-version}}", "%essence_minecraft_version%");
            firstJoin = firstJoin.replace("{{time}}", "%essence_time%");
            firstJoin = firstJoin.replace("{{date}}", "%essence_date%");
            firstJoin = firstJoin.replace("{{datetime}}", "%essence_datetime%");
            firstJoin = firstJoin.replace("{{player}}", "%essence_player%");
            f.set("chat.broadcasts.first-join", firstJoin);
            log.info("[1/4] Done.");

            log.info("[2/4] Updating join message...");
            String join = f.getString("chat.broadcasts.join");
            join = join.replace("{{essence-version}}", "%essence_version%");
            join = join.replace("{{minecraft-version}}", "%essence_minecraft_version%");
            join = join.replace("{{time}}", "%essence_time%");
            join = join.replace("{{date}}", "%essence_date%");
            join = join.replace("{{datetime}}", "%essence_datetime%");
            join = join.replace("{{player}}", "%essence_player%");
            f.set("chat.broadcasts.join", join);
            log.info("[2/4] Done.");

            log.info("[3/4] Updating leaving message...");
            String leave = f.getString("chat.broadcasts.leave");
            leave = leave.replace("{{essence-version}}", "%essence_version%");
            leave = leave.replace("{{minecraft-version}}", "%essence_minecraft_version%");
            leave = leave.replace("{{time}}", "%essence_time%");
            leave = leave.replace("{{date}}", "%essence_date%");
            leave = leave.replace("{{datetime}}", "%essence_datetime%");
            leave = leave.replace("{{player}}", "%essence_player%");
            f.set("chat.broadcasts.leave", leave);
            log.info("[3/4] Done.");

            log.info("[4/4] Updating MOTD...");
            String motd = f.getString("chat.motd");
            motd = motd.replace("{{essence-version}}", "%essence_version%");
            motd = motd.replace("{{minecraft-version}}", "%essence_minecraft_version%");
            motd = motd.replace("{{time}}", "%essence_time%");
            motd = motd.replace("{{date}}", "%essence_date%");
            motd = motd.replace("{{datetime}}", "%essence_datetime%");
            motd = motd.replace("{{player}}", "%essence_player%");
            f.set("chat.motd", motd);
            log.info("[4/4] Done.");

            f.set("config-version", 2);

            log.info("Done.");
            log.info("");
        }

        if (f.getInt("config-version") == 2) {
            log.info("Essence is updating your configuration file, please wait...");

            log.info("[1/4] Migrating kit module...");
            f.set("kit.spawn-kits", f.getStringList("spawn-kits"));
            f.remove("spawn-kits");
            log.info("[1/4] Done.");

            log.info("[2/4] Migrating chat module...");
            f.set("chat.manage-chat", f.getBoolean("chat.enabled"));
            f.set("chat.enabled", true);

            f.set("chat.broadcasts.first-join", f.getString("broadcasts.first-join"));
            f.set("chat.broadcasts.join", f.getString("broadcasts.join"));
            f.set("chat.broadcasts.leave", f.getString("broadcasts.leave"));
            f.remove("broadcasts.first-join");
            f.remove("broadcasts.join");
            f.remove("broadcasts.leave");

            if (f.getBoolean("motd.enabled")) {
                f.set("chat.motd", f.getString("motd.message"));
            } else {
                f.set("chat.motd", "false");
            }
            f.remove("motd.enabled");
            f.remove("motd.message");
            f.remove("motd");
            log.info("[2/4] Done.");

            log.info("[3/4] Migrating advanced settings...");
            f.set("advanced.update-check", f.getBoolean("update-check"));
            f.set("advanced.verbose", f.getBoolean("verbose"));
            f.set("advanced.playerdata.store-ip-address", f.getBoolean("playerdata.store-ip-address"));
            f.remove("update-check");
            f.remove("verbose");
            f.remove("playerdata.store-ip-address");
            log.info("[3/4] Done.");

            log.info("[4/4] Migrating disabled commands...");
            List<String> dc = f.getStringList("disabled-commands");
            f.remove("disabled-commands");

            f.set("disabled-commands.list", dc);
            f.set("disabled-commands.feedback", f.getBoolean("disabled-commands-feedback"));
            f.remove("disabled-commands-feedback");
            log.info("[4/4] Done.");

            f.set("config-version", 3);

            log.info("Done.");
            log.info("");
        }

        if (f.getInt("config-version") == 3) {
            log.info("Essence is updating your configuration file, please wait...");

            log.info("[1/1] Migrating disabled commands list...");
            List<String> dcl = f.getStringList("disabled-commands.list");
            f.remove("disabled-commands.list");
            f.set("disabled-commands", dcl);
            log.info("[1/1] Done.");

            f.set("config-version", 4);

            log.info("Done.");
            log.info("");
        }

        f.save();
    }
}
