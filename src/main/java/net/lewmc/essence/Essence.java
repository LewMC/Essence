package net.lewmc.essence;

import net.lewmc.essence.commands.TeamCommands;
import net.lewmc.essence.commands.chat.*;
import net.lewmc.essence.commands.economy.BalanceCommand;
import net.lewmc.essence.commands.economy.PayCommand;
import net.lewmc.essence.commands.inventories.*;
import net.lewmc.essence.commands.EssenceCommands;
import net.lewmc.essence.commands.GamemodeCommands;
import net.lewmc.essence.commands.stats.*;
import net.lewmc.essence.commands.teleportation.*;
import net.lewmc.essence.events.DeathEvent;
import net.lewmc.essence.events.JoinEvent;
import net.lewmc.essence.events.PlayerDamageEvent;
import net.lewmc.essence.tabcompleter.*;
import net.lewmc.essence.utils.CommandUtil;
import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.UpdateUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Essence extends JavaPlugin {
    private final LogUtil log = new LogUtil(this);

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
        this.log.info("Please report any issues with Essence to our GitHub repository: https://github.com/lewmilburn/essence/issues");
        this.log.info("");
        this.log.info("Beginning startup...");
        this.log.info("");
        int pluginId = 20768; // <-- Replace with the id of your plugin!
        new Metrics(this, pluginId);

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

        UpdateUtil update = new UpdateUtil(this);
        update.VersionCheck();
        update.UpdateConfig();
        update.UpdateLanguage();

        this.log.info("Startup completed.");
    }

    private void checkForPaper() {
        CommandUtil cmd = new CommandUtil(this);
        if (!cmd.isPaper()) {
            this.log.severe("You are running " + this.getServer().getName());
            this.log.severe("Some commands have been disabled, please see https://bit.ly/essencepaper for help.");
            this.log.severe("To get full plugin support please consider using Paper instead.");
            this.log.severe("You can download it from https://papermc.io");
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

        File warpsFile = new File(getDataFolder() + File.separator + "data" + File.separator + "warps.yml");
        if (!warpsFile.exists()) {
            saveResource("data/warps.yml", false);
        }

        File languageFile = new File(getDataFolder() + File.separator + "language" + File.separator + "en-gb.yml");
        if (!languageFile.exists()) {
            saveResource("language/en-gb.yml", false);
        }

        File statsFolder = new File(getDataFolder() + File.separator + "data" + File.separator + "players");
        if (!statsFolder.exists()) {
            if (!statsFolder.mkdirs()) {
                log.severe("Unable to make data folder.");
                log.warn("The plugin is being disabled, most of the plugin's features will not work without the data folder.");
                log.warn("Please create a folder called 'data' in the 'Essence' folder.");
                log.warn("Please create a folder called 'players' in the 'data' folder.");
                log.warn("Once this is complete, restart the server and Essence will re-enable.");
                this.log.info("");
                getServer().getPluginManager().disablePlugin(this);
            }
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

            if (command.isEnabled("feed")) { this.getCommand("feed").setExecutor(new FeedCommand(this)); }
            if (command.isEnabled("heal")) { this.getCommand("heal").setExecutor(new HealCommand(this)); }

            if (command.isEnabled("delhome")) { this.getCommand("delhome").setExecutor(new DelhomeCommand(this)); }
            if (command.isEnabled("delwarp")) { this.getCommand("delwarp").setExecutor(new DelwarpCommand(this)); }
            if (command.isEnabled("home")) { this.getCommand("home").setExecutor(new HomeCommand(this)); }
            if (command.isEnabled("homes")) { this.getCommand("homes").setExecutor(new HomesCommand(this)); }
            if (command.isEnabled("sethome")) { this.getCommand("sethome").setExecutor(new SethomeCommand(this)); }
            if (command.isEnabled("setwarp")) { this.getCommand("setwarp").setExecutor(new SetwarpCommand(this)); }
            if (command.isEnabled("tp")) { this.getCommand("tp").setExecutor(new TeleportCommand(this)); }
            if (command.isEnabled("tprandom")) { this.getCommand("tprandom").setExecutor(new TprandomCommand(this)); }
            if (command.isEnabled("warp")) { this.getCommand("warp").setExecutor(new WarpCommand(this)); }
            if (command.isEnabled("warps")) { this.getCommand("warps").setExecutor(new WarpsCommand(this)); }
            if (command.isEnabled("back")) { this.getCommand("back").setExecutor(new BackCommand(this)); }

            if (command.isEnabled("broadcast")) { this.getCommand("broadcast").setExecutor(new BroadcastCommand(this)); }

            if (command.isEnabled("pay")) { this.getCommand("pay").setExecutor(new PayCommand(this)); }
            if (command.isEnabled("balance")) { this.getCommand("balance").setExecutor(new BalanceCommand(this)); }

            if (command.isEnabled("team")) { this.getCommand("team").setExecutor(new TeamCommands(this)); }
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
    }
}
