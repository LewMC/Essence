package net.lewmc.essence;

import net.lewmc.essence.economy.UtilVaultEconomy;
import net.lewmc.foundry.Logger;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

/**
 * Handles Essence's integrations.
 */
public class EssenceIntegrations {
    /**
     * Is PlaceholderAPI enabled?
     */
    public boolean PAPIEnabled;

    /**
     * The Vault chat handler.
     */
    public Chat chat;

    /**
     * The Vault economy handler.
     */
    public Economy economy;

    private final Logger log;
    private final Essence plugin;

    /**
     * Constructor.
     * @param plugin Reference to the main Essence class.
     */
    public EssenceIntegrations(Essence plugin) {
        this.plugin = plugin;
        this.log = new Logger(plugin.config);
    }

    /**
     * Registers Essence's PlaceholderAPIExpansion
     * @return boolean - If it could be setup correctly.
     */
    public boolean loadPlaceholderAPI() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new EssencePAPIExpansion(this.plugin).register();
            this.PAPIEnabled = true;
            this.log.info("Placeholder API is installed, registered placeholders.");
        } else {
            this.PAPIEnabled = false;
            if (this.plugin.verbose) {
                this.log.info("Placeholder API is not installed, placeholders not registered.");
            }
        }

        return this.PAPIEnabled;
    }

    /**
     * Sets up Vault to use Essence's economy.
     * @return boolean - If it could be setup correctly.
     */
    public boolean loadVaultEconomy() {
        if (this.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        this.log.info("Vault found, setting up economy service...");

        this.plugin.getServer().getServicesManager().register(Economy.class, new UtilVaultEconomy(this.plugin), this.plugin, ServicePriority.Highest);

        RegisteredServiceProvider<Economy> rsp = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            log.severe("No economy service provider found after registration!");
            return false;
        }

        this.economy = rsp.getProvider();

        this.log.info("");

        this.plugin.economySymbol = this.plugin.getConfig().getString("economy.symbol");

        return this.economy != null;
    }

    /**
     * Sets up Essence to use Vault's chat interfaces.
     * @return boolean - If it could be setup correctly.
     */
    public boolean loadVaultChat() {
        if (this.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        this.log.info("Vault found, setting up chat service...");

        RegisteredServiceProvider<Chat> rsp = this.plugin.getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp != null) {
            this.chat = rsp.getProvider();
        }

        this.log.info("");

        return this.chat != null;
    }

    /**
     * Sets up Essence to use bStats Metrics.
     */
    public void loadMetrics() {
        int pluginId = 20768;
        Metrics metrics = new Metrics(this.plugin, pluginId);
        metrics.addCustomChart(new SimplePie("language", () -> this.plugin.getConfig().getString("language")));
        if (this.economy == null) {
            metrics.addCustomChart(new SimplePie("economy_enabled", () -> "false"));
        } else {
            metrics.addCustomChart(new SimplePie("economy_enabled", () -> "true"));
        }
        if (this.chat == null) {
            metrics.addCustomChart(new SimplePie("chat_enabled", () -> "false"));
        } else {
            metrics.addCustomChart(new SimplePie("chat_enabled", () -> "true"));
        }
        if (!this.PAPIEnabled) {
            metrics.addCustomChart(new SimplePie("papi_enabled", () -> "false"));
        } else {
            metrics.addCustomChart(new SimplePie("papi_enabled", () -> "true"));
        }
    }
}
