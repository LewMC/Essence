package net.lewmc.essence.commands.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.CommandUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.PermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HealCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the HealCommand class.
     * @param plugin References to the main plugin class.
     */
    public HealCommand(Essence plugin) {
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
        String[] args
    ) {
        MessageUtil message = new MessageUtil(commandSender, plugin);
        if (!(commandSender instanceof Player) && args.length == 0) {
            message.send("heal","usage");
            return true;
        }
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("heal")) {
            CommandUtil cmd = new CommandUtil(this.plugin);
            if (cmd.isDisabled("heal")) {
                return cmd.disabled();
            }

            if (args.length > 0) {
                return this.healOther(permission, commandSender, message, args);
            } else {
                return this.healSelf(permission, commandSender, message);
            }
        }

        return true;
    }

    /**
     * Heals the command sender.
     * @param permission PermisionHandler - The permission system.
     * @param sender CommandSender - The user to feed.
     * @param message MessageUtil - The messaging system.
     * @return boolean - If the operation was successful
     */
    private boolean healSelf(PermissionHandler permission, CommandSender sender, MessageUtil message) {
        if (permission.has("essence.stats.heal")) {
            Player player = (Player) sender;
            player.setHealth(20.0);
            message.send("heal", "beenhealed");
            return true;
        } else {
            return permission.not();
        }
    }

    /**
     * Heals another user.
     * @param permission PermisionHandler - The permission system.
     * @param sender CommandSender - The user to feed.
     * @param message MessageUtil - The messaging system.
     * @param args String[] - List of command arguments.
     * @return boolean - If the operation was successful
     */
    private boolean healOther(PermissionHandler permission, CommandSender sender, MessageUtil message, String[] args) {
        if (permission.has("essence.stats.heal.other")) {
            String pName = args[0];
            Player p = Bukkit.getPlayer(pName);
            if (p != null) {
                message.send("heal", "healed", new String[] { p.getName() });
                if (!(sender instanceof Player)) {
                    message.sendTo(p, "heal", "serverhealed");
                } else {
                    message.sendTo(p, "heal", "healedby", new String[] { sender.getName() });
                }
                p.setHealth(20.0);
            } else {
                message.send("generic", "playernotfound");
            }
            return true;
        } else {
            return permission.not();
        }
    }
}