package net.lewmc.essence;

import net.lewmc.essence.commands.inventories.*;
import net.lewmc.essence.commands.EssenceCommands;
import net.lewmc.essence.commands.GamemodeCommands;
import net.lewmc.essence.commands.stats.HealCommand;
import net.lewmc.essence.commands.stats.FeedCommand;
import net.lewmc.essence.commands.teleportation.*;
import net.lewmc.essence.events.JoinEvent;
import net.lewmc.essence.utils.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Essence extends JavaPlugin {
    private LogUtil log = new LogUtil(this);

    @Override
    public void onEnable() {
        this.log.info("Beginning startup.");
        if (!Bukkit.getOnlineMode()) {
            this.log.severe("Your server is running in offline mode.");
            this.log.warn("Homes set in offline mode may not save properly if you switch back to online mode.");
            this.log.warn("Homes set in online mode may not work properly in offline mode.");
        }

        this.initFileSystem();

        loadClasses();
        loadCommands();
        loadEventHandlers();

        this.log.info("Startup completed.");
    }

    @Override
    public void onDisable() {}

    /**
     * Initialise the file system.
     */
    private void initFileSystem() {
        saveDefaultConfig();

        saveResource("config.yml", false);
        saveResource("data/warps.yml", false);

        File statsFolder = new File(getDataFolder() + File.separator + "data" + File.separator + "players");
        if (!statsFolder.exists()) {
            statsFolder.mkdirs();
        }
    }

    /**
     * Loads any classes that can't be loaded by initializers.
     */
    private void loadClasses() {
        this.log.info("LoadClasses: Loading...");
        //EntityPickupItemClass = new EntityPickupItem(this);
        this.log.info("LoadClasses: Done");
    }

    /**
     * Loads and registers the plugin's command handlers.
     */
    private void loadCommands() {
        this.log.info("LoadCommands: Loading...");
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
            this.getCommand("sethome").setExecutor(new SethomeCommand(this));
            this.getCommand("setwarp").setExecutor(new SetwarpCommand(this));
            this.getCommand("tp").setExecutor(new TeleportCommand(this));
            this.getCommand("warp").setExecutor(new WarpCommand(this));
        } catch (NullPointerException e) {
            this.log.severe("LoadCommands: Unable to load Essence commands.");
        }
        this.log.info("LoadCommands: Done.");
    }

    /**
     * Loads and registers all the plugin's event handlers.
     */
    private void loadEventHandlers() {
        this.log.info("LoadEventHandlers: Loading event handlers...");
        Bukkit.getLogger().info("[Essence] Loading event handlers...");
        Bukkit.getServer().getPluginManager().registerEvents(new JoinEvent(this), this);
        this.log.info("LoadEventHandlers: Done.");
    }
}
