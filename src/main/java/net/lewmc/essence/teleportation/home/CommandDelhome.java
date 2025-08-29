package net.lewmc.essence.teleportation.home;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /delhome command.
 */
public class CommandDelhome extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the DelhomeCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandDelhome(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.home.delete";
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
        String name;
        if (args.length == 0) {
            name = "home";
        } else {
            name = args[0];
        }

        Files config = new Files(this.plugin.foundryConfig, this.plugin);
        config.load(config.playerDataFile((Player) cs));

        String homeName = name.toLowerCase();

        UtilMessage msg = new UtilMessage(this.plugin, cs);
        if (config.get("homes." + homeName) == null) {
            config.close();
            msg.send("home", "notfound", new String[]{name});
            return true;
        }

        if (config.remove("homes." + homeName)) {
            msg.send("home", "deleted", new String[]{homeName});
        } else {
            msg.send("generic", "exception");
        }

        config.save();

        return true;
    }
}