package net.lewmc.essence.module.teleportation.home;

import net.lewmc.essence.Essence;
import net.lewmc.essence.global.UtilCommand;
import net.lewmc.essence.global.UtilMessage;
import net.lewmc.essence.global.UtilPermission;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandDelhome implements CommandExecutor {
    private final Logger log;
    private final Essence plugin;

    /**
     * Constructor for the DelhomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandDelhome(Essence plugin) {
        this.plugin = plugin;
        this.log = new Logger(plugin.config);
    }

    /**
     * @param cs Information about who sent the command - player or console.
     * @param command       Information about what command was sent.
     * @param s             Command label - not used here.
     * @param args          The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    public boolean onCommand(
        @NotNull CommandSender cs,
        @NotNull Command command,
        @NotNull String s,
        String[] args
    ) {
        if (command.getName().equalsIgnoreCase("delhome")) {
            UtilCommand cmd = new UtilCommand(this.plugin, cs);
            if (cmd.isDisabled("delhome")) { return cmd.disabled(); }

            if (!(cs instanceof Player p)) { return this.log.noConsole(); }

            UtilPermission perms = new UtilPermission(this.plugin, cs);

            if (perms.has("essence.home.delete")) {
                String name;
                if (args.length == 0) {
                    name = "home";
                } else {
                    name = args[0];
                }

                Files config = new Files(this.plugin.config, this.plugin);
                config.load(config.playerDataFile(p));

                String homeName = name.toLowerCase();

                UtilMessage msg = new UtilMessage(this.plugin, cs);
                if (config.get("homes."+homeName) == null) {
                    config.close();
                    msg.send("home", "notfound", new String[] { name });
                    return true;
                }

                if (config.remove("homes."+homeName)) {
                    msg.send("home", "deleted", new String[] { homeName });
                } else {
                    msg.send("generic", "exception");
                }

                config.save();
            } else {
                return perms.not();
            }
            return true;
        }

        return false;
    }
}
