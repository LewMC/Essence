package net.lewmc.essence.commands.teleportation.home;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Logger;
import net.lewmc.foundry.Security;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SethomeCommand implements CommandExecutor {
    private final Essence plugin;
    private final Logger log;

    /**
     * Constructor for the SethomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public SethomeCommand(Essence plugin) {
        this.plugin = plugin;
        this.log = new Logger(plugin.config);
    }

    /**
     * @param cs            Information about who sent the command - player or console.
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
        if (command.getName().equalsIgnoreCase("sethome")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("sethome")) { return cmd.disabled(); }

            if (!(cs instanceof Player p)) { return this.log.noConsole(); }

            PermissionHandler perms = new PermissionHandler(this.plugin, cs);

            if (perms.has("essence.home.create")) {

                String name = "home";
                if (args.length != 0) {
                    name = args[0];
                }

                Location loc = p.getLocation();
                Files playerData = new Files(this.plugin.config, this.plugin);
                playerData.load(playerData.playerDataFile(p));

                Security sec = new Security(this.plugin.config);
                MessageUtil msg = new MessageUtil(this.plugin, cs);

                if (sec.hasSpecialCharacters(name.toLowerCase())) {
                    playerData.close();
                    msg.send("home", "specialchars");
                    return true;
                }

                HomeUtil hu = new HomeUtil(this.plugin);
                int homeLimit = perms.getHomesLimit(p);
                if (hu.getHomeCount(p) >= homeLimit && homeLimit != -1) {
                    msg.send("home", "hitlimit");
                    return true;
                }

                if (hu.create(name.toLowerCase(), p, loc)) {
                    msg.send("home", "created", new String[] { name });
                } else {
                    msg.send("home", "cantcreate", new String[] { name });
                }
            } else {
                return perms.not();
            }
            return true;
        }

        return false;
    }
}