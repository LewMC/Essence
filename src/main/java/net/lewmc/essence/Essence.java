package net.lewmc.essence;

import com.tcoded.folialib.FoliaLib;
import net.lewmc.essence.commands.KitCommand;
import net.lewmc.essence.commands.TeamCommands;
import net.lewmc.essence.commands.admin.InfoCommand;
import net.lewmc.essence.commands.admin.SeenCommand;
import net.lewmc.essence.commands.chat.*;
import net.lewmc.essence.commands.economy.BalanceCommand;
import net.lewmc.essence.commands.economy.PayCommand;
import net.lewmc.essence.commands.inventories.*;
import net.lewmc.essence.commands.EssenceCommands;
import net.lewmc.essence.commands.GamemodeCommands;
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
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

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
     * The Vault economy handler.
     */
    private Economy economy = null;

    /**
     * Stores pending teleport requests.
     * String = The requested player's name.
     * String[] = The requester and if the requested player should teleport to them or not ("true" or "false")
     */
    public HashMap<String, String[]> teleportRequests = new HashMap<>();

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
        this.log.info("Running Essence version "+this.getDescription().getVersion()+ ".");
        this.log.info("Please report any issues with Essence to our GitHub repository: https://github.com/lewmc/essence/issues");
        this.log.info("");
        this.log.info("Please consider leaving us a review at https://www.spigotmc.org/resources/essence.114553");
        this.log.info("");
        this.log.info("Beginning startup...");
        this.log.info("");
        int pluginId = 20768; // <-- Replace with the id of your plugin!
        new Metrics(this, pluginId);

        this.verbose = this.getConfig().getBoolean("verbose");

        if (!Bukkit.getOnlineMode()) {
            this.log.severe(">> Your server is running in offline mode.");
            this.log.warn(">> Homes set in offline mode may not save properly if you switch back to online mode.");
            this.log.warn(">> Homes set in online mode may not work properly in offline mode.");
            this.log.info("");
        }

        checkForEssentials();
        checkForPaper();

        initFileSystem();
        loadCommands();
        loadEventHandlers();
        loadTabCompleters();

        if (!setupEconomy()) {
            this.log.warn("Vault not found! Using local economy.");
        }

        UpdateUtil update = new UpdateUtil(this);
        update.VersionCheck();
        update.UpdateConfig();
        update.UpdateLanguage();

        this.log.info("Startup completed.");
    }

    /**
     * Sets up Vault to use Essence's economy.
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

        if (this.economy == null) {
            this.log.severe("Economy provider is null!");
        }

        return this.economy != null;
    }

    /**
     * Checks if the server is running Paper, and informs the user that they should upgrade if not.
     */
    private void checkForPaper() {
        CommandUtil cmd = new CommandUtil(this);
        if (!cmd.isPaperCompatible()) {
            this.log.severe("You are running " + this.getServer().getName());
            this.log.severe("Some commands have been disabled, please see https://bit.ly/essencepaper for help.");
            this.log.severe("To get full plugin support please consider using Paper instead.");
            this.log.severe("You can download it from https://papermc.io");
        } else {
            this.log.info("Running server jar: "+ this.getServer().getName());
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
        if (
            getServer().getPluginManager().getPlugin("Essentials") != null ||
            getServer().getPluginManager().getPlugin("EssentialsX") != null
        ) {
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

        File enGB = new File(getDataFolder() + File.separator + "language" + File.separator + "en-GB.yml");
        if (!enGB.exists()) {
            saveResource("language/en-GB.yml", false);
        }

        File zhCN = new File(getDataFolder() + File.separator + "language" + File.separator + "zh-CN.yml");
        if (!zhCN.exists()) {
            saveResource("language/zh-CN.yml", false);
        }

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
    }

    /**
     * Loads and registers the plugin's command handlers.
     */
    private void loadCommands() {
        try {
            CommandUtil command = new CommandUtil(this);
            
            if (command.isEnabled("essence")) { this.getCommand("essence").setExecutor(new EssenceCommands(this)); }

            if (command.isEnabled("gamemode")) { this.getCommand("gamemode").setExecutor(new GamemodeCommands(this)); }
            if (command.isEnabled("gmc")) { this.getCommand("gmc").setExecutor(new GamemodeCommands(this)); }
            if (command.isEnabled("gms")) { this.getCommand("gms").setExecutor(new GamemodeCommands(this)); }
            if (command.isEnabled("gma")) { this.getCommand("gma").setExecutor(new GamemodeCommands(this)); }
            if (command.isEnabled("gmsp")) { this.getCommand("gmsp").setExecutor(new GamemodeCommands(this)); }

            if (command.isEnabled("anvil")) { this.getCommand("anvil").setExecutor(new AnvilCommand(this)); }
            if (command.isEnabled("cartography")) { this.getCommand("cartography").setExecutor(new CartographyCommand(this)); }
            if (command.isEnabled("craft")) { this.getCommand("craft").setExecutor(new CraftCommand(this)); }
            if (command.isEnabled("enderchest")) { this.getCommand("enderchest").setExecutor(new EnderchestCommand(this)); }
            if (command.isEnabled("grindstone")) { this.getCommand("grindstone").setExecutor(new GrindstoneCommand(this)); }
            if (command.isEnabled("loom")) { this.getCommand("loom").setExecutor(new LoomCommand(this)); }
            if (command.isEnabled("smithing")) { this.getCommand("smithing").setExecutor(new SmithingCommand(this)); }
            if (command.isEnabled("stonecutter")) { this.getCommand("stonecutter").setExecutor(new StonecutterCommand(this)); }
            if (command.isEnabled("trash")) { this.getCommand("trash").setExecutor(new TrashCommand(this)); }
            if (command.isEnabled("kit")) { this.getCommand("kit").setExecutor(new KitCommand(this)); }

            if (command.isEnabled("feed")) { this.getCommand("feed").setExecutor(new FeedCommand(this)); }
            if (command.isEnabled("heal")) { this.getCommand("heal").setExecutor(new HealCommand(this)); }
            if (command.isEnabled("repair")) { this.getCommand("repair").setExecutor(new RepairCommand(this)); }

            if (command.isEnabled("tp")) { this.getCommand("tp").setExecutor(new TeleportCommand(this)); }
            if (command.isEnabled("tpa")) { this.getCommand("tpa").setExecutor(new TpaCommand(this)); }
            if (command.isEnabled("tpaccept")) { this.getCommand("tpaccept").setExecutor(new TpacceptCommand(this)); }
            if (command.isEnabled("tpdeny")) { this.getCommand("tpdeny").setExecutor(new TpdenyCommand(this)); }
            if (command.isEnabled("tptoggle")) { this.getCommand("tptoggle").setExecutor(new TptoggleCommand(this)); }
            if (command.isEnabled("tpahere")) { this.getCommand("tpahere").setExecutor(new TpahereCommand(this)); }
            if (command.isEnabled("tpcancel")) { this.getCommand("tpcancel").setExecutor(new TpcancelCommand(this)); }
            if (command.isEnabled("tprandom")) { this.getCommand("tprandom").setExecutor(new TprandomCommand(this)); }

            if (command.isEnabled("home")) { this.getCommand("home").setExecutor(new HomeCommand(this)); }
            if (command.isEnabled("homes")) { this.getCommand("homes").setExecutor(new HomesCommand(this)); }
            if (command.isEnabled("sethome")) { this.getCommand("sethome").setExecutor(new SethomeCommand(this)); }
            if (command.isEnabled("delhome")) { this.getCommand("delhome").setExecutor(new DelhomeCommand(this)); }
            if (command.isEnabled("thome")) { this.getCommand("thome").setExecutor(new ThomeCommand(this)); }
            if (command.isEnabled("thomes")) { this.getCommand("thomes").setExecutor(new ThomesCommand(this)); }
            if (command.isEnabled("setthome")) { this.getCommand("setthome").setExecutor(new SetthomeCommand(this)); }
            if (command.isEnabled("delthome")) { this.getCommand("delthome").setExecutor(new DelthomeCommand(this)); }

            if (command.isEnabled("warp")) { this.getCommand("warp").setExecutor(new WarpCommand(this)); }
            if (command.isEnabled("warps")) { this.getCommand("warps").setExecutor(new WarpsCommand(this)); }
            if (command.isEnabled("setwarp")) { this.getCommand("setwarp").setExecutor(new SetwarpCommand(this)); }
            if (command.isEnabled("delwarp")) { this.getCommand("delwarp").setExecutor(new DelwarpCommand(this)); }

            if (command.isEnabled("spawn")) { this.getCommand("spawn").setExecutor(new SpawnCommand(this)); }
            if (command.isEnabled("setspawn")) { this.getCommand("setspawn").setExecutor(new SetspawnCommand(this)); }

            if (command.isEnabled("back")) { this.getCommand("back").setExecutor(new BackCommand(this)); }

            if (command.isEnabled("broadcast")) { this.getCommand("broadcast").setExecutor(new BroadcastCommand(this)); }

            if (command.isEnabled("pay")) { this.getCommand("pay").setExecutor(new PayCommand(this)); }
            if (command.isEnabled("balance")) { this.getCommand("balance").setExecutor(new BalanceCommand(this)); }

            if (command.isEnabled("team")) { this.getCommand("team").setExecutor(new TeamCommands(this)); }

            if (command.isEnabled("seen")) { this.getCommand("seen").setExecutor(new SeenCommand(this)); }
            if (command.isEnabled("info")) { this.getCommand("info").setExecutor(new InfoCommand(this)); }
        } catch (NullPointerException e) {
            this.log.severe("LoadCommands: Unable to load Essence commands.");
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
    }
}
