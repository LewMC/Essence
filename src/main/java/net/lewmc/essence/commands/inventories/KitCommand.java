package net.lewmc.essence.commands.inventories;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class KitCommand implements CommandExecutor {

    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the KitCommand class.
     * @param plugin References to the main plugin class.
     */
    public KitCommand (Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
    }

    /**
     * @param commandSender Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] args)
    {

        if (!(commandSender instanceof Player)) {
            this.log.noConsole();
            return true;
        }
        MessageUtil message = new MessageUtil(commandSender, plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("kit")) {
            if (permission.has("essence.inventory.loom")) {
                if (args.length == 0) {
                    String kits = "No kits found.";

                    DataUtil config = new DataUtil(this.plugin, message);
                    config.load("data/kits.yml");
                    Set<String> keys = config.getKeys("kits");

                    int i = 0;
                    for (Object object : keys) {
                        ConfigurationSection cs = config.getSection("kits."+object);
                        if (cs.get("permission") == null) {
                            if (i == 0) {
                                kits = object.toString();
                            } else {
                                kits += ", " + object.toString();
                            }
                        } else {
                            if (permission.has(cs.get("permission").toString()) || permission.has("essence.kits.all")) {
                                if (i == 0) {
                                    kits = object.toString();
                                } else {
                                    kits += ", " + object.toString();
                                }
                            }
                        }
                        i++;
                    }

                    config.close();

                    message.PrivateMessage("kit", "select", kits);
                } else {
                    KitUtil kit = new KitUtil(this.plugin, player);

                    if (kit.giveKit(args[0]) == 0) {
                        message.PrivateMessage("kit", "done", args[0]);
                    } else if (kit.giveKit(args[0]) == 1) {
                        message.PrivateMessage("kit", "nopermission");
                    } else if (kit.giveKit(args[0]) == 2) {
                        message.PrivateMessage("kit", "notexist");
                    }
                }
            } else {
                permission.not();
            }

            return true;
        }
        return false;
    }
}