package net.lewmc.essence;

import com.tcoded.folialib.FoliaLib;
import net.lewmc.essence.global.*;
import net.lewmc.essence.module.admin.*;
import net.lewmc.essence.module.chat.*;
import net.lewmc.essence.module.core.CommandEssence;
import net.lewmc.essence.module.core.CommandRules;
import net.lewmc.essence.module.core.TabCompleterEssence;
import net.lewmc.essence.module.economy.*;
import net.lewmc.essence.module.inventory.*;
import net.lewmc.essence.module.kit.CommandKit;
import net.lewmc.essence.module.stats.*;
import net.lewmc.essence.module.gamemode.CommandGamemode;
import net.lewmc.essence.module.gamemode.TabCompleterGamemode;
import net.lewmc.essence.module.team.TabCompleterTeam;
import net.lewmc.essence.module.team.CommandTeam;
import net.lewmc.essence.module.teleportation.*;
import net.lewmc.essence.module.teleportation.home.*;
import net.lewmc.essence.module.teleportation.home.team.*;
import net.lewmc.essence.module.teleportation.spawn.CommandSetspawn;
import net.lewmc.essence.module.teleportation.spawn.CommandSpawn;
import net.lewmc.essence.module.teleportation.tp.*;
import net.lewmc.essence.module.teleportation.warp.*;
import net.lewmc.essence.module.economy.UtilVaultEconomy;
import net.lewmc.foundry.*;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.*;
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
     * The Vault economy handler.
     */
    private Economy economy = null;

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
     * Stores if PlaceholderAPI is being used.
     */
    public boolean usingPAPI = false;

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
     * Vault chat
     */
    public Chat chat;

    /**
     * Economy symbol.
     */
    public String economySymbol = "$";

    /**
     * Holds the Foundry configuration.
     */
    public FoundryConfig config;

    /**
     * The Foundry registry.
     */
    private Registry registry;

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

        this.registry = new Registry(this.config, this);

        this.initFileSystem();
        this.loadCommands();
        this.loadEventHandlers();
        this.loadTabCompleters();
        this.loadChatFormat();

        if (!setupEconomy()) {
            this.log.warn("Vault not found! Using local economy.");
        }

        if (!setupChat()) {
            this.log.warn("Vault not found! Using local chat.");
        }

        UtilUpdate update = new UtilUpdate(this);
        update.VersionCheck();
        update.UpdateConfig();
        update.UpdateLanguage();

        this.registerPAPIExpansion();
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

        int pluginId = 20768;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SimplePie("language", () -> getConfig().getString("language")));
        if (economy == null) {
            metrics.addCustomChart(new SimplePie("economy_enabled", () -> "false"));
        } else {
            metrics.addCustomChart(new SimplePie("economy_enabled", () -> "true"));
        }
    }

    /**
     * Sets up Vault to use Essence's economy.
     *
     * @return boolean - If it could be setup correctly.
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        this.log.info("Vault found, setting up economy service...");

        getServer().getServicesManager().register(Economy.class, new UtilVaultEconomy(this), this, ServicePriority.Highest);

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            log.severe("No economy service provider found after registration!");
            return false;
        }

        this.economy = rsp.getProvider();

        this.log.info("");

        this.economySymbol = this.getConfig().getString("economy.symbol");

        return this.economy != null;
    }

    /**
     * Sets up Vault to use Essence's economy.
     *
     * @return boolean - If it could be setup correctly.
     */
    private boolean setupChat() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        this.log.info("Vault found, setting up chat service...");

        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp != null) {
            this.chat = rsp.getProvider();
        }

        this.log.info("");

        return this.chat != null;
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
     * Loads and registers the plugin's command handlers.
     */
    private void loadCommands() {
        this.registry.command("essence", new CommandEssence(this));

        this.registry.command(new String[] {"gamemode", "gmc", "gms", "gma", "gmsp"}, new CommandGamemode(this));

        this.registry.command("anvil", new CommandAnvil(this));
        this.registry.command("cartography", new CommandCartography(this));
        this.registry.command("craft", new CommandCraft(this));
        this.registry.command("enderchest", new CommandEnderchest(this));
        this.registry.command("grindstone", new CommandGrindstone(this));
        this.registry.command("loom", new CommandLoom(this));
        this.registry.command("smithing", new CommandSmithing(this));
        this.registry.command("stonecutter", new CommandStonecutter(this));
        this.registry.command("trash", new CommandTrash(this));

        this.registry.command("kit", new CommandKit(this));

        this.registry.command("feed", new CommandFeed(this));
        this.registry.command("heal", new CommandHeal(this));
        this.registry.command("repair", new CommandRepair(this));
        this.registry.command("invisible", new CommandInvisible(this));

        this.registry.command("tp", new CommandTeleport(this));
        this.registry.command("tpa", new CommandTpa(this));
        this.registry.command("tpaccept", new CommandTpaccept(this));
        this.registry.command("tpdeny", new CommandTpdeny(this));
        this.registry.command("tptoggle", new CommandTptoggle(this));
        this.registry.command("tpahere", new CommandTpahere(this));
        this.registry.command("tpcancel", new CommandTpcancel(this));
        this.registry.command("tprandom", new CommandTprandom(this));

        this.registry.command("home", new CommandHome(this));
        this.registry.command("homes", new CommandHomes(this));
        this.registry.command("sethome", new CommandSethome(this));
        this.registry.command("delhome", new CommandDelhome(this));
        this.registry.command("thome", new CommandThome(this));
        this.registry.command("thomes", new CommandThomas(this));
        this.registry.command("setthome", new CommandSetthome(this));
        this.registry.command("delthome", new CommandDelthomes(this));

        this.registry.command("warp", new CommandWarp(this));
        this.registry.command("warps", new CommandWarps(this));
        this.registry.command("setwarp", new CommandSetwarp(this));
        this.registry.command("delwarp", new CommandDelwarp(this));

        this.registry.command("spawn", new CommandSpawn(this));
        this.registry.command("setspawn", new CommandSetspawn(this));

        this.registry.command("back", new CommandBack(this));

        this.registry.command("broadcast", new CommandBroadcast(this));
        this.registry.command("msg", new CommandMsg(this));
        this.registry.command("reply", new CommandReply(this));
        this.registry.command("nick", new CommandNick(this));

        this.registry.command("pay", new CommandPay(this));
        this.registry.command("balance", new CommandBalance(this));

        this.registry.command("team", new CommandTeam(this));

        this.registry.command("seen", new CommandSeen(this));
        this.registry.command("info", new CommandInfo(this));

        this.registry.command("rules", new CommandRules(this));
    }

    /**
     * Loads and registers all tab completers.
     */
    private void loadTabCompleters() {
        this.registry.tabCompleter(new String[] { "warp", "delwarp" }, new TabCompleterWarp(this));
        this.registry.tabCompleter(new String[] { "home", "delhome" }, new TabCompleterHome(this));
        this.registry.tabCompleter(new String[] { "gamemode", "gm" }, new TabCompleterGamemode());
        this.registry.tabCompleter(new String[] { "es" }, new TabCompleterEssence());
        this.registry.tabCompleter(new String[] { "team" }, new TabCompleterTeam());
        this.registry.tabCompleter(new String[] { "tp" }, new TabCompleterTp());
    }

    /**
     * Loads and registers all the plugin's event handlers.
     */
    private void loadEventHandlers() {
        this.registry.event(new EventJoin(this));
        this.registry.event(new EventDeath(this));
        this.registry.event(new EventPlayerDamage(this));
        this.registry.event(new EventRespawn(this));
        this.registry.event(new EventPlayerBedEnter(this));
        this.registry.event(new EventLeave(this));
        this.registry.event(new EventPlayerMove(this));
        this.registry.event(new EventPlayerChat(this));
    }

    /**
     * Registers Essence's PlaceholderAPIExpansion
     */
    private void registerPAPIExpansion() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new EssencePAPIExpansion(this).register();
            usingPAPI = true;
            this.log.info("Placeholder API is installed, registered placeholders.");
        } else {
            usingPAPI = false;
            if (this.verbose) {
                this.log.info("Placeholder API is not installed, placeholders not registered.");
            }
        }
    }

    private void loadChatFormat() {
        this.chat_nameFormat = this.getConfig().getString("chat.name-format");
        this.chat_manage = this.getConfig().getBoolean("chat.enabled");
        this.chat_allowMessageFormatting = this.getConfig().getBoolean("chat.allow-message-formatting");
    }
}
