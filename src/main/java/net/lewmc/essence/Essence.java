package net.lewmc.essence;

import com.tcoded.folialib.FoliaLib;
import net.lewmc.essence.commands.*;
import net.lewmc.essence.commands.admin.*;
import net.lewmc.essence.commands.chat.*;
import net.lewmc.essence.commands.economy.BalanceCommand;
import net.lewmc.essence.commands.economy.PayCommand;
import net.lewmc.essence.commands.inventories.*;
import net.lewmc.essence.commands.stats.*;
import net.lewmc.essence.commands.teleportation.*;
import net.lewmc.essence.commands.teleportation.home.*;
import net.lewmc.essence.commands.teleportation.home.team.*;
import net.lewmc.essence.commands.teleportation.tp.*;
import net.lewmc.essence.commands.teleportation.warp.*;
import net.lewmc.essence.events.*;
import net.lewmc.essence.tabcompleter.*;
import net.lewmc.essence.utils.CommandUtil;
import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.UpdateUtil;
import net.lewmc.essence.utils.economy.VaultEconomy;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
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
    private final LogUtil log = new LogUtil(this);

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
     * This function runs when Essence is enabled.
     */
    @Override
    public void onEnable() {

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

        UpdateUtil update = new UpdateUtil(this);
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

        getServer().getServicesManager().register(Economy.class, new VaultEconomy(this), this, ServicePriority.Highest);

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
        CommandUtil cmd = new CommandUtil(this, null);
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
        try {
            this.getCommand("essence").setExecutor(new EssenceCommands(this));

            this.getCommand("gamemode").setExecutor(new GamemodeCommands(this));
            this.getCommand("gmc").setExecutor(new GamemodeCommands(this));
            this.getCommand("gms").setExecutor(new GamemodeCommands(this));
            this.getCommand("gma").setExecutor(new GamemodeCommands(this));
            this.getCommand("gmsp").setExecutor(new GamemodeCommands(this));

            this.getCommand("anvil").setExecutor(new AnvilCommand(this));
            this.getCommand("cartography").setExecutor(new CartographyCommand(this));
            this.getCommand("craft").setExecutor(new CraftCommand(this));
            this.getCommand("enderchest").setExecutor(new EnderchestCommand(this));
            this.getCommand("grindstone").setExecutor(new GrindstoneCommand(this));
            this.getCommand("loom").setExecutor(new LoomCommand(this));
            this.getCommand("smithing").setExecutor(new SmithingCommand(this));
            this.getCommand("stonecutter").setExecutor(new StonecutterCommand(this));
            this.getCommand("trash").setExecutor(new TrashCommand(this));
            this.getCommand("kit").setExecutor(new KitCommand(this));

            this.getCommand("feed").setExecutor(new FeedCommand(this));
            this.getCommand("heal").setExecutor(new HealCommand(this));
            this.getCommand("repair").setExecutor(new RepairCommand(this));

            this.getCommand("tp").setExecutor(new TeleportCommand(this));
            this.getCommand("tpa").setExecutor(new TpaCommand(this));
            this.getCommand("tpaccept").setExecutor(new TpacceptCommand(this));
            this.getCommand("tpdeny").setExecutor(new TpdenyCommand(this));
            this.getCommand("tptoggle").setExecutor(new TptoggleCommand(this));
            this.getCommand("tpahere").setExecutor(new TpahereCommand(this));
            this.getCommand("tpcancel").setExecutor(new TpcancelCommand(this));
            this.getCommand("tprandom").setExecutor(new TprandomCommand(this));

            this.getCommand("home").setExecutor(new HomeCommand(this));
            this.getCommand("homes").setExecutor(new HomesCommand(this));
            this.getCommand("sethome").setExecutor(new SethomeCommand(this));
            this.getCommand("delhome").setExecutor(new DelhomeCommand(this));
            this.getCommand("thome").setExecutor(new ThomeCommand(this));
            this.getCommand("thomes").setExecutor(new ThomesCommand(this));
            this.getCommand("setthome").setExecutor(new SetthomeCommand(this));
            this.getCommand("delthome").setExecutor(new DelthomeCommand(this));

            this.getCommand("warp").setExecutor(new WarpCommand(this));
            this.getCommand("warps").setExecutor(new WarpsCommand(this));
            this.getCommand("setwarp").setExecutor(new SetwarpCommand(this));
            this.getCommand("delwarp").setExecutor(new DelwarpCommand(this));

            this.getCommand("spawn").setExecutor(new SpawnCommand(this));
            this.getCommand("setspawn").setExecutor(new SetspawnCommand(this));

            this.getCommand("back").setExecutor(new BackCommand(this));

            this.getCommand("broadcast").setExecutor(new BroadcastCommand(this));
            this.getCommand("msg").setExecutor(new MsgCommand(this));
            this.getCommand("reply").setExecutor(new ReplyCommand(this));
            this.getCommand("nick").setExecutor(new NickCommand(this));

            this.getCommand("pay").setExecutor(new PayCommand(this));
            this.getCommand("balance").setExecutor(new BalanceCommand(this));

            this.getCommand("team").setExecutor(new TeamCommands(this));

            this.getCommand("seen").setExecutor(new SeenCommand(this));
            this.getCommand("info").setExecutor(new InfoCommand(this));

            this.getCommand("invisible").setExecutor(new InvisibleCommand(this));

            this.getCommand("rules").setExecutor(new RulesCommands(this));
        } catch (NullPointerException e) {
            this.log.severe("Unable to load Essence commands.");
            this.log.severe(e.getMessage());
            this.log.severe("");
            this.log.severe(Arrays.toString(e.getStackTrace()));
            this.log.info("");
        }
    }

    /**
     * Loads and registers all tab completers.
     */
    private void loadTabCompleters() {
        getCommand("warp").setTabCompleter(new WarpTabCompleter(this));
        getCommand("delwarp").setTabCompleter(new WarpTabCompleter(this));

        getCommand("home").setTabCompleter(new HomeTabCompleter(this));
        getCommand("delhome").setTabCompleter(new HomeTabCompleter(this));

        getCommand("gamemode").setTabCompleter(new GamemodeTabCompleter());
        getCommand("gm").setTabCompleter(new GamemodeTabCompleter());

        getCommand("es").setTabCompleter(new EssenceTabCompleter());

        getCommand("team").setTabCompleter(new TeamTabCompleter());

        getCommand("tp").setTabCompleter(new TpTabCompleter());
    }

    /**
     * Loads and registers all the plugin's event handlers.
     */
    private void loadEventHandlers() {
        Bukkit.getServer().getPluginManager().registerEvents(new JoinEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDamageEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new RespawnEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerBedEnter(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LeaveEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerMove(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerChatEvent(this), this);
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
