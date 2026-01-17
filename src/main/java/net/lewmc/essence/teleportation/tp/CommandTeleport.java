package net.lewmc.essence.teleportation.tp;

import com.tcoded.folialib.FoliaLib;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilPlayer;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandTeleport extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the TeleportCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandTeleport(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
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
     * @return boolean  true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage message = new UtilMessage(this.plugin, cs);

        Player player = null;

        if (!console(cs)) { player = (Player) cs; }

        UtilPermission permission = new UtilPermission(this.plugin, cs);

        if (args.length == 0) {
            message.send("teleport", "usage");
            return true;
        }

        UtilTeleport tp = new UtilTeleport(this.plugin);

        // /tp <selector> <x> <y> <z>
        if (args.length >= 4) {
            boolean isSelf = cs instanceof Player && (args[0].equalsIgnoreCase("@s") || (args[0].equalsIgnoreCase(cs.getName())));

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
                x = args[1].startsWith("~") ?
                        (args[1].length() == 1 ? ref.getLocation().getX() : ref.getLocation().getX() + Double.parseDouble(args[1].substring(1))) :
                        Double.parseDouble(args[1]);
                y = args[2].startsWith("~") ?
                        (args[2].length() == 1 ? ref.getLocation().getY() : ref.getLocation().getY() + Double.parseDouble(args[2].substring(1))) :
                        Double.parseDouble(args[2]);
                z = args[3].startsWith("~") ?
                        (args[3].length() == 1 ? ref.getLocation().getZ() : ref.getLocation().getZ() + Double.parseDouble(args[3].substring(1))) :
                        Double.parseDouble(args[3]);
            } catch (Exception e) {
                message.send("generic", "numberformaterror");
                return true;
            }
            for (Player t : targets) {

                if (!tp.teleportToggleCheck(player, t)) {
                    message.send("teleport", "requestsdisabled", new String[] { t.getName() });
                } else {
                    Location loc = new Location(t.getWorld(), x, y, z);
                    tp.doTeleport(t, loc, 0, true);
                }
            }
            message.send("teleport", "tocoord", new String[] { String.format("%.1f, %.1f, %.1f", x, y, z) });
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
                    tp.doTeleport(from, to.getLocation(), 0, true); // Folia
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
                x = args[0].startsWith("~") ? 
                    (args[0].length() == 1 ? player.getLocation().getX() : player.getLocation().getX() + Double.parseDouble(args[0].substring(1))) : 
                    Double.parseDouble(args[0]);
                y = args[1].startsWith("~") ? 
                    (args[1].length() == 1 ? player.getLocation().getY() : player.getLocation().getY() + Double.parseDouble(args[1].substring(1))) : 
                    Double.parseDouble(args[1]);
                z = args[2].startsWith("~") ? 
                    (args[2].length() == 1 ? player.getLocation().getZ() : player.getLocation().getZ() + Double.parseDouble(args[2].substring(1))) : 
                    Double.parseDouble(args[2]);
            } catch (Exception e) {
                message.send("generic", "numberformaterror");
                return true;
            }
            Location loc = new Location(player.getWorld(), x, y, z);
            tp.doTeleport(player, loc, 0, true);
            message.send("teleport", "tocoord", new String[] { String.format("%.1f, %.1f, %.1f", x, y, z) });
            return true;
        }

        // /tp <player> Player to player teleport only
        if (args.length == 1 && player != null) {
            if (!permission.has("essence.teleport.player")) {
                return permission.not();
            }

            List<Player> toList = parseSelector(args[0], cs);
            if (!toList.isEmpty()) {
                Player to = toList.getFirst();

                if (!tp.teleportToggleCheck(player, to)) {
                    message.send("teleport", "requestsdisabled", new String[]{to.getName()});
                    return true;
                }

                tp.doTeleport(player, to.getLocation(), 0, true);
                message.send("teleport", "to", new String[]{to.getName()});
                return true;
            }

            FoliaLib flib = new FoliaLib(this.plugin);

            if (permission.has("essence.teleport.offline")) {
                flib.getScheduler().runAsync(task -> {
                    UtilPlayer up = new UtilPlayer(this.plugin);
                    if (!Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) {
                        flib.getScheduler().runAtEntity((Player) cs, t -> message.send("generic", "playernotfound"));
                        return;
                    }

                    UUID uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
                    if (!up.loadPlayer(uuid)) {
                        flib.getScheduler().runAtEntity((Player) cs, t -> message.send("teleport", "offlineplayernodata"));
                        return;
                    }

                    double x = (double) up.getPlayer(uuid, UtilPlayer.KEYS.LAST_LOCATION_X);
                    double y = (double) up.getPlayer(uuid, UtilPlayer.KEYS.LAST_LOCATION_Y);
                    double z = (double) up.getPlayer(uuid, UtilPlayer.KEYS.LAST_LOCATION_Z);
                    String worldName = (String) up.getPlayer(uuid, UtilPlayer.KEYS.LAST_LOCATION_WORLD);

                    flib.getScheduler().runAtEntity((Player) cs, t -> {
                        World world = Bukkit.getWorld(worldName);
                        if (world == null) {
                            message.send("teleport", "offlinenoworld");
                            return;
                        }

                        Location offlineLoc = new Location(world, x, y, z);
                        tp.doTeleport((Player) cs, offlineLoc, 0, true);

                        message.send("teleport", "to", new String[]{up.getPlayer(uuid, UtilPlayer.KEYS.USER_NICKNAME) != null ? up.getPlayer(uuid, UtilPlayer.KEYS.USER_NICKNAME).toString() : args[0]});
                        message.send("teleport", "tooffline");
                    });
                });
            } else {
                message.send("generic", "playernotfound");
            }
            return true;
        }

        message.send("teleport", "usage");
        return true;
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
            if (sender instanceof Player self) {
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