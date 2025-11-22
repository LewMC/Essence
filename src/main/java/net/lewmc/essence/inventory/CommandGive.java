package net.lewmc.essence.inventory;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilItem;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.core.UtilPlayer;
import net.lewmc.foundry.Parser;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /give command.
 */
public class CommandGive extends FoundryCommand {
    private final Essence plugin;
    private UtilMessage msg;

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
        this.msg = new UtilMessage(this.plugin, cs);
        UtilPlayer up = new UtilPlayer(this.plugin);
        UtilPermission perm = new UtilPermission(this.plugin, cs);
        UtilItem ui = new UtilItem(this.plugin);

        if (args.length == 1) {
            if (cs instanceof Player player) {
                if (ui.itemIsBlacklisted(args[0])) { this.msg.send("give","blacklisted", new String[] {args[0]}); return true; }

                if (up.givePlayerItem(player, args[0], 1)) {
                    this.msg.send("give","gaveself", new String[] {"1", args[0]});
                } else {
                    this.msg.send("generic", "itemnotfound", new String[] {args[0]});
                }
            } else {
                this.msg.send("give", "usage");
                return true;
            }
        } else if (args.length == 2) {
            // Try to parse as /give <item> <amount> for self
            if (cs instanceof Player player && Parser.isNumeric(args[1]) ) {
                try {
                    int amount = this.parseItemAmount(args[1]);

                    if (ui.itemIsBlacklisted(args[0])) {
                        this.msg.send("give", "blacklisted", new String[]{args[0]});
                        return true;
                    }
                    if (up.givePlayerItem(player, args[0], amount)) {
                        this.msg.send("give", "gaveself", new String[]{String.valueOf(amount), args[0]});
                    } else {
                        this.msg.send("generic", "itemnotfound", new String[]{args[1]});
                    }
                    return true;
                } catch (NumberFormatException e) {
                    this.msg.send("generic", "exception");
                    this.plugin.log.warn(e.getMessage());
                }
            }

            // Parse as /give <player> <item>
            if (!perm.has("essence.inventory.give.other")) {
                return perm.not();
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                this.msg.send("generic", "playernotfound");
                return true;
            }
            if (ui.itemIsBlacklisted(args[1])) {
                this.msg.send("give", "blacklisted", new String[]{args[1]});
                return true;
            }
            if (up.givePlayerItem(target, args[1], 1)) {
                this.msg.send("give", "gaveother", new String[]{"1", args[1], target.getName()});
            } else {
                this.msg.send("give", "itemnotfound", new String[]{args[1]});
            }
        } else if (args.length == 3) {
            // /give <player> <item> <amount>
            if (!perm.has("essence.inventory.give.other")) {
                return perm.not();
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                this.msg.send("generic", "playernotfound");
                return true;
            }
            try {
                int amount = this.parseItemAmount(args[2]);
                if (ui.itemIsBlacklisted(args[1])) {
                    this.msg.send("give", "blacklisted", new String[]{args[1]});
                    return true;
                }
                if (up.givePlayerItem(target, args[1], amount)) {
                    this.msg.send("give", "gaveother", new String[]{String.valueOf(amount), args[1], target.getName()});
                } else {
                    this.msg.send("give", "itemnotfound", new String[]{args[1]});
                }
            } catch (NumberFormatException e) {
                this.msg.send("give", "usage");
            }
        } else {
            this.msg.send("give", "usage");
            return true;
        }
        return true;
    }

    /**
     * Parses the amount being requested.
     * @param amount String - The amount requested
     * @return int - The amount validated as an integer, or zero if invalid.
     */
    private int parseItemAmount(String amount) {
        try {
            int iAmount = Integer.parseInt(amount);
            if (iAmount <= 0 || iAmount > 2304) {
                this.msg.send("give", "invalidamount", new String[]{amount});
                return 0;
            } else {
                return iAmount;
            }
        } catch (Exception e) {
            this.msg.send("give", "invalidamount", new String[]{amount});
            return 0;
        }
    }
}