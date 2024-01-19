package net.lewmc.essence;

import net.lewmc.essence.commands.chat.*;
import net.lewmc.essence.commands.economy.BalanceCommand;
import net.lewmc.essence.commands.economy.PayCommand;
import net.lewmc.essence.commands.inventories.*;
import net.lewmc.essence.commands.EssenceCommands;
import net.lewmc.essence.commands.GamemodeCommands;
import net.lewmc.essence.commands.stats.*;
import net.lewmc.essence.commands.teleportation.*;
import net.lewmc.essence.events.JoinEvent;
import net.lewmc.essence.tabcompleter.*;
import net.lewmc.essence.utils.ConfigUtil;
import net.lewmc.essence.utils.LogUtil;
import net.lewmc.essence.utils.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Essence extends JavaPlugin {
    private final LogUtil log = new LogUtil(this);
    private boolean isDisabled = false;

    @Override
    public void onEnable() {
        this.log.info("Beginning startup...");
        int pluginId = 20768; // <-- Replace with the id of your plugin!
        new Metrics(this, pluginId);

        if (!Bukkit.getOnlineMode()) {
            this.log.severe("Your server is running in offline mode.");
            this.log.warn("Homes set in offline mode may not save properly if you switch back to online mode.");
            this.log.warn("Homes set in online mode may not work properly in offline mode.");
        }

        checkForEssentials();

        if (!isDisabled) {
            initFileSystem();
            loadCommands();
            loadEventHandlers();
            loadTabCompleters();

            ConfigUtil cu = new ConfigUtil(this);
            cu.UpdateConfig();

            this.log.info("Startup completed.");
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
            this.isDisabled = true;
            this.log.severe("Essentials is installed alongside Essence.");
            this.log.severe("Essence's commands clash with Essentials, to prevent issues with data Essence is now disabled.");
            this.log.severe("If you require commands that are in Essentials but not in Essence, please remove Essence from the server.");
            this.log.severe("All Essence commands are implemented in Essentials in a similar way.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {}

    /**
     * Initialise the file system.
     */
    private void initFileSystem() {
        saveDefaultConfig();

        saveResource("data/warps.yml", false);

        File statsFolder = new File(getDataFolder() + File.separator + "data" + File.separator + "players");
        if (!statsFolder.exists()) {
            if (!statsFolder.mkdirs()) {
                log.severe("Unable to make data folder.");
                log.warn("The plugin is being disabled, most of the plugin's features will not work without the data folder.");
                log.warn("Please create a folder called 'data' in the 'Essence' folder.");
                log.warn("Please create a folder called 'players' in the 'data' folder.");
                log.warn("Once this is complete, restart the server and Essence will re-enable.");
                getServer().getPluginManager().disablePlugin(this);
            }
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

            this.getCommand("feed").setExecutor(new FeedCommand(this));
            this.getCommand("heal").setExecutor(new HealCommand(this));

            this.getCommand("delhome").setExecutor(new DelhomeCommand(this));
            this.getCommand("delwarp").setExecutor(new DelwarpCommand(this));
            this.getCommand("home").setExecutor(new HomeCommand(this));
            this.getCommand("homes").setExecutor(new HomesCommand(this));
            this.getCommand("sethome").setExecutor(new SethomeCommand(this));
            this.getCommand("setwarp").setExecutor(new SetwarpCommand(this));
            this.getCommand("tp").setExecutor(new TeleportCommand(this));
            this.getCommand("warp").setExecutor(new WarpCommand(this));
            this.getCommand("warps").setExecutor(new WarpsCommand(this));
            this.getCommand("back").setExecutor(new BackCommand(this));

            this.getCommand("broadcast").setExecutor(new BroadcastCommand(this));

            this.getCommand("pay").setExecutor(new PayCommand(this));
            this.getCommand("balance").setExecutor(new BalanceCommand(this));
        } catch (NullPointerException e) {
            this.log.severe("LoadCommands: Unable to load Essence commands.");
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
    }

    /**
     * Loads and registers all the plugin's event handlers.
     */
    private void loadEventHandlers() {
        Bukkit.getServer().getPluginManager().registerEvents(new JoinEvent(this), this);
    }
}
