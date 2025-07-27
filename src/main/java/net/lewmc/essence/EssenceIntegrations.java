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
        if (this.plugin.verbose) {
            this.log.info("Config economy mode set to '" + this.plugin.economyMode + "'");
        }
        switch (this.plugin.economyMode) {
            case "VAULT" -> {
                if (this.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
                    return false;
                }

                this.plugin.getServer().getServicesManager().register(Economy.class, new UtilVaultEconomy(this.plugin), this.plugin, ServicePriority.Highest);

                RegisteredServiceProvider<Economy> ersp = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);
                if (ersp == null) {
                    this.log.severe("Something went wrong whilst setting up economy service.");
                    this.log.severe("Essence will fallback to internal-only economy mode.");
                    return false;
                }

                this.economy = ersp.getProvider();

                this.log.info("Setup economy in Vault mode.");

                this.plugin.economySymbol = this.plugin.getConfig().getString("economy.symbol");

                return this.economy != null;
            }
            case "ESSENCE", "INTERNAL" -> {
                this.log.warn("Setup economy in Essence-only mode.");
                this.log.warn("Vault economy is disabled, but Essence commands will still use internal economy.");
                return false;
            }
            case "OFF", "DISABLED", "DISABLE" -> {
                this.log.warn("Economy is disabled.");
                return false;
            }
            case null, default -> {
                this.log.warn("Unknown economy mode, economy is disabled. Please set mode to 'VAULT', 'ESSENCE' or 'OFF'");
                return false;
            }
        }
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

        RegisteredServiceProvider<Chat> crsp = this.plugin.getServer().getServicesManager().getRegistration(Chat.class);
        if (crsp != null) {
            this.chat = crsp.getProvider();
        } else {
            this.log.severe("Something went wrong whilst setting up chat service.");
            this.log.severe("Some chat features may be disabled.");
        }

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
