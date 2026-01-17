package net.lewmc.essence;

import com.tcoded.folialib.FoliaLib;
import net.lewmc.essence.admin.ModuleAdmin;
import net.lewmc.essence.chat.ModuleChat;
import net.lewmc.essence.core.*;
import net.lewmc.essence.economy.ModuleEconomy;
import net.lewmc.essence.environment.ModuleEnvironment;
import net.lewmc.essence.gamemode.ModuleGamemode;
import net.lewmc.essence.inventory.ModuleInventory;
import net.lewmc.essence.kit.ModuleKit;
import net.lewmc.essence.stats.ModuleStats;
import net.lewmc.essence.team.ModuleTeam;
import net.lewmc.essence.teleportation.ModuleTeleportation;
import net.lewmc.essence.world.ModuleWorld;
import net.lewmc.foundry.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The main Essence class.
 */
public class Essence extends JavaPlugin {
    /**
     * The logging system.
     */
    public Logger log;

    /**
     * Stores pending teleport requests.
     * String = The requested player's name.
     * String[] = The requester and if the requested player should teleport to them or not ("true" or "false")
     */
    public ConcurrentMap<String, String[]> teleportRequests = new ConcurrentHashMap<>();

    /**
     * Stores pending player clears.
     * UUID = The requested player's UUID.
     * TypePendingRequests.TypePendingClears = Data
     */
    public ConcurrentMap<UUID, TypePendingRequests.TypePendingClears> pendingClears = new ConcurrentHashMap<>();

    /**
     * Stores pending teleports.
     * UUID = The requested player's UUID.
     */
    public List<UUID> teleportingPlayers = new ArrayList<>();

    /**
     * Stores message history.
     * CommandSender = The receiver.
     * CommandSender = The sender.
     */
    public ConcurrentMap<CommandSender, CommandSender> msgHistory = new ConcurrentHashMap<>();

    /**
     * Stores a cache of player data.
     */
    public ConcurrentMap<UUID, TypePlayer> players = new ConcurrentHashMap<>();

    /**
     * Store's Essence's configuration.
     */
    public ConcurrentMap<String, Object> config;

    /**
     * Stores which players are flying.
     */
    public List<UUID> flyingPlayers = new ArrayList<>();

    /**
     * Stores update status.
     */
    public boolean hasPendingUpdate = false;

    /**
     * Manages random numbers.
     * Pseudo-random, not to be used in secure contexts.
     */
    public Random rand = new Random();

    /**
     * Holds the Foundry configuration.
     */
    public FoundryConfig foundryConfig;

    /**
     * Holds Essence's integrations.
     */
    public EssenceIntegrations integrations;

    /**
     * Handles if Essence should be verbose.
     */
    public boolean verbose;

    /**
     * Stores the language file.
     */
    public Files messageStore;

    /**
     * Checks if deferred tasks have been run (in Core/EventWorldLoad)
     */
    public boolean deferredTasksRun = false;

    /**
     * This function runs when Essence is enabled.
     */
    @Override
    public void onEnable() {
        this.foundryConfig = new FoundryConfig(this);
        this.foundryConfig.pluginId = "ES";
        this.log = new Logger(this.foundryConfig);

        this.log.info("");
        this.log.info("███████████████████████████████⟍");
        this.log.info("███████████████████████████████  ⟍    ┌─────── Essence by LewMC ────────");
        this.log.info("██           █████         ████   │   │ Stress-free server utilities.");
        this.log.info("██           ████           ███   │   └ Version "+ this.getDescription().getVersion());
        this.log.info("██    ██████████    █████    ██   │");
        this.log.info("██    ██████████     ██████████   │");
        this.log.info("██          █████          ████   │   ┌── ‼ ── Found a problem?  ── ‼ ──");
        this.log.info("██          ███████         ███   │   │ Please report any problems to");
        this.log.info("██    ██████████████████     ██   │   │ our GitHub issues page at");
        this.log.info("██    ██████████    █████    ██   │   └ github.com/lewmc/essence");
        this.log.info("██           ████           ███   │");
        this.log.info("██           █████         ████   │");
        this.log.info("███████████████████████████████   │   ┌── ✓ ── Enjoying Essence? ── ✓ ──");
        this.log.info("███████████████████████████████   │   │ Support LewMC and Essence by");
        this.log.info("⟍                                 │   └ visiting lewmc.net/support");
        this.log.info("  ⟍ ──────────────────────────────┘");
        this.log.info("");
        this.log.info("Beginning startup...");
        this.log.info("");

        // Load order is sensitive - TEST IF CHANGING!
        this.checkConfigExists();
        UtilUpdate update = new UtilUpdate(this);
        update.migrate();
        this.startupConfig();
        update.VersionCheck();
        update.UpdateLanguage();

        if (this.verbose) {
            this.log.warn("Verbose mode is ENABLED.");
            this.log.warn("This will likely cause a lot of console spam.");
            this.log.warn("You should only enable this if you're having problems.");
            this.log.info("");
        }

        if (!Bukkit.getOnlineMode()) {
            this.log.severe(">> Your server is running in offline mode.");
            this.log.warn(">> Player data saved in offline mode may not work properly if you switch back to online mode.");
            this.log.warn(">> Player data saved in online mode may not work properly in offline mode.");
            this.log.info("");
        }

        this.checkForEssentials();
        this.checkForPaper();
        this.initFileSystem();
        this.loadModules();

        this.integrations = new EssenceIntegrations(this);
        if (!this.integrations.loadPlaceholderAPI() && verbose) { this.log.warn("PlaceholderAPI not found! Using local placeholders."); }
        if (!this.integrations.loadVaultEconomy() && verbose) { this.log.warn("Vault not found or is disabled! Using local economy."); }
        if (!this.integrations.loadVaultChat() && verbose) { this.log.warn("Vault not found! Using local chat."); }
        this.integrations.loadMetrics();

        this.checkLanguageSystem();

        this.log.info("");
        this.log.info("Startup completed.");

        new Security(this.foundryConfig).startWatchdog();
    }

