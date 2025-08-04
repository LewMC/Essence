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
        this.log = new Logger(plugin.config);
    }

    /**
     * Checks Essence's version.
     */
    public void VersionCheck() {
        if (this.plugin.getConfig().getBoolean("update-check")) {
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
                    } else {
                        log.warn("UPDATE > There's a new version of Essence available.");
                        log.warn("UPDATE > Your version: "+this.plugin.getDescription().getVersion()+" - latest version: "+response);
                        log.warn("UPDATE > You can download the latest version from lewmc.net/essence");
                        this.log.info("");
                        this.plugin.hasPendingUpdate = true;
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
     * Updates Essence's configuration.
     */
    public void UpdateConfig() {
        File configFile = new File(this.plugin.getDataFolder(), "config.yml");

        try {
            ConfigUpdater.update(plugin, "config.yml", configFile);
        } catch (IOException e) {
            this.log.warn("Unable to update configuration: "+e);
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
     * Migrates old Essence files.
     */
    public void migrate() {
        // NEW LANGUAGE FILES.
        if (Objects.equals(this.plugin.getConfig().getString("language"), "en-gb")) {
            Files config = new Files(this.plugin.config, this.plugin);
            config.load("config.yml");
            if (config.exists("language/en-gb.yml")) {
                config.delete("language/en-gb.yml");
            }
            config.set("language", "en-GB");
            config.save();
            this.plugin.reloadConfig();
        }

        Logger log = new Logger(this.plugin.config);

        // NEW PLACEHOLDER SYSTEM (1.9.0+)
        Files f = new Files(this.plugin.config, this.plugin);
        f.load("config.yml");
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
            f.delete("spawn-kits");
            log.info("[1/4] Done.");

            log.info("[2/4] Migrating chat module...");
            f.set("chat.manage-chat", f.getString("chat.enabled"));
            f.set("chat.enabled", true);

            f.set("chat.broadcasts.first-join", f.getString("broadcasts.first-join"));
            f.set("chat.broadcasts.join", f.getString("broadcasts.join"));
            f.set("chat.broadcasts.leave", f.getString("broadcasts.leave"));
            f.delete("broadcasts.first-join");
            f.delete("broadcasts.join");
            f.delete("broadcasts.leave");

            if (f.getBoolean("motd.enabled")) {
                f.set("chat.motd", f.getString("motd.message"));
            } else {
                f.set("chat.motd", "false");
            }
            f.delete("motd.enabled");
            f.delete("motd.message");
            f.delete("motd");
            log.info("[2/4] Done.");

            log.info("[3/4] Migrating advanced settings...");
            f.set("advanced.update-check", f.getBoolean("update-check"));
            f.set("advanced.console-prefix", f.getBoolean("console-prefix"));
            f.set("advanced.verbose", f.getBoolean("verbose"));
            f.set("advanced.playerdata.store-ip-address", f.getBoolean("playerdata.store-ip-address"));
            f.delete("update-check");
            f.delete("console-prefix");
            f.delete("verbose");
            f.delete("playerdata.store-ip-address");
            log.info("[3/4] Done.");

            log.info("[4/4] Migrating disabled commands...");
            List<String> dc = f.getStringList("disabled-commands");
            f.delete("disabled-commands");

            f.set("disabled-commands.list", dc);
            f.set("disabled-commands.feedback", f.getBoolean("disabled-commands-feedback"));
            f.delete("disabled-commands-feedback");
            log.info("[4/4] Done.");

            f.set("config-version", 3);

            log.info("Done.");
            log.info("");
        }

        f.save();
        f.close();
    }
}
