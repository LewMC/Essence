package net.lewmc.essence;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.lewmc.essence.utils.placeholders.PlaceholderUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Essence's PlaceholderAPI Expansion class.
 */
public class EssencePAPIExpansion extends PlaceholderExpansion {
    private final Essence plugin;

    /**
     * Sets up the class.
     * @param plugin Reference to the main Essence class.
     */
    public EssencePAPIExpansion(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Retrieves the author.
     * @return String - Author's name
     */
    @Override
    @NotNull
    public String getAuthor() {
        return "LewMC";
    }

    /**
     * Retrieves the identifier.
     * @return String - The identifier
     */
    @Override
    @NotNull
    public String getIdentifier() {
        return "essence";
    }

    /**
     * Retrieves the version.
     * @return String - The version
     */
    @Override
    @NotNull
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    /**
     * Handles the PAPI request.
     * @param player OfflinePlayer - The player making the request.
     * @param param String - The param.
     * @return String - The placeholder response.
     */
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String param) {
        PlaceholderUtil pu = new PlaceholderUtil(this.plugin, (Player) player);

        if (param.equalsIgnoreCase("version")) { return pu.replaceSingle("version"); }
        if (param.equalsIgnoreCase("minecraft_version")) { return pu.replaceSingle("minecraft_version"); }
        if (param.equalsIgnoreCase("time")) { return pu.replaceSingle("time"); }
        if (param.equalsIgnoreCase("date")) { return pu.replaceSingle("date"); }
        if (param.equalsIgnoreCase("datetime")) { return pu.replaceSingle("datetime"); }
        if (param.equalsIgnoreCase("player")) { return pu.replaceSingle("player"); }
        if (param.equalsIgnoreCase("team")) { return pu.replaceSingle("team_name"); }
        if (param.equalsIgnoreCase("team_name")) { return pu.replaceSingle("team_name"); }
        if (param.equalsIgnoreCase("team_leader")) { return pu.replaceSingle("team_leader"); }

        return null; //
    }
}