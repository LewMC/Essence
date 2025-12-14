package net.lewmc.essence.world;

import net.lewmc.essence.Essence;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import net.lewmc.foundry.Parser;
import net.lewmc.foundry.Security;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * The UtilWorld class, used for world management.
 */
public class UtilWorld {
    private final Essence plugin;

    /**
     * Constructor for the class.
     * @param plugin Essence - Reference to the main plugin class.
     */
    public UtilWorld(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates a world
     * @param name String - the world name
     * @param flags Map(String, String) - the flags
     * @return WORLD_STATUS - the status
     */
    public WORLD_STATUS create(String name, Map<String, String> flags) {
        try {
            if (this.plugin.getServer().getWorld(name) != null) {
                return WORLD_STATUS.EXISTS;
            }

            if (new Files(this.plugin.foundryConfig, this.plugin).exists(this.plugin.getDataFolder().getPath()+"/"+name)) {
                return WORLD_STATUS.EXISTS;
            }

            WorldCreator wc = new WorldCreator(name);

            if (new Security(this.plugin.foundryConfig).hasSpecialCharacters(name)) {
                return WORLD_STATUS.INVALID_CHARS;
            }

            if (flags.get("-e") != null) {
                if (flags.get("-e").equalsIgnoreCase("normal")) {
                    wc.environment(World.Environment.NORMAL);
                } else if (flags.get("-e").equalsIgnoreCase("nether")) {
                    wc.environment(World.Environment.NETHER);
                } else if (flags.get("-e").equalsIgnoreCase("end")) {
                    wc.environment(World.Environment.THE_END);
                } else if (flags.get("-e").equalsIgnoreCase("custom")) {
                    wc.environment(World.Environment.CUSTOM);
                } else {
                    return WORLD_STATUS.INVALID_E;
                }
            } else {
                wc.environment(World.Environment.NORMAL);
            }

            if (flags.get("-n") != null) {
                if (flags.get("-n").equalsIgnoreCase("true") || flags.get("-n").equalsIgnoreCase("yes") || flags.get("-n").equalsIgnoreCase("y")) {
                    wc.generateStructures(true);
                } else if (flags.get("-n").equalsIgnoreCase("false") || flags.get("-n").equalsIgnoreCase("no") || flags.get("-n").equalsIgnoreCase("n")) {
                    wc.generateStructures(false);
                } else {
                    return WORLD_STATUS.INVALID_N;
                }
            }

            if (flags.get("-h") != null) {
                if (flags.get("-h").equalsIgnoreCase("true") || flags.get("-h").equalsIgnoreCase("yes") || flags.get("-h").equalsIgnoreCase("y")) {
                    wc.hardcore(true);
                } else if (flags.get("-h").equalsIgnoreCase("false") || flags.get("-h").equalsIgnoreCase("no") || flags.get("-h").equalsIgnoreCase("n")) {
                    wc.hardcore(false);
                } else {
                    return WORLD_STATUS.INVALID_H;
                }
            }

            if (flags.get("-b") != null) {
                wc.biomeProvider(flags.get("-b"));
            }

            if (flags.get("-g") != null) {
                wc.generator(flags.get("-g"));
            }

            if (flags.get("-s") != null) {
                if (new Parser().isNumeric(flags.get("-s"))) {
                    try {
                        wc.seed(Long.parseLong(flags.get("-s")));
                    } catch (NumberFormatException e) {
                        this.plugin.log.warn("Invalid seed specified for world " + name);
                        this.plugin.log.warn(e.getMessage());
                        return WORLD_STATUS.INVALID_S;
                    }
                }
            }

            if (flags.get("-t") != null) {
                if (flags.get("-t").equalsIgnoreCase("normal")) {
                    wc.type(WorldType.NORMAL);
                } else if (flags.get("-t").equalsIgnoreCase("flat")) {
                    wc.type(WorldType.FLAT);
                } else if (flags.get("-t").equalsIgnoreCase("amplified")) {
                    wc.type(WorldType.AMPLIFIED);
                } else if (flags.get("-t").equalsIgnoreCase("large_biomes")) {
                    wc.type(WorldType.LARGE_BIOMES);
                } else {
                    return WORLD_STATUS.INVALID_T;
                }
            } else {
                wc.type(WorldType.NORMAL);
            }

            if (flags.get("-gs") != null) {
                wc.generatorSettings(flags.get("-gs"));
            }

            if (
                !flags.get("-a").equalsIgnoreCase("true") &&
                !flags.get("-a").equalsIgnoreCase("yes") &&
                !flags.get("-a").equalsIgnoreCase("y") &&
                !flags.get("-a").equalsIgnoreCase("false") &&
                !flags.get("-a").equalsIgnoreCase("no") &&
                !flags.get("-a").equalsIgnoreCase("n"))
            {
                return WORLD_STATUS.INVALID_A;
            }

            World newWorld = wc.createWorld();

            if (newWorld != null) {
                Files worldData = new Files(this.plugin.foundryConfig, this.plugin);
                worldData.load("data/worlds.yml");
                if (worldData.get("world."+newWorld.getUID()) == null) {
                    worldData.set("world."+newWorld.getUID()+".name", name);
                    worldData.set("world."+newWorld.getUID()+".can-enter", true);

                    if (flags.get("-a") != null) {
                        if (flags.get("-a").equalsIgnoreCase("true") || flags.get("-a").equalsIgnoreCase("yes") || flags.get("-a").equalsIgnoreCase("y")) {
                            worldData.set("world."+newWorld.getUID()+".autoload", true);
                        } else if (flags.get("-a").equalsIgnoreCase("false") || flags.get("-a").equalsIgnoreCase("no") || flags.get("-a").equalsIgnoreCase("n")) {
                            worldData.set("world."+newWorld.getUID()+".autoload", false);
                        }
                    }

                    worldData.save();
                }

                return this.load(name);
            } else {
                new Logger(this.plugin.foundryConfig).warn("Unable to create world: tried to create world but it was null when checked, something went wrong.");
                return WORLD_STATUS.OTHER_ERROR;
            }
        } catch (Exception e) {
            new Logger(this.plugin.foundryConfig).warn("Unable to create world: " + e.getMessage());
            return WORLD_STATUS.OTHER_ERROR;
        }
    }

    /**
     * Deletes a world
     * @param name String - the world name
     * @return WORLD_STATUS - the status
     */
    public WORLD_STATUS delete(String name) {
        List<ESSENCE_WORLD> worlds = this.list();

        for (ESSENCE_WORLD w : worlds) {
            if (Objects.equals(name, w.name)) {
                if (w.status == WORLD_STATUS.LOADED) {
                    return WORLD_STATUS.LOADED;
                } else {
                    String path = new File(Bukkit.getWorldContainer(), w.name).getPath();
                    if (!new Files(this.plugin.foundryConfig, this.plugin).exists(path)) return WORLD_STATUS.NOT_FOUND;

                    try {
                        new Files(this.plugin.foundryConfig, this.plugin).deleteDirectory(Path.of(path));
                    } catch (IOException e) {
                        return WORLD_STATUS.OTHER_ERROR;
                    }

                    Files worldData = new Files(this.plugin.foundryConfig, this.plugin);
                    worldData.load("data/worlds.yml");
                    if (worldData.get("world."+w.uuid) != null) {
                        worldData.remove("world."+w.uuid);
                        worldData.save();
                    } else {
                        worldData.close();
                    }

                    return WORLD_STATUS.UNLOADED;
                }
            }
        }

        return WORLD_STATUS.NOT_FOUND;
    }

    /**
     * Unloads a world
     * @param name String - the world name
     * @return WORLD_STATUS - the status
     */
    public WORLD_STATUS unload(String name) {
        World world = Bukkit.getWorld(name);

        if (world == null) {
            return WORLD_STATUS.NOT_FOUND;
        } else {
            if (Bukkit.unloadWorld(world, true)) {
                return WORLD_STATUS.UNLOADED;
            } else {
                return WORLD_STATUS.OTHER_ERROR;
            }
        }
    }

    /**
     * Loads a world
     * @param name String - the world name
     * @return WORLD_STATUS - the status
     */
    public WORLD_STATUS load(String name) {
        World world = Bukkit.getWorld(name);

        if (world == null) {
            if (new File(Bukkit.getWorldContainer(), name).exists()) {
                World loadedWorld = WorldCreator.name(name).createWorld();
                if (loadedWorld != null) {
                    this.saveToEssenceFile(loadedWorld, name, true, true);
                    return WORLD_STATUS.LOADED;
                } else {
                    return WORLD_STATUS.OTHER_ERROR;
                }
            } else {
                return WORLD_STATUS.NOT_FOUND;
            }
        } else {
            this.saveToEssenceFile(world, name, true, true);
            return WORLD_STATUS.LOADED;
        }
    }

    /**
     * Returns a list of worlds (loaded and unloaded).
     * @return List(ESSENCE_WORLD)
     */
    public List<ESSENCE_WORLD> list() {
        List<ESSENCE_WORLD> worlds = new ArrayList<>();

        // Loaded worlds
        Files worldData = new Files(this.plugin.foundryConfig, this.plugin);
        worldData.load("data/worlds.yml");
        Set<String> worldList = worldData.getKeys("world", false);

        for (String w : worldList) {
            ESSENCE_WORLD world = new ESSENCE_WORLD();

            world.uuid = UUID.fromString(w);
            world.name = worldData.getString("world."+w+".name");
            world.autoload = worldData.getBoolean("world."+w+".autoload");

            if (Bukkit.getWorld(UUID.fromString(w)) != null) {
                world.status = WORLD_STATUS.LOADED;
            } else {
                File[] folders = Bukkit.getWorldContainer().listFiles(File::isDirectory);
                if (Arrays.stream(folders).anyMatch(f -> f.getName().equals(world.name))) {
                    world.status = WORLD_STATUS.UNLOADED;
                } else {
                    world.status = WORLD_STATUS.DELETED;
                }
            }

            worlds.add(world);
        }

        worldData.close();

        return worlds;
    }


    /**
     * Various status codes for transmitting data about worlds.
     */
    public enum WORLD_STATUS {
        EXISTS,
        LOADED,
        UNLOADED,
        INVALID_E,
        INVALID_S,
        INVALID_T,
        INVALID_N,
        INVALID_H,
        INVALID_L,
        INVALID_A,
        OTHER_ERROR,
        INVALID_CHARS,
        NOT_FOUND,
        DELETED
    }

    /**
     * Essence's world data object.
     */
    public static class ESSENCE_WORLD {
        public UUID uuid;
        public String name;
        public WORLD_STATUS status;
        public Boolean autoload;

        public String getName() {
            return this.name;
        }
    }

    /**
     * Autoloads worlds on startup.
     */
    public void autoloadWorlds() {
        List<World> loadedWorlds = this.plugin.getServer().getWorlds();
        if (!loadedWorlds.isEmpty()) {
            this.load(loadedWorlds.getFirst().getName());
        }
        if (loadedWorlds.size() >= 2 && loadedWorlds.get(1).getName().contains("nether")) {
            this.load(loadedWorlds.get(1).getName());
        }
        if (loadedWorlds.size() >= 3 && loadedWorlds.get(2).getName().contains("the_end")) {
            this.load(loadedWorlds.get(2).getName());
        }

        List<ESSENCE_WORLD> list = this.list();
        if (list != null) {
            for (ESSENCE_WORLD w : list) {
                if (w.autoload && new File(Bukkit.getWorldContainer(), w.name).exists()) {
                    this.load(w.name);
                }
            }
        }
    }

    /**
     * Saves a world to Essence's data file.
     * @param world World - The world
     * @param name String - World name
     * @param canEnter boolean - Can players enter the world?
     * @param autoload boolean - Autoload on startup?
     */
    private void saveToEssenceFile(World world, String name, boolean canEnter, boolean autoload) {
        Files worldData = new Files(this.plugin.foundryConfig, this.plugin);
        worldData.load("data/worlds.yml");
        if (worldData.get("world."+world.getUID()) == null) {
            worldData.set("world."+world.getUID()+".name", name);
            worldData.set("world."+world.getUID()+".can-enter", canEnter);
            worldData.set("world."+world.getUID()+".autoload", autoload);
            worldData.save();
        } else {
            worldData.close();
        }
    }
}