    /**
     * Checks if the server is running Paper, and informs the user that they should upgrade if not.
     */
    private void checkForPaper() {
        if (!new FoliaLib(this).isPaper() && !new FoliaLib(this).isFolia()) {
            this.log.severe("Essence no longer supports CraftBukkit or Spigot servers.");
            this.log.severe("Please upgrade to Paper to continue using Essence.");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            this.log.info("Running server jar: " + this.getServer().getName());
            if (this.verbose) {
                FoliaLib flib = new FoliaLib(this);
                this.log.info("Is Folia: " + flib.isFolia());
            }
            this.log.info("");
        }
    }

    /**
     * Checks if Essentials or EssentialsX is installed and disables the plugin if it is.
     */
    private void checkForEssentials() {
        if (getServer().getPluginManager().getPlugin("Essentials") != null || getServer().getPluginManager().getPlugin("EssentialsX") != null) {
            this.log.severe("Essentials is installed alongside Essence.");
            this.log.severe("Essence's commands clash with Essentials, this may cause issues later down the line.");
            this.log.severe("If you require commands that are in Essentials but not in Essence, please remove Essence from the server.");
            this.log.severe("All Essence commands are implemented in Essentials in a similar way.");
        }
    }

    /**
     * Initialise the file system.
     */
    private void initFileSystem() {
        // Language files are in UpdateUtil!
        // Config is in EssenceConfiguration.java

        File statsFolder = new File(getDataFolder() + File.separator + "data" + File.separator + "players");
        if (!statsFolder.exists() && !statsFolder.mkdirs()) {
            log.severe("Unable to make data folder.");
            log.warn("The plugin is being disabled, most of the plugin's features will not work without the data folder.");
            log.warn("Please create a folder called 'data' in the 'Essence' folder.");
            log.warn("Please create a folder called 'players' in the 'data' folder.");
            log.warn("Once this is complete, restart the server and Essence will re-enable.");
            this.log.info("");
            getServer().getPluginManager().disablePlugin(this);
        }


        File warpsFile = new File(getDataFolder() + File.separator + "data" + File.separator + "warps.yml");
        if (!warpsFile.exists()) {
            saveResource("data/warps.yml", false);
        }

        File spawnsFile = new File(getDataFolder() + File.separator + "data" + File.separator + "worlds.yml");
        if (!spawnsFile.exists()) {
            saveResource("data/worlds.yml", false);
        }

        File kitsFile = new File(getDataFolder() + File.separator + "data" + File.separator + "kits.yml");
        if (!kitsFile.exists()) {
            saveResource("data/kits.yml", false);
        }

        File rulesFile = new File(getDataFolder() + File.separator + "rules.txt");
        if (!rulesFile.exists()) {
            saveResource("rules.txt", false);
        }
    }

