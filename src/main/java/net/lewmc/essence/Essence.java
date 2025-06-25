package net.lewmc.essence;

import com.tcoded.folialib.FoliaLib;
import net.lewmc.essence.admin.ModuleAdmin;
import net.lewmc.essence.chat.ModuleChat;
import net.lewmc.essence.core.ModuleCore;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilUpdate;
import net.lewmc.essence.economy.ModuleEconomy;
import net.lewmc.essence.environment.ModuleEnvironment;
import net.lewmc.essence.gamemode.ModuleGamemode;
import net.lewmc.essence.inventory.ModuleInventory;
import net.lewmc.essence.kit.ModuleKit;
import net.lewmc.essence.stats.ModuleStats;
import net.lewmc.essence.team.ModuleTeam;
import net.lewmc.essence.teleportation.ModuleTeleportation;
import net.lewmc.foundry.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

/**
 * The main Essence class.
 */
public class Essence extends JavaPlugin {
    /**
     * The logging system.
     */
    private Logger log;

    /**
     * The config.yml's verbose value is stored here.
     */
    public boolean verbose;

    /**
     * The config.yml's disabled-commands-feedback value is stored here.
     */
    public boolean disabledCommandsFeedback;

    /**
     * Stores pending teleport requests.
     * String = The requested player's name.
     * String[] = The requester and if the requested player should teleport to them or not ("true" or "false")
     */
    public Map<String, String[]> teleportRequests = new HashMap<>();

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
    public Map<CommandSender, CommandSender> msgHistory = new HashMap<>();

    /**
     * Stores which commands are disabled.
     */
    public List<String> disabledCommands;

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
     * The nameFormat to use in chats.
     */
    public String chat_nameFormat;

    /**
     * Stores if Essence should manage chat messages or not
     */
    public boolean chat_manage = false;

    /**
     * Should essence chat allow colours?
     */
    public boolean chat_allowMessageFormatting;

    /**
     * Economy symbol.
     */
    public String economySymbol = "$";

    /**
     * Holds the Foundry configuration.
     */
    public FoundryConfig config;

    /**
     * Holds Essence's integrations.
     */
    public EssenceIntegrations integrations;

