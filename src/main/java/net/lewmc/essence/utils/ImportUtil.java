package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;
import org.bukkit.Location;

import java.io.File;
import java.util.Set;
import java.util.UUID;

/**
 * Imports data from other systems.
 */
public class ImportUtil {
    private final Essence plugin;

    /**
     * Constructor for the ImportUtil class.
     * @param plugin Essence - Reference to the main class.
     */
    public ImportUtil(Essence plugin) {
        this.plugin = plugin;
    }

    public boolean EssentialsWarps() {
        File dir = new File(this.plugin.getServer().getPluginsFolder()+"/Essentials/warps");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                FileUtil file = new FileUtil(this.plugin);
                file.loadNoReformat(child);
                WarpUtil warp = new WarpUtil(this.plugin);

                Location loc = new Location(
                        plugin.getServer().getWorld(file.getString("world-name")),
                        file.getDouble("x"),
                        file.getDouble("y"),
                        file.getDouble("z"),
                        (float) file.getDouble("yaw"),
                        (float) file.getDouble("pitch")
                );

                warp.create(
                        file.getString("name"),
                        UUID.fromString(file.getString("lastowner")),
                        loc
                );

                file.close();
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean EssentialsHomes() {
        File dir = new File(this.plugin.getServer().getPluginsFolder()+"/Essentials/userdata");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                FileUtil file = new FileUtil(this.plugin);
                file.loadNoReformat(child);
                Set<String> homes = file.getKeys("homes", false);

                for (String home : homes) {
                    HomeUtil homeUtil = new HomeUtil(this.plugin);

                    Location loc = new Location(
                            this.plugin.getServer().getWorld(file.getString("homes."+home+".world-name")),
                            file.getDouble("homes."+home+".x"),
                            file.getDouble("homes."+home+".y"),
                            file.getDouble("homes."+home+".z"),
                            (float) file.getDouble("homes."+home+".yaw"),
                            (float) file.getDouble("homes."+home+".pitch")
                    );

                    homeUtil.create(
                            home,
                            this.plugin.getServer().getPlayer(file.getString("last-account-name")),
                            loc
                    );
                }
                file.close();
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean EssentialsSpawns() {
        return false;
    }

    public boolean HuskWarps() {
        return false;
    }

    public boolean HuskHomes() {
        return false;
    }

    public boolean HuskSpawns() {
        return false;
    }
}
