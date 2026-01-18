package net.lewmc.essence.stats;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPermission;
import net.lewmc.foundry.Logger;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CommandSpeed extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandSpeed class.
     * @param plugin References to the main plugin class.
     */
    public CommandSpeed(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.stats.speed";
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
        UtilMessage message = new UtilMessage(this.plugin, cs);

        if (args.length == 1 || (args.length == 2 && (args[1].equalsIgnoreCase("fly") || args[1].equalsIgnoreCase("walk")))) {
            if (cs instanceof Player p) {
                return this.speedSelf(p, message, args);
            } else {
                message.send("speed","usage");
                return true;
            }
        }
        else if (args.length == 2 || (args.length == 3 && (args[2].equalsIgnoreCase("fly") || args[2].equalsIgnoreCase("walk")))) {
            return this.speedOther(new UtilPermission(this.plugin, cs), cs, message, args);
        } else {
            if (cs instanceof Player p) {
                message.send("speed", "current", new String[]{ String.valueOf(p.getWalkSpeed()*10) });
            } else {
                message.send("speed","usage");
            }
        }
        return true;
    }

    /**
     * Set your own speed.
     * @param p Player - The user whose speed to adjust.
     * @param msg MessageUtil - The messaging system.
     * @return boolean - If the operation was successful
     */
    private boolean speedSelf(Player p, UtilMessage msg, String[] args) {
        try {
            if (Objects.equals(args[0], "off") || Objects.equals(args[0], "reset") || Objects.equals(args[0], "default")) {
                if (args.length == 2 && args[1].equalsIgnoreCase("fly")) {
                    p.setFlySpeed(0.1F);
                    msg.send("speed", "resetfly");
                } else if (args.length == 2 && args[1].equalsIgnoreCase("walk")) {
                    p.setWalkSpeed(0.2F);
                    msg.send("speed", "resetwalk");
                } else {
                    p.setWalkSpeed(0.2F);
                    p.setFlySpeed(0.1F);
                    msg.send("speed", "reset");
                }
            } else {
                float speed = Float.parseFloat(args[0]);

                if (speed > 10 || speed < -10) {
                    msg.send("speed", "bounds");
                    return true;
                }

                if (args.length == 2 && args[1].equalsIgnoreCase("fly")) {
                    p.setFlySpeed(speed/10);
                    msg.send("speed", "setfly", new String[]{ args[0] });
                } else if (args.length == 2 && args[1].equalsIgnoreCase("walk")) {
                    p.setWalkSpeed(speed/10);
                    msg.send("speed", "setwalk", new String[]{ args[0] });
                } else {
                    p.setWalkSpeed(speed/10);
                    p.setFlySpeed(speed/10);
                    msg.send("speed", "set", new String[]{ args[0] });
                }
            }
        } catch (NumberFormatException | NullPointerException e) {
            msg.send("speed", "usage");
            msg.send("generic", "exception");
            new Logger(this.plugin.foundryConfig).severe("Unable to set player speed due to error:");
            new Logger(this.plugin.foundryConfig).severe(e.getMessage());
        }
        return true;
    }

    /**
     * Set another user's speed.
     * @param perms PermisionHandler - The permission system.
     * @param cs CommandSender - Who sent the command.
     * @param msg MessageUtil - The messaging system.
     * @param args String[] - List of command arguments.
     * @return boolean - If the operation was successful
     */
    private boolean speedOther(UtilPermission perms, CommandSender cs, UtilMessage msg, String[] args) {
        if (perms.has("essence.stats.speed.other")) {
            Player p = Bukkit.getPlayer(args[1]);
            if (p != null) {
                try {
                    if (Objects.equals(args[0], "off") || Objects.equals(args[0], "reset") || Objects.equals(args[0], "default")) {
                        if (args.length == 3 && args[2].equalsIgnoreCase("fly")) {
                            p.setFlySpeed(0.1F);
                            msg.send("speed", "resetflybyother", new String[]{ cs.getName() });
                            msg.sendTo(cs, "speed", "resetflyother", new String[]{ p.getName() });
                        } else if (args.length == 3 && args[2].equalsIgnoreCase("walk")) {
                            p.setWalkSpeed(0.2F);
                            msg.send("speed", "resetwalkbyother", new String[]{ cs.getName() });
                            msg.sendTo(cs, "speed", "resetwalkother", new String[]{ p.getName() });
                        } else {
                            p.setWalkSpeed(0.2F);
                            p.setFlySpeed(0.1F);
                            msg.send("speed", "resetbyother", new String[]{ cs.getName() });
                            msg.sendTo(cs, "speed", "resetother", new String[]{ p.getName() });
                        }
                    } else {
                        float speed = Float.parseFloat(args[0]);

                        if (speed > 10 || speed < -10) {
                            msg.send("speed", "bounds");
                            return true;
                        }

                        if (args.length == 3 && args[2].equalsIgnoreCase("fly")) {
                            p.setFlySpeed(speed/10);
                            msg.send("speed", "setflybyother", new String[]{cs.getName(), args[0]});
                            msg.sendTo(cs, "speed", "setflyother", new String[]{args[1],args[0]});
                        } else if (args.length == 3 && args[2].equalsIgnoreCase("walk")) {
                            p.setWalkSpeed(speed/10);
                            msg.send("speed", "setwalkbyother", new String[]{cs.getName(), args[0]});
                            msg.sendTo(cs, "speed", "setwalkother", new String[]{args[1],args[0]});
                        } else {
                            p.setWalkSpeed(speed/10);
                            p.setFlySpeed(speed/10);
                            msg.send("speed", "setbyother", new String[]{cs.getName(), args[0]});
                            msg.sendTo(cs, "speed", "setother", new String[]{args[1],args[0]});
                        }
                    }
                } catch (NumberFormatException | NullPointerException e) {
                    msg.send("speed", "usage");
                    msg.send("generic", "exception");
                    new Logger(this.plugin.foundryConfig).severe("Unable to set player speed due to error:");
                    new Logger(this.plugin.foundryConfig).severe(e.getMessage());
                }
                return true;
            } else {
                msg.send("generic", "playernotfound");
            }
            return true;
        } else {
            return perms.not();
        }
    }
}