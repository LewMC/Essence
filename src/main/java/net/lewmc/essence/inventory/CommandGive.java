package net.lewmc.essence.inventory;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.core.UtilPlayer;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /give command.
 */
public class CommandGive extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandGive class.
     *
     * @param plugin References to the main plugin class
     */
    public CommandGive(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission
     * @return String - the permission string
     */
    @Override
    protected String requiredPermission() {
        return "essence.inventory.give";
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
        UtilMessage msg = new UtilMessage(this.plugin, cs);
        UtilPlayer pu = new UtilPlayer(this.plugin, cs);
        UtilPermission perm = new UtilPermission(this.plugin, cs);

        if (args.length == 1) {
            if (cs instanceof Player player) {
                if (perm.itemIsBlacklisted(args[0])) { msg.send("give","blacklisted", new String[] {args[0]}); return true; }

                if (pu.givePlayerItem(player, args[0], 1)) {
                    msg.send("give","gaveself", new String[] {"1", args[0]});
                } else {
                    msg.send("give", "itemnotfound");
                }
            } else {
                msg.send("give", "usage");
                return true;
            }
        } else if (args.length == 2 || args.length == 3) {
            Player player = Bukkit.getPlayer(args[1]);

            if (player == null || player == cs) {
                if (player == cs) {
                    if (perm.itemIsBlacklisted(args[0])) { msg.send("give","blacklisted", new String[] {args[0]}); return true; }
                    if (pu.givePlayerItem(player, args[0], Integer.parseInt(args[1]))) {
                        msg.send("give","gaveself", new String[] {args[1], args[0]});
                    } else {
                        msg.send("give", "itemnotfound");
                    }
                } else {
                    msg.send("generic", "playernotfound");
                    return true;
                }
            } else {
                if (!perm.has("essence.inventory.give.other")) { return perm.not(); }
                if (args.length == 2) {
                    if (perm.itemIsBlacklisted(args[1])) { msg.send("give","blacklisted", new String[] {args[1]}); return true; }
                    if (pu.givePlayerItem(player, args[1], 1)) {
                        msg.send("give","gaveother", new String[] {"1", args[1], args[0]});
                    } else {
                        msg.send("give", "itemnotfound");
                    }
                } else {
                    if (perm.itemIsBlacklisted(args[1])) { msg.send("give","blacklisted", new String[] {args[1]}); return true; }
                    if (pu.givePlayerItem(player, args[1], Integer.parseInt(args[2]))) {
                        msg.send("give","gaveother", new String[] {args[2], args[1], args[0]});
                    } else {
                        msg.send("give", "itemnotfound");
                    }
                }
            }
        } else {
            msg.send("give", "usage");
            return true;
        }
        return true;
    }
}