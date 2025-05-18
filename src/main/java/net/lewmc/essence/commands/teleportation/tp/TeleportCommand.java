package net.lewmc.essence.commands.teleportation.tp;

import net.lewmc.essence.utils.*;
import net.lewmc.essence.Essence;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TeleportCommand implements CommandExecutor {
    private final Essence plugin;

    /**
     * Constructor for the TeleportCommand class.
     * @param plugin References to the main plugin class.
     */
    public TeleportCommand(Essence plugin) {
        this.plugin = plugin;
    }

    /**
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
        if (command.getName().equalsIgnoreCase("tp")) {
            CommandUtil cmd = new CommandUtil(this.plugin, cs);
            if (cmd.isDisabled("tp")) { return cmd.disabled(); }

            MessageUtil message = new MessageUtil(this.plugin, cs);

            Player player = null;

            if (!console(cs)) {
                player = (Player) cs;
            }

            PermissionHandler permission = new PermissionHandler(this.plugin, cs);

            if (args.length == 0) {
                message.send("teleport", "usage");
                return true;
            }

            TeleportUtil tp = new TeleportUtil(this.plugin);

            // /tp <selector> <x> <y> <z>
            if (args.length >= 4) {
                boolean isSelf = false;
                if (cs instanceof Player && (args[0].equalsIgnoreCase("@s") || (args[0].equalsIgnoreCase(cs.getName())))) {
                    isSelf = true;
                }

                if (isSelf) {
                    if (!permission.has("essence.teleport.coord")) {
                        return permission.not();
                    }
                } else {
                    if (!(permission.has("essence.teleport.other") && permission.has("essence.teleport.coord"))) {
                        return permission.not();
                    }
                }
                String selector = args[0];
                List<Player> targets = parseSelector(selector, cs);
                if (targets.isEmpty()) {
                    message.send("generic", "playernotfound");
                    return true;
                }
                double x, y, z;
                try {
                    Player ref = (cs instanceof Player) ? (Player) cs : targets.getFirst();
                    x = args[1].equals("~") ? ref.getLocation().getX() : Double.parseDouble(args[1]);
                    y = args[2].equals("~") ? ref.getLocation().getY() : Double.parseDouble(args[2]);
                    z = args[3].equals("~") ? ref.getLocation().getZ() : Double.parseDouble(args[3]);
                } catch (Exception e) {
                    message.send("generic", "numberformaterror");
                    return true;
                }
                for (Player t : targets) {

                    if (!tp.teleportToggleCheck(player, t)) {
                        message.send("teleport", "requestsdisabled", new String[] { t.getName() });
                    } else {
                        Location loc = new Location(t.getWorld(), x, y, z);
                        tp.doTeleport(t, loc, 0);
                    }
                }
                message.send("teleport", "tocoord", new String[] { x+", "+y+", "+z });
                return true;
            }

            // /tp <selector1> <selector2> or /tp <selector1> <player2>
            if (args.length == 2) {
                if (!(permission.has("essence.teleport.other") && permission.has("essence.teleport.player"))) {
                    return permission.not();
                }
                List<Player> fromList = parseSelector(args[0], cs);
                List<Player> toList = parseSelector(args[1], cs);
                if (fromList.isEmpty() || toList.isEmpty()) {
                    message.send("generic", "playernotfound");
                    return true;
                }
                Player to = toList.getFirst();
                for (Player from : fromList) {

                    if (!tp.teleportToggleCheck(player, to)) {
                        message.send("teleport", "requestsdisabled", new String[] { to.getName() });
                    } else {
                        tp.doTeleport(from, to.getLocation(), 0); // Folia
                    }
                }
                message.send("teleport", "toplayer", new String[] { fromList.stream().map(Player::getName).collect(Collectors.joining(", ")), to.getName() });
                return true;
            }

            // /tp <x> <y> <z> (Teleport yourself to the coordinates)
            if (args.length == 3 && player != null) {
                if (!permission.has("essence.teleport.coord")) {
                    return permission.not();
                }
                double x, y, z;
                try {
                    x = args[0].equals("~") ? player.getLocation().getX() : Double.parseDouble(args[0]);
                    y = args[1].equals("~") ? player.getLocation().getY() : Double.parseDouble(args[1]);
                    z = args[2].equals("~") ? player.getLocation().getZ() : Double.parseDouble(args[2]);
                } catch (Exception e) {
                    message.send("generic", "numberformaterror");
                    return true;
                }
                Location loc = new Location(player.getWorld(), x, y, z);
                tp.doTeleport(player, loc, 0); // Folia兼容
                message.send("teleport", "tocoord", new String[] { x+", "+y+", "+z });
                return true;
            }

            // /tp <player> Player to player teleport only
            if (args.length == 1 && player != null) {
                if (!permission.has("essence.teleport.player")) {
                    return permission.not();
                }

                List<Player> toList = parseSelector(args[0], cs);
                if (toList.isEmpty()) {
                    message.send("generic", "playernotfound");
                    return true;
                }
                Player to = toList.getFirst();

                if (!tp.teleportToggleCheck(player, to)) {
                    message.send("teleport", "requestsdisabled", new String[] { to.getName() });
                    return true;
                }

                tp.doTeleport(player, to.getLocation(), 0);
                message.send("teleport", "to", new String[] { to.getName() });
                return true;
            }

            message.send("teleport", "usage");
            return true;
        }

        return false;
    }

    /**
     * Checks if the command sender is the console.
     * @param commandSender CommandSender - The command sender.
     * @return boolean - If the command sender is the console.
     */
    public boolean console(CommandSender commandSender) {
        return !(commandSender instanceof Player);
    }

    /**
     * Parse the target selector and return a list of matching players
     * @param selector The selector to be parsed
     * @param sender The command sender.
     * @return List a list of Player.
     */
    private List<Player> parseSelector(String selector, CommandSender sender) {
        if (selector.equalsIgnoreCase("@s")) {
            if (sender instanceof Player) {
                List<Player> list = new ArrayList<>();
                list.add((Player) sender);
                return list;
            }
            return new ArrayList<>();
        } else if (selector.equalsIgnoreCase("@a")) {
            return new ArrayList<>(Bukkit.getOnlinePlayers());
        } else if (selector.equalsIgnoreCase("@p")) {
            if (sender instanceof Player) {
                Player self = (Player) sender;
                Player nearest = null;
                double minDist = Double.MAX_VALUE;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.equals(self)) continue;
                    double dist = p.getLocation().distance(self.getLocation());
                    if (dist < minDist) {
                        minDist = dist;
                        nearest = p;
                    }
                }
                if (nearest != null) {
                    List<Player> list = new ArrayList<>();
                    list.add(nearest);
                    return list;
                }
            }
            return new ArrayList<>();
        } else {
            Player p = plugin.getServer().getPlayer(selector);
            if (p != null) {
                List<Player> list = new ArrayList<>();
                list.add(p);
                return list;
            }
            return new ArrayList<>();
        }
    }
}