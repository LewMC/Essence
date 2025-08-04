package net.lewmc.essence.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFly extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandFly class.
     * @param plugin References to the main plugin class.
     */
    public CommandFly(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.stats.fly";
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
        if (cmd.isDisabled("fly")) { return cmd.disabled(); }

        UtilMessage message = new UtilMessage(this.plugin, cs);

        if (args.length > 0) {
            return this.flyOther(new UtilPermission(this.plugin, cs), cs, message, args);
        } else {
            if (!(cs instanceof Player)) {
                message.send("fly","usage");
                return true;
            } else {
                return this.flySelf((Player) cs, message);
            }
        }
    }

    /**
     * Fly the command sender.
     * @param p Player - The user to fly.
     * @param msg MessageUtil - The messaging system.
     * @return boolean - If the operation was successful
     */
    private boolean flySelf(Player p, UtilMessage msg) {
        if (this.plugin.flyingPlayers.contains(p.getUniqueId())) {
            p.setFlying(false);
            p.setAllowFlight(false);
            this.plugin.flyingPlayers.remove(p.getUniqueId());
            msg.send("fly", "stopped");
        } else {
            p.setAllowFlight(true);
            p.setFlying(true);
            this.plugin.flyingPlayers.add(p.getUniqueId());
            msg.send("fly", "flying");
        }
        return true;
    }

    /**
     * Fly another user.
     * @param perms PermisionHandler - The permission system.
     * @param cs CommandSender - The user to fly.
     * @param msg MessageUtil - The messaging system.
     * @param args String[] - List of command arguments.
     * @return boolean - If the operation was successful
     */
    private boolean flyOther(UtilPermission perms, CommandSender cs, UtilMessage msg, String[] args) {
        if (perms.has("essence.stats.fly.other")) {
            Player p = Bukkit.getPlayer(args[0]);
            if (p != null) {
                if (this.plugin.flyingPlayers.contains(p.getUniqueId())) {
                    p.setFlying(false);
                    p.setAllowFlight(false);
                    msg.send("fly", "stopother", new String[] { p.getName() });
                    msg.sendTo(p, "fly", "stopbyother", new String[] { cs.getName() });
                    this.plugin.flyingPlayers.remove(p.getUniqueId());
                } else {
                    p.setAllowFlight(true);
                    p.setFlying(true);
                    msg.send("fly", "flyother", new String[] { p.getName() });
                    msg.sendTo(p, "fly", "flybyother", new String[] { cs.getName() });
                    this.plugin.flyingPlayers.add(p.getUniqueId());
                }
            } else {
                msg.send("generic", "playernotfound");
            }
            return true;
        } else {
            return perms.not();
        }
    }
}