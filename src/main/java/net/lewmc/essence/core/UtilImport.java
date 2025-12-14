package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import net.lewmc.essence.teleportation.home.UtilHome;
import net.lewmc.essence.teleportation.warp.UtilWarp;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.util.Set;
import java.util.UUID;

/**
 * Imports data from other systems.
 */
public class UtilImport {
    private final Essence plugin;
    private final Logger log;

    /**
     * Constructor for the ImportUtil class.
     * @param plugin Essence - Reference to the main class.
     */
    public UtilImport(Essence plugin) {
        this.plugin = plugin;
        this.log = new Logger(this.plugin.foundryConfig);
    }

    /**
     * Imports warps from EssentialsX.
     * @return boolean - If the process was successful.
     */
    public boolean essentialsWarps() {
        File dir = new File(this.plugin.getServer().getPluginsFolder()+"/Essentials/warps");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                Files file = new Files(this.plugin.foundryConfig, this.plugin);
                file.loadNoReformat(child);
                UtilWarp warp = new UtilWarp(this.plugin);

                if (file.getString("x") != null) {

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
                }

                file.close();
            }
        } else {
            this.log.info("No Essentials warps found!");
            return false;
        }
        return true;
    }

    /**
     * Imports homes from EssentialsX.
     * @return boolean - If the process was successful.
     */
    public boolean essentialsHomes() {
        File dir = new File(this.plugin.getServer().getPluginsFolder()+"/Essentials/userdata");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                Files file = new Files(this.plugin.foundryConfig, this.plugin);
                if (this.plugin.verbose) { this.log.info("Import > Home > Begin processing from " + child.getName()); }
                file.loadNoReformat(child);
                Set<String> homes = file.getKeys("homes", false);

                if (homes != null) {
                    for (String home : homes) {
                        if (this.plugin.verbose) { this.log.info("Import > Home > Found home " + home); }
                        UtilHome utilHome = new UtilHome(this.plugin);

                        if (file.getString("homes." + home) != null) {

                            Location loc = new Location(
                                    this.plugin.getServer().getWorld(file.getString("homes." + home + ".world-name")),
                                    file.getDouble("homes." + home + ".x"),
                                    file.getDouble("homes." + home + ".y"),
                                    file.getDouble("homes." + home + ".z"),
                                    (float) file.getDouble("homes." + home + ".yaw"),
                                    (float) file.getDouble("homes." + home + ".pitch")
                            );

                            try {
                                UUID uuid = UUID.fromString(child.getName().replace(".yml", ""));

                                if (utilHome.create(
                                        home,
                                        this.plugin.getServer().getOfflinePlayer(uuid),
                                        loc
                                )) {
                                    if (this.plugin.verbose) {
                                        this.log.info("Import > Home > " + child.getName().replace(".yml", "") + " > " + home);
                                    }
                                } else {
                                    this.log.severe("Import > Home > Unable to import home " + home + " for user " + this.plugin.getServer().getOfflinePlayer(uuid) + " (" + child.getName().replace(".yml", "") + ")");
                                }
                            } catch (Exception e) {
                                this.log.severe("Import > Home > Unable to import from " + child.getName() + " due to null UUID.");
                                this.log.severe("Import > Home > Threw exception");
                                this.log.severe(e.getMessage());
                            }
                        }
                    }
                }
                if (this.plugin.verbose) { this.log.info("Import > Home > End processing from " + child.getName()); }
                file.close();
            }
        } else {
            this.log.info("No Essentials homes found!");
            return false;
        }
        return true;
    }

    /**
     * Imports spawns from EssentialsX.
     * @return boolean - If the process was successful.
     */
    public boolean essentialsSpawns() {
        boolean success = false;
        Files file = new Files(this.plugin.foundryConfig, this.plugin);

        if (file.exists(this.plugin.getServer().getPluginsFolder() + "/Essentials/spawn.yml")) {
            file.loadNoReformat(new File(this.plugin.getServer().getPluginsFolder() + "/Essentials/spawn.yml"));
            Set<String> spawns = file.getKeys("spawns", false);

            if (spawns != null) {
                for (String esxName : spawns) {
                    if (file.getString("spawns." + esxName) != null) {
                        Files spawnFile = new Files(this.plugin.foundryConfig, this.plugin);

                        spawnFile.load("data/worlds.yml");

                        World world = this.plugin.getServer().getWorld(file.getString("spawns." + esxName + ".world-name"));

                        if (world != null) {
                            UUID wUID = world.getUID();
                            if (spawnFile.get("worlds." + wUID) == null) {
                                spawnFile.set("worlds." + wUID + ".spawn.x", file.getDouble("spawns." + esxName + ".x"));
                                spawnFile.set("worlds." + wUID + ".spawn.y", file.getDouble("spawns." + esxName + ".y"));
                                spawnFile.set("worlds." + wUID + ".spawn.z", file.getDouble("spawns." + esxName + ".z"));
                                spawnFile.set("worlds." + wUID + ".spawn.yaw", file.getDouble("spawns." + esxName + ".yaw"));
                                spawnFile.set("worlds." + wUID + ".spawn.pitch", file.getDouble("spawns." + esxName + ".pitch"));
                            }
                        }

                        success = spawnFile.save();
                    }
                }
            } else {
                this.log.info("No Essentials spawns found!");
            }
            file.close();
        } else {
            this.log.info("Essentials spawns file does not exist!");
        }
        return success;
    }
}
