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
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("delhome")) { return cmd.disabled(); }

            if (!(cs instanceof Player p)) { return this.log.noConsole(); }

            PermissionHandler perms = new PermissionHandler(this.plugin, cs);

            if (perms.has("essence.home.delete")) {
                String name;
                if (args.length == 0) {
                    name = "home";
                } else {
                    name = args[0];
                }

                FileUtil config = new FileUtil(this.plugin);
                config.load(config.playerDataFile(p));

                String homeName = name.toLowerCase();

                MessageUtil msg = new MessageUtil(this.plugin, cs);
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
