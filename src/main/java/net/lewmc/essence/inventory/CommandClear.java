package net.lewmc.essence.inventory;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.TypePendingRequests;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.core.UtilPlayer;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * /clear command.
 */
public class CommandClear extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandClear class.
     *
     * @param plugin References to the main plugin class
     */
    public CommandClear(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission
     * @return String - the permission string
     */
    @Override
    protected String requiredPermission() {
        return "essence.inventory.clear";
    }

    /**
     * /clear command handler.
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not? */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage msg = new UtilMessage(this.plugin, cs);
        UtilPermission perm = new UtilPermission(this.plugin, cs);

        Player executor = (cs instanceof Player p) ? p : null;
        Player target = (args.length == 1) ? Bukkit.getPlayer(args[0]) : (executor != null && args.length == 0 ? executor : null);
        if (confirmRequired(executor, target, msg)) {
            return true;
        }

        if (args.length == 1) {
            if (target == null) {
                msg.send("generic", "playernotfound");
                return true;
            }

            if (perm.has("essence.inventory.clear.other")) {
                target.getInventory().clear();
                this.plugin.pendingClears.remove(executor.getUniqueId());
                msg.send("clear", "clearedother", new String[]{target.getName()});
                msg.sendTo(target, "clear", "clearedby", new String[]{cs.getName()});
            } else {
                return perm.not();
            }
        } else if (args.length == 0) {
            executor.getInventory().clear();
            msg.send("clear", "cleared");
        } else {
            msg.send("clear", "usage");
        }

        return true;
    }

    /**
     * Checks if confirmation is required.
     * @param executor Player
     * @param target Player
     * @param msg UtilMessage
     * @return true = yes, false = no
     */
    private boolean confirmRequired(Player executor, Player target, UtilMessage msg) {
        if (executor == null) return false;

        UtilPlayer up = new UtilPlayer(this.plugin);

        boolean requireConfirm = up.getPlayer(executor.getUniqueId(), UtilPlayer.KEYS.USER_CONFIRM_CLEAR) instanceof Boolean b && b;
        if (!requireConfirm) return false;

        UUID executorId = executor.getUniqueId();
        UUID targetId = (target != null ? target.getUniqueId() : executorId);

        TypePendingRequests.TypePendingClears pending = this.plugin.pendingClears.get(executorId);

        boolean sameTarget = pending != null && pending.target.equals(targetId);

        if (pending == null || ((System.currentTimeMillis() - pending.time) > 30_000) || !sameTarget) {
            TypePendingRequests.TypePendingClears newPending = new TypePendingRequests.TypePendingClears();
            newPending.time = System.currentTimeMillis();
            newPending.target = targetId;

            this.plugin.pendingClears.put(executorId, newPending);
            msg.send("clear", "confirm");
            return true;
        }

        this.plugin.pendingClears.remove(executorId);
        return false;
    }

}