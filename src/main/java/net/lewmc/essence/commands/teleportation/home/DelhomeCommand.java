package net.lewmc.essence.commands.teleportation.home;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DelhomeCommand implements CommandExecutor {
    private final LogUtil log;
    private final Essence plugin;

    /**
     * Constructor for the DelhomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public DelhomeCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new LogUtil(plugin);
    }

    /**
     * @param commandSender Information about who sent the command - player or console.
     * @param command       Information about what command was sent.
     * @param s             Command label - not used here.
     * @param args          The command's arguments.
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
        MessageUtil message = new MessageUtil(commandSender,plugin);
        Player player = (Player) commandSender;
        PermissionHandler permission = new PermissionHandler(commandSender, message);

        if (command.getName().equalsIgnoreCase("delhome")) {
            if (permission.has("essence.home.delete")) {
                String name;
                if (args.length == 0) {
                    name = "home";
                } else {
                    name = args[0];
                }

                FileUtil config = new FileUtil(this.plugin);
                config.load(config.playerDataFile(player));

                String homeName = name.toLowerCase();

                if (config.get("homes."+homeName) == null) {
                    config.close();
                    message.PrivateMessage("home", "notfound", name);
                    return true;
                }

                if (config.remove("homes."+homeName)) {
                    message.PrivateMessage("home", "deleted", homeName);
                } else {
                    message.PrivateMessage("generic", "exception");
                }

                config.save();
            } else {
                permission.not();
            }
            return true;
        }

        return false;
    }
}
