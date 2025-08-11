package net.lewmc.essence.teleportation.home;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.Security;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSethome extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the SethomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandSethome(Essence plugin) {
        this.plugin = plugin;
    }

    @Override
    protected String requiredPermission() {
        return "essence.home.create";
    }

    /**
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean  true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        Player p = (Player) cs;

        String name = "home";
        if (args.length != 0) {
            name = args[0];
        }

        Files playerData = new Files(this.plugin.config, this.plugin);
        playerData.load(playerData.playerDataFile(p));

        UtilMessage msg = new UtilMessage(this.plugin, cs);

        if (new Security(this.plugin.config).hasSpecialCharacters(name.toLowerCase())) {
            playerData.close();
            msg.send("home", "specialchars");
            return true;
        }

        UtilHome hu = new UtilHome(this.plugin);
        int homeLimit = new UtilPermission(this.plugin, cs).getHomesLimit(p);
        if (hu.getHomeCount(p) > homeLimit && homeLimit != -1) {
            msg.send("home", "hitlimit");
            return true;
        }

        if (hu.create(name.toLowerCase(), p, p.getLocation())) {
            msg.send("home", "created", new String[]{name});
        } else {
            msg.send("home", "cantcreate", new String[]{name});
        }

        return true;
    }
}
