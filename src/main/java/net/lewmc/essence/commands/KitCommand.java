package net.lewmc.essence.commands;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * /kit command.
 */
public class KitCommand implements CommandExecutor {

    private final Essence plugin;

    /**
     * Constructor for the KitCommand class.
     * @param plugin References to the main plugin class.
     */
    public KitCommand (Essence plugin) {
        this.plugin = plugin;
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
        LogUtil log = new LogUtil(this.plugin);

        CommandUtil cmd = new CommandUtil(this.plugin);
        if (cmd.console(commandSender)) {
            log.noConsole();
            return true;
        }

        MessageUtil message = new MessageUtil(commandSender, plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("kit")) {
            if (cmd.isDisabled("kit")) {
                return cmd.disabled();
            }

            if (args.length == 0) {
                StringBuilder kits = new StringBuilder("No kits found.");

                FileUtil kitData = new FileUtil(this.plugin);
                kitData.load("data/kits.yml");
                Set<String> keys = kitData.getKeys("kits", false);

                int i = 0;
                for (Object object : keys) {
                    if (kitData.get("kits." + object + ".permission") == null) {
                        if (i == 0) {
                            kits = new StringBuilder(object.toString());
                        } else {
                            kits.append(", ").append(object.toString());
                        }
                    } else {
                        if (permission.has(kitData.get("kits." + object + ".permission").toString()) || permission.has("essence.kits.all")) {
                            if (i == 0) {
                                kits = new StringBuilder(object.toString());
                            } else {
                                kits.append(", ").append(object.toString());
                            }
                        }
                    }
                    i++;
                }

                kitData.close();

                message.send("kit", "select", new String[] { kits.toString() });
            } else {
                KitUtil kit = new KitUtil(this.plugin, player);

                if (kit.giveKit(args[0]) == 0) {
                    message.send("kit", "done", new String[] { args[0] });
                } else if (kit.giveKit(args[0]) == 1) {
                    message.send("kit", "nopermission");
                } else if (kit.giveKit(args[0]) == 2) {
                    message.send("kit", "notexist");
                }
            }
            return true;
        }
        return false;
    }
}
