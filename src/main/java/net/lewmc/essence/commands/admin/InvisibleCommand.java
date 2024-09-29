package net.lewmc.essence.commands.admin;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.CommandUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.PermissionHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * /invisible command.
 */
public class InvisibleCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the InvisibleCommand class.
     * @param plugin References to the main plugin class.
     */
    public InvisibleCommand(Essence plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            String[] args
    ) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) commandSender;
        MessageUtil message = new MessageUtil(commandSender, plugin);
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("invisible")) {
            CommandUtil cmd = new CommandUtil(this.plugin);
            if (cmd.isDisabled("invisible")) {
                return cmd.disabled(message);
            }

            if (permission.has("essence.admin.invisible")) {
                // Apply invisibility
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                message.send("visibility", "invisible", new String[]{player.getName()});
                return true;
            } else {
                return permission.not();
            }
        }

        return false;
    }
}
