package net.lewmc.essence.commands.chat;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NickCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the NickCommand class.
     * @param plugin References to the main plugin class.
     */
    public NickCommand(Essence plugin) {
        this.plugin = plugin;
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
    public boolean onCommand(
        @NotNull CommandSender cs,
        @NotNull Command command,
        @NotNull String s,
        String[] args
    ) {
        if (command.getName().equalsIgnoreCase("nick")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("nick")) { return cmd.disabled(); }

            PermissionHandler permission = new PermissionHandler(this.plugin, cs);
            if (!permission.has("essence.chat.nick.self")) { return permission.not(); }

            MessageUtil msg = new MessageUtil(this.plugin, cs);

            PlayerUtil pu = new PlayerUtil(this.plugin, cs);

            if (args.length == 1) {
                if (cmd.console(cs)) {
                    msg.send("nick","consoleusage");
                    return true;
                }

                if (new SecurityUtil().hasSpecialCharacters(args[0])) {
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
                if (!permission.has("essence.chat.nick.other")) { return permission.not(); }
                Player player = Bukkit.getPlayer(args[0]);

                if (player == null) {
                    msg.send("generic","playernotfound");
                    return true;
                }

                if (new SecurityUtil().hasSpecialCharacters(args[1])) {
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

        return false;
    }
}
