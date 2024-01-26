package net.lewmc.essence.commands.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class HomesCommand implements CommandExecutor {
    private final Essence plugin;
    private final LogUtil log;

    /**
     * Constructor for the HomesCommand class.
     * @param plugin References to the main plugin class.
     */
    public HomesCommand(Essence plugin) {
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
        String[] args
    ) {
        if (!(commandSender instanceof Player)) {
            this.log.noConsole();
            return true;
        }
        MessageUtil message = new MessageUtil(commandSender, plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("homes")) {
            if (permission.has("essence.home.list")) {
                DataUtil dataUtil = new DataUtil(this.plugin, message);
                dataUtil.load(dataUtil.playerDataFile(player));

                Set<String> keys = dataUtil.getKeys("homes");

                if (keys == null) {
                    dataUtil.close();
                    message.PrivateMessage("home", "noneset");
                    return true;
                }

                StringBuilder setHomes = new StringBuilder();
                int i = 0;

                for (String key : keys) {
                    if (i == 0) {
                        setHomes.append(key);
                    } else {
                        setHomes.append(", ").append(key);
                    }
                    i++;
                }
                dataUtil.close();
                message.PrivateMessage("home", "list", setHomes.toString());
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}