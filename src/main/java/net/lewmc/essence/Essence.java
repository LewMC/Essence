package net.lewmc.essence;

import net.lewmc.essence.commands.inventories.*;
import net.lewmc.essence.commands.EssenceCommands;
import net.lewmc.essence.commands.GamemodeCommands;
import net.lewmc.essence.commands.stats.HealCommand;
import net.lewmc.essence.commands.stats.FeedCommand;
import net.lewmc.essence.commands.teleportation.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Essence extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("[Essence] Beginning startup sequence.");

        saveDefaultConfig();

        saveResource("config.yml", false);
        saveResource("warps.yml", false);
        saveResource("homes.yml", false);

        loadClasses();
        loadCommands();
        loadEventHandlers();

        Bukkit.getLogger().info("[Essence] Startup completed.");
    }

    @Override
    public void onDisable() {}

    /**
     * Loads any classes that can't be loaded by initializers.
     */
    private void loadClasses() {
        Bukkit.getLogger().info("[Essence] Loading classes...");
        //EntityPickupItemClass = new EntityPickupItem(this);
        Bukkit.getLogger().info("[Essence] Classes loaded...");
    }

    /**
     * Loads and registers the plugin's command handlers.
     */
    private void loadCommands() {
        Bukkit.getLogger().info("[Essence] Loading commands...");
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
            Bukkit.getLogger().severe("[Essence] ERROR: Couldn't enable essence commands.");
        }
        Bukkit.getLogger().info("[Essence] Commands loaded.");
    }

    /**
     * Loads and registers all the plugin's event handlers.
     */
    private void loadEventHandlers() {
        Bukkit.getLogger().info("[Essence] Loading event handlers...");
        //Bukkit.getServer().getPluginManager().registerEvents(EntityPickupItemClass, this);
        Bukkit.getLogger().info("[Essence] Event handlers loaded.");
    }
}
