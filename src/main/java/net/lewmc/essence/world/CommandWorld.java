package net.lewmc.essence.world;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * /world command.
 */
public class CommandWorld extends FoundryPlayerCommand {
    private final Essence plugin;
    private UtilMessage msg;
    private UtilWorld world;

    /**
     * Constructor for the CommandWorld class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandWorld(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     *
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.world";
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
        this.msg = new UtilMessage(this.plugin, cs);
        UtilPermission perms = new UtilPermission(this.plugin, cs);

        if (args.length == 0) {
            this.msg.send("world", "usage");
        } else {
            this.world = new UtilWorld(this.plugin);
            if (args[0].equalsIgnoreCase("teleport")) {
                if (!perms.has("essence.world.teleport")) {
                    msg.send("generic", "missingpermission");
                    return true;
                }
                // todo: implement
            } else if (args[0].equalsIgnoreCase("create")) {
                if (!perms.has("essence.world.create")) {
                    msg.send("generic", "missingpermission");
                    return true;
                }
                this.create(args);
            } else if (args[1].equalsIgnoreCase("delete")) {
                if (!perms.has("essence.world.delete")) {
                    msg.send("generic", "missingpermission");
                    return true;
                }
                // todo: implement
            }
        }

        return true;
    }

    private void create(String[] args) {
        if (args.length == 2) {
            if (this.world.createWorld(args[1])) {
                this.msg.send("world", "created", new String[]{args[1]});
            } else {
                this.msg.send("world", "notcreated", new String[]{args[1]});
            }
        } else {
            this.msg.send("world", "noname");
        }
    }
}