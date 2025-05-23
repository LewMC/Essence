package net.lewmc.essence.chat;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.core.UtilPlayer;
import net.lewmc.foundry.Security;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandNick extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the NickCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandNick(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.chat.nick.self";
    }

    /**
     * /nick command handler.
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("nick")) { return cmd.disabled(); }

        UtilMessage msg = new UtilMessage(this.plugin, cs);
        UtilPlayer pu = new UtilPlayer(this.plugin, cs);

        if (args.length == 1) {
            if (cmd.console(cs)) {
                msg.send("nick","consoleusage");
                return true;
            }

            if (new Security(this.plugin.config).hasSpecialCharacters(args[0])) {
                msg.send("nick","specialchars");
                return true;
            }

            if (args[0].equalsIgnoreCase("off")) {
                if (pu.removePlayerDisplayname((Player) cs)) {
                    msg.send("nick","success", new String[]{cs.getName()});
                }
                return true;
            }

            if (pu.setPlayerDisplayname((Player) cs, args[0])) {
                msg.send("nick","success", new String[]{args[0]});
            }
        } else if (args.length == 2) {
            UtilPermission perms = new UtilPermission(this.plugin, cs);
            if (!perms.has("essence.chat.nick.other")) { return perms.not(); }
            Player player = Bukkit.getPlayer(args[0]);

            if (player == null) {
                msg.send("generic","playernotfound");
                return true;
            }

            if (new Security(this.plugin.config).hasSpecialCharacters(args[1])) {
                msg.send("nick","specialchars");
                return true;
            }

            if (args[1].equalsIgnoreCase("off")) {
                if (pu.removePlayerDisplayname(player)) {
                    msg.send("nick","successother", new String[]{args[0], player.getName()});
                    msg.sendTo(player, "nick","changedby", new String[]{cs.getName(), player.getName()});
                }
                return true;
            }

            if (pu.setPlayerDisplayname(player, args[1])) {
                msg.send("nick","successother", new String[]{args[0], args[1]});
                msg.sendTo(player, "nick","changedby", new String[]{cs.getName(), args[1]});
            }
        } else {
            msg.send("nick","usage");
        }

        return true;
    }
}