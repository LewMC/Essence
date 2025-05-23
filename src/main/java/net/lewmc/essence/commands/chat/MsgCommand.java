package net.lewmc.essence.commands.chat;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.CommandUtil;
import net.lewmc.essence.utils.MessageUtil;
import net.lewmc.essence.utils.PermissionHandler;
import net.lewmc.essence.utils.placeholders.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MsgCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the MsgCommand class.
     * @param plugin References to the main plugin class.
     */
    public MsgCommand(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * /msg command handler.
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
        if (command.getName().equalsIgnoreCase("msg")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("msg")) { return cmd.disabled(); }

            PermissionHandler permission = new PermissionHandler(this.plugin, cs);
            if (!permission.has("essence.chat.msg")) { return permission.not(); }

            MessageUtil message = new MessageUtil(plugin, cs);

            if (args.length > 1) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if ((p.getName().toLowerCase()).equalsIgnoreCase(args[0])) {
                        String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                        msg = new PlaceholderUtil(this.plugin, cs).replaceAll(msg);

                        String[] repl = new String[] {cs.getName(), p.getName(), msg};

                        message.send("msg", "send", repl);
                        message.sendTo(p, "msg", "send", repl);

                        if (this.plugin.msgHistory.containsKey(p)) {
                            this.plugin.msgHistory.replace(p, cs);
                        } else {
                            this.plugin.msgHistory.put(p, cs);
                        }

                        return true;
                    }
                }
                message.send("generic", "playernotfound");
            } else {
                message.send("msg","usage");
            }

            return true;
        }

        return false;
    }
}