    /**
     * This function runs when Essence is enabled.
     */
    @Override
    public void onEnable() {
        this.config = new FoundryConfig(this);
        this.log = new Logger(this.config);

        this.log.info("");
        this.log.info("███████╗░██████╗░██████╗███████╗███╗░░██╗░█████╗░███████╗");
        this.log.info("██╔════╝██╔════╝██╔════╝██╔════╝████╗░██║██╔══██╗██╔════╝");
        this.log.info("█████╗░░╚█████╗░╚█████╗░█████╗░░██╔██╗██║██║░░╚═╝█████╗░░");
        this.log.info("██╔══╝░░░╚═══██╗░╚═══██╗██╔══╝░░██║╚████║██║░░██╗██╔══╝░░");
        this.log.info("███████╗██████╔╝██████╔╝███████╗██║░╚███║╚█████╔╝███████╗");
        this.log.info("╚══════╝╚═════╝░╚═════╝░╚══════╝╚═╝░░╚══╝░╚════╝░╚══════╝");
        this.log.info("");
        this.log.info("Running Essence version " + this.getDescription().getVersion() + ".");
        this.log.info("Please report any issues with Essence to our GitHub repository: https://github.com/lewmc/essence/issues");
        this.log.info("");
        this.log.info("Please consider leaving us a review at https://www.spigotmc.org/resources/essence.114553");
        this.log.info("");
        this.log.info("Beginning startup...");
        this.log.info("");

        this.verbose = this.getConfig().getBoolean("verbose");

        if (this.verbose) {
            this.log.warn("Verbose mode is ENABLED.");
            this.log.warn("This will likely cause a lot of console spam.");
            this.log.warn("You should only enable this if you're having problems.");
            this.log.info("");
        }

        this.disabledCommandsFeedback = this.getConfig().getBoolean("disabled-commands-feedback");

        if (!Bukkit.getOnlineMode()) {
            this.log.severe(">> Your server is running in offline mode.");
            this.log.warn(">> Homes set in offline mode may not save properly if you switch back to online mode.");
            this.log.warn(">> Homes set in online mode may not work properly in offline mode.");
            this.log.info("");
        }

        this.disabledCommands = this.getConfig().getStringList("disabled-commands");

        this.checkForEssentials();
        this.checkForPaper();
        this.initFileSystem();
        this.loadChatFormat();
        this.loadModules();

        UtilUpdate update = new UtilUpdate(this);
        update.VersionCheck();
        update.UpdateConfig();
        update.UpdateLanguage();

        this.integrations = new EssenceIntegrations(this);
        if (!this.integrations.loadPlaceholderAPI() && verbose) { this.log.warn("PlaceholderAPI not found! Using local placeholders."); }
        if (!this.integrations.loadVaultEconomy() && verbose) { this.log.warn("Vault not found! Using local economy."); }
        if (!this.integrations.loadVaultChat() && verbose) { this.log.warn("Vault not found! Using local chat."); }
        this.integrations.loadMetrics();

        this.checkLanguageSystem();

        this.log.info("");
        this.log.info("Startup completed.");

        if (Objects.equals(System.getProperty("ESSENCE_LOADED", ""), "TRUE")) {
            this.log.severe("");
            this.log.severe("WARNING: RELOAD DETECTED!");
            this.log.severe("");
            this.log.severe("This may cause issues with Essence, other plugins, and your server overall.");
            this.log.severe("These issues include breaking permissions and other crashing exceptions.");
            this.log.severe("If you are reloading datapacks use /minecraft:reload instead.");
            this.log.severe("");
            this.log.severe("WE HIGHLY RECOMMEND RESTARTING YOUR SERVER.");
            this.log.severe("");
            this.log.severe("We will not provide support for any issues when plugin reloaders are used.");
            this.log.severe("");
            this.log.severe("More info: https://madelinemiller.dev/blog/problem-with-reload");
            this.log.severe("");
        }

        System.setProperty("ESSENCE_LOADED", "TRUE");
    }

    /**
     * Checks if the server is running Paper, and informs the user that they should upgrade if not.
     */
    private void checkForPaper() {
        UtilCommand cmd = new UtilCommand(this, null);
        if (!cmd.isPaperCompatible()) {
            this.log.severe("You are running " + this.getServer().getName());
            this.log.severe("Some commands have been disabled, please see https://bit.ly/essencepaper for help.");
            this.log.severe("To get full plugin support please consider using Paper instead.");
            this.log.severe("You can download it from https://papermc.io");
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
        saveDefaultConfig();

        // Language files are in UpdateUtil!

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

        File spawnsFile = new File(getDataFolder() + File.separator + "data" + File.separator + "spawns.yml");
        if (!spawnsFile.exists()) {
            saveResource("data/spawns.yml", false);
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
        File setLang = new File(getDataFolder() + File.separator + "language" + File.separator + getConfig().getString("language") + ".yml");
        if (!setLang.exists()) {
            this.log.severe("Language file '" + getConfig().getString("language") + "' does not exist!");
            this.log.severe("Please check the file and try again.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    /**
     * Loads Essence's modules.
     * @since 1.10.0
     */
    private void loadModules() {
        Registry reg = new Registry(this.config, this);
        
        new ModuleAdmin(this, reg);
        new ModuleChat(this, reg);
        new ModuleCore(this, reg);
        new ModuleEconomy(this, reg);
        new ModuleEnvironment(this, reg);
        new ModuleGamemode(this, reg);
        new ModuleInventory(this, reg);
        new ModuleKit(this, reg);
        new ModuleStats(this, reg);
        new ModuleTeam(this, reg);
        new ModuleTeleportation(this, reg);
    }

    private void loadChatFormat() {
        this.chat_nameFormat = this.getConfig().getString("chat.name-format");
        this.chat_manage = this.getConfig().getBoolean("chat.enabled");
        this.chat_allowMessageFormatting = this.getConfig().getBoolean("chat.allow-message-formatting");
    }
}
