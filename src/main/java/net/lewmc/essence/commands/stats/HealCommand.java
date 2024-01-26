package net.lewmc.essence.commands.stats;

import net.lewmc.essence.Essence;
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
            message.PrivateMessage("heal","usage");
            return true;
        }
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("heal")) {
            if (args.length > 0) {
                if (permission.has("essence.stats.heal.other")) {
                    String pName = args[0];
                    Player p = Bukkit.getPlayer(pName);
                    if (p != null) {
                        message.PrivateMessage("heal", "healed", p.getName());
                        if (!(commandSender instanceof Player)) {
                            message.SendTo(p, "heal", "serverhealed");
                        } else {
                            message.SendTo(p, "heal", "healedby", commandSender.getName());
                        }
                        p.setHealth(20.0);
                    } else {
                        message.PrivateMessage("generic", "playernotfound");
                    }
                    return true;
                } else {
                    return permission.not();
                }
            } else {
                if (permission.has("essence.stats.heal")) {
                    Player player = (Player) commandSender;
                    player.setHealth(20.0);
                    message.PrivateMessage("heal", "beenhealed");
                    return true;
                } else {
                    return permission.not();
                }
            }
        }

        return true;
    }
}