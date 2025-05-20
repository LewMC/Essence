package net.lewmc.essence.kit;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * /kit command.
 */
public class CommandKit extends FoundryPlayerCommand {

    private final Essence plugin;

    /**
     * Constructor for the KitCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandKit(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return null;
    }

    /**
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("kit")) { return cmd.disabled(); }

        Player p = (Player) cs;

        if (args.length == 0) {
            StringBuilder kits = new StringBuilder("No kits found.");

            Files kitData = new Files(this.plugin.config, this.plugin);
            kitData.load("data/kits.yml");
            Set<String> keys = kitData.getKeys("kits", false);

            int i = 0;
            for (Object object : keys) {
                if (kitData.get("kits." + object + ".permission") == null) {
                    if (i == 0) {
                        kits = new StringBuilder(object.toString());
                    } else {
                        kits.append(", ").append(object.toString());
                    }
                } else {
                    UtilPermission perms = new UtilPermission(this.plugin, cs);
                    if (perms.has(kitData.get("kits." + object + ".permission").toString()) || perms.has("essence.kits.all")) {
                        if (i == 0) {
                            kits = new StringBuilder(object.toString());
                        } else {
                            kits.append(", ").append(object.toString());
                        }
                    }
                }
                i++;
            }

            kitData.close();

            new UtilMessage(this.plugin, cs).send("kit", "select", new String[] { kits.toString() });
        } else {
            UtilKit kit = new UtilKit(this.plugin, p);
            UtilMessage msg = new UtilMessage(this.plugin, cs);
            if (kit.giveKit(args[0]) == 0) {
                msg.send("kit", "done", new String[] { args[0] });
            } else if (kit.giveKit(args[0]) == 1) {
                msg.send("kit", "nopermission");
            } else if (kit.giveKit(args[0]) == 2) {
                msg.send("kit", "notexist");
            } else if (kit.giveKit(args[0]) == 3) {
                msg.send("kit", "max");
            }
        }
        return true;
    }
}