    /**
     * Checks the language system.
     */
    private void checkLanguageSystem() {
        File setLang = new File(getDataFolder() + File.separator + "language" + File.separator + this.config.get("language") + ".yml");
        if (!setLang.exists()) {
            this.log.severe("Language file '" + this.config.get("language") + "' does not exist!");
            this.log.severe("Please check the file and try again.");
            getServer().getPluginManager().disablePlugin(this);
        }

        this.messageStore = new Files(this.foundryConfig, this);
        this.messageStore.load("language/"+this.config.get("language")+".yml");
    }

    /**
     * Loads Essence's modules.
     * @since 1.10.0
     */
    private void loadModules() {
        Registry reg = new Registry(this.foundryConfig, this);

        new ModuleCore(this, reg);

        if ((boolean) this.config.get("admin.enabled")) { new ModuleAdmin(this, reg); if (this.verbose) { this.log.info("Loaded module: ADMIN"); } } else { if (this.verbose) { this.log.warn("Disabled module: ADMIN"); } }
        if ((boolean) this.config.get("chat.enabled")) { new ModuleChat(this, reg); if (this.verbose) { this.log.info("Loaded module: CHAT"); } } else { if (this.verbose) { this.log.warn("Disabled module: CHAT"); } }
        if ((boolean) this.config.get("economy.enabled")) { new ModuleEconomy(this, reg); if (this.verbose) { this.log.info("Loaded module: ECONOMY"); } } else { if (this.verbose) { this.log.warn("Disabled module: ECONOMY"); } }
        if ((boolean) this.config.get("environment.enabled")) { new ModuleEnvironment(this, reg); if (this.verbose) { this.log.info("Loaded module: ENVIRONMENT"); } } else { if (this.verbose) { this.log.warn("Disabled module: ENVIRONMENT"); } }
        if ((boolean) this.config.get("gamemode.enabled")) { new ModuleGamemode(this, reg); if (this.verbose) { this.log.info("Loaded module: GAMEMODE"); } } else { if (this.verbose) { this.log.warn("Disabled module: GAMEMODE"); } }
        if ((boolean) this.config.get("inventory.enabled")) { new ModuleInventory(this, reg); if (this.verbose) { this.log.info("Loaded module: INVENTORY"); } } else { if (this.verbose) { this.log.warn("Disabled module: INVENTORY"); } }
        if ((boolean) this.config.get("kit.enabled")) { new ModuleKit(this, reg); if (this.verbose) { this.log.info("Loaded module: KIT"); } } else { if (this.verbose) { this.log.warn("Disabled module: KIT"); } }
        if ((boolean) this.config.get("stats.enabled")) { new ModuleStats(this, reg); if (this.verbose) { this.log.info("Loaded module: STATS"); } } else { if (this.verbose) { this.log.warn("Disabled module: STATS"); } }
        if ((boolean) this.config.get("team.enabled")) { new ModuleTeam(this, reg); if (this.verbose) { this.log.info("Loaded module: TEAM"); } } else { if (this.verbose) { this.log.warn("Disabled module: TEAM"); } }
        if ((boolean) this.config.get("teleportation.enabled")) { new ModuleTeleportation(this, reg); if (this.verbose) { this.log.info("Loaded module: TELEPORTATION"); } } else { if (this.verbose) { this.log.warn("Disabled module: TELEPORTATION"); } }
        if ((boolean) this.config.get("world.enabled")) { new ModuleWorld(this, reg); if (this.verbose) { this.log.info("Loaded module: WORLD"); } } else { if (this.verbose) { this.log.warn("Disabled module: WORLD"); } }
    }

    /**
     * Starts Essence's config.
     * @since 1.10.1
     */
    public void startupConfig() {
        this.config = new EssenceConfiguration(this).startup();
        this.verbose = (boolean) this.config.get("advanced.verbose");
        this.foundryConfig.verbose = this.verbose;
    }

    /**
     * Checks if the configuration file exists. If it doesn't, it creates it.
     * @since 1.10.2
     */
    private void checkConfigExists() {
        Files configFile = new Files(this.foundryConfig, this);

        if (!configFile.exists("config.yml")) {
            this.saveDefaultConfig();
            if (!configFile.exists("config.yml")) {
                Logger log = new Logger(this.foundryConfig);
                log.severe("The server was unable to create Essence's configuration file!");
                log.severe("You can download a blank config file from the link below.");
                log.severe("https://github.com/LewMC/Essence/blob/main/src/main/resources/config.yml");
                log.severe("Please contact lewmc.net/help for help and to report the issue.");
            }
        }
    }

    @Override
    public void onDisable() {
        new FoliaLib(this).getScheduler().cancelAllTasks();
        if (this.messageStore != null) {
            this.messageStore.close();
        }
    }
}
