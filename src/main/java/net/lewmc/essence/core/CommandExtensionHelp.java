package net.lewmc.essence.core;

import net.lewmc.essence.Essence;
import org.bukkit.command.CommandSender;

/**
 * Help command class.
 */
public class CommandExtensionHelp {
    private final UtilMessage message;
    private final String[] args;
    private final Essence plugin;
    private final CommandSender cs;

    /**
     * Constructor for the HelpCommand class.
     * @param plugin Essence - Reference to main plugin class.
     * @param message MessageUtil - Instance of the MessageUtil class.
     * @param args String[] - Array of command arguments.
     */
    public CommandExtensionHelp(Essence plugin, UtilMessage message, String[] args, CommandSender cs) {
        this.plugin = plugin;
        this.message = message;
        this.args = args;
        this.cs = cs;
    }

    /**
     * Runs the /help command.
     * @return If the command was executed correctly.
     */
    public boolean runHelpCommand() {
        UtilCommand cu = new UtilCommand(this.plugin, this.cs);
        if (args.length > 1) {
            if ("inventory".equalsIgnoreCase(args[1])) {
                if (args.length < 3 || args[2].equals("1")) {
                    int blank = 0;
                    this.message.send("help", "inventory");
                    if (!cu.isDisabled("anvil")) { this.message.send("help", "anvil"); } else { blank++; }
                    if (!cu.isDisabled("cartography")) { this.message.send("help", "cartography"); } else { blank++; }
                    if (!cu.isDisabled("craft")) { this.message.send("help", "craft"); } else { blank++; }
                    if (!cu.isDisabled("grindstone")) { this.message.send("help", "grindstone"); } else { blank++; }
                    if (!cu.isDisabled("loom")) { this.message.send("help", "loom"); } else { blank++; }
                    if (!cu.isDisabled("smithing")) { this.message.send("help", "smithing"); } else { blank++; }
                    if (!cu.isDisabled("stonecutter")) { this.message.send("help", "stonecutter"); } else { blank++; }
                    if (!cu.isDisabled("enderchest")) { this.message.send("help", "echest"); } else { blank++; }
                    this.blank(blank);
                    this.message.send("help", "page", new String[] { "1", "2" });
                } else if (args[2].equals("2")) {
                    int blank = 7;
                    this.message.send("help", "inventory");
                    if (!cu.isDisabled("trash")) { this.message.send("help", "trash"); } else { blank++; }
                    this.blank(blank);
                    this.message.send("help", "page", new String[] { "2", "2" });
                }
            } else if ("gamemode".equalsIgnoreCase(args[1])) {
                int blank = 3;
                this.message.send("help", "gamemode");
                if (!cu.isDisabled("gamemode")) { this.message.send("help", "gm"); } else { blank++; }
                if (!cu.isDisabled("gmc")) { this.message.send("help", "gmc"); } else { blank++; }
                if (!cu.isDisabled("gms")) { this.message.send("help", "gms"); } else { blank++; }
                if (!cu.isDisabled("gma")) { this.message.send("help", "gma"); } else { blank++; }
                if (!cu.isDisabled("gmsp")) { this.message.send("help", "gmsp"); } else { blank++; }
                this.blank(blank);
                this.message.send("help", "page", new String[] { "1", "1" });
            } else if ("teleport".equalsIgnoreCase(args[1])) {
                if (args.length < 3 || args[2].equals("1")) {
                    int blank = 0;
                    this.message.send("help", "teleport");
                    if (!cu.isDisabled("tp")) { this.message.send("help", "tp"); } else { blank++; }
                    if (!cu.isDisabled("tpa")) { this.message.send("help", "tpa"); } else { blank++; }
                    if (!cu.isDisabled("tpaccept")) { this.message.send("help", "tpaccept"); } else { blank++; }
                    if (!cu.isDisabled("tpdeny")) { this.message.send("help", "tpdeny"); } else { blank++; }
                    if (!cu.isDisabled("tpahere")) { this.message.send("help", "tpahere"); } else { blank++; }
                    if (!cu.isDisabled("tptoggle")) { this.message.send("help", "tptoggle"); } else { blank++; }
                    if (!cu.isDisabled("tpcancel")) { this.message.send("help", "tpcancel"); } else { blank++; }
                    if (!cu.isDisabled("warp")) { this.message.send("help", "warp"); } else { blank++; }
                    this.blank(blank);
                    this.message.send("help", "page", new String[] { "1", "3" });
                } else if (args[2].equals("2")) {
                    int blank = 0;
                    this.message.send("help", "teleport");
                    if (!cu.isDisabled("warps")) { this.message.send("help", "warps"); } else { blank++; }
                    if (!cu.isDisabled("setwarp")) { this.message.send("help", "setwarp"); } else { blank++; }
                    if (!cu.isDisabled("delwarp")) { this.message.send("help", "delwarp"); } else { blank++; }
                    if (!cu.isDisabled("home")) { this.message.send("help", "home"); } else { blank++; }
                    if (!cu.isDisabled("homes")) { this.message.send("help", "homes"); } else { blank++; }
                    if (!cu.isDisabled("sethome")) { this.message.send("help", "sethome"); } else { blank++; }
                    if (!cu.isDisabled("delhome")) { this.message.send("help", "delhome"); } else { blank++; }
                    if (!cu.isDisabled("back")) { this.message.send("help", "back"); } else { blank++; }
                    this.blank(blank);
                    this.message.send("help", "page", new String[] { "2", "3" });
                } else if (args[2].equals("3")) {
                    int blank = 0;
                    this.message.send("help", "teleport");
                    if (!cu.isDisabled("setspawn")) { this.message.send("help", "setspawn"); } else { blank++; }
                    if (!cu.isDisabled("spawn")) { this.message.send("help", "spawn"); } else { blank++; }
                    if (!cu.isDisabled("world")) { this.message.send("help", "world"); } else { blank++; }
                    if (!cu.isDisabled("tprandom")) { this.message.send("help", "tpr"); } else { blank++; }
                    if (!cu.isDisabled("thome")) { this.message.send("help", "thome"); } else { blank++; }
                    if (!cu.isDisabled("thomes")) { this.message.send("help", "thomes"); } else { blank++; }
                    if (!cu.isDisabled("setthome")) { this.message.send("help", "setthome"); } else { blank++; }
                    if (!cu.isDisabled("delthome")) { this.message.send("help", "delthome"); } else { blank++; }
                    this.blank(blank);
                    this.message.send("help", "page", new String[] { "3", "3" });
                }
            } else if ("stats".equalsIgnoreCase(args[1])) {
                int blank = 5;
                this.message.send("help", "stats");
                if (!cu.isDisabled("feed")) { this.message.send("help", "feed"); } else { blank++; }
                if (!cu.isDisabled("heal")) { this.message.send("help", "heal"); } else { blank++; }
                if (!cu.isDisabled("repair")) { this.message.send("help", "repair"); } else { blank++; }
                this.blank(blank);
                this.message.send("help", "page", new String[] { "1", "1" });
            } else if ("economy".equalsIgnoreCase(args[1])) {
                int blank = 6;
                this.message.send("help", "economy");
                if (!cu.isDisabled("pay")) { this.message.send("help", "pay"); } else { blank++; }
                if (!cu.isDisabled("balance")) { this.message.send("help", "bal"); } else { blank++; }
                this.blank(blank);
                this.message.send("help", "page", new String[] { "1", "1" });
            } else if ("admin".equalsIgnoreCase(args[1])) {
                int blank = 4;
                this.message.send("help", "admin");
                if (!cu.isDisabled("info")) { this.message.send("help", "info"); } else { blank++; }
                if (!cu.isDisabled("seen")) { this.message.send("help", "seen"); } else { blank++; }
                if (!cu.isDisabled("invisible")) { this.message.send("help", "invisible"); } else { blank++; }
                if (!cu.isDisabled("visible")) { this.message.send("help", "visible"); } else { blank++; }
                this.blank(blank);
                this.message.send("help", "page", new String[] { "1", "1" });
            } else if ("chat".equalsIgnoreCase(args[1])) {
                int blank = 3;
                this.message.send("help", "chat");
                if (!cu.isDisabled("broadcast")) { this.message.send("help", "broadcast"); } else { blank++; }
                if (!cu.isDisabled("msg")) { this.message.send("help", "msg"); } else { blank++; }
                if (!cu.isDisabled("reply")) { this.message.send("help", "reply"); } else { blank++; }
                if (!cu.isDisabled("nick")) { this.message.send("help", "nickself"); } else { blank++; }
                if (!cu.isDisabled("nick")) { this.message.send("help", "nickother"); } else { blank++; }
                this.blank(blank);
                this.message.send("help", "page", new String[] { "1", "1" });
            } else if ("environment".equalsIgnoreCase(args[1])) {
                int blank = 4;
                this.message.send("help", "environment");
                if (!cu.isDisabled("time")) { this.message.send("help", "time"); } else { blank++; }
                if (!cu.isDisabled("weather")) { this.message.send("help", "weather"); } else { blank++; }
                if (!cu.isDisabled("ptime")) { this.message.send("help", "ptime"); } else { blank++; }
                if (!cu.isDisabled("pweather")) { this.message.send("help", "pweather"); } else { blank++; }
                this.blank(blank);
                this.message.send("help", "page", new String[] { "1", "1" });
            } else if ("misc".equalsIgnoreCase(args[1])) {
                int blank = 6;
                this.message.send("help", "misc");
                if (!cu.isDisabled("rules")) { this.message.send("help", "rules"); } else { blank++; }
                if (!cu.isDisabled("kit")) { this.message.send("help", "kit"); } else { blank++; }
                this.blank(blank);
                this.message.send("help", "page", new String[] { "1", "1" });
            } else if ("team".equalsIgnoreCase(args[1]) && !cu.isDisabled("team")) {
                if (args.length < 3 || args[2].equals("1")) {
                    this.message.send("help", "team");
                    this.message.send("help", "teamcreate");
                    this.message.send("help", "teamjoin");
                    this.message.send("help", "teamleave");
                    this.message.send("help", "teamrequests");
                    this.message.send("help", "teamaccept");
                    this.message.send("help", "teamdecline");
                    this.message.send("help", "teamtransfer");
                    this.message.send("help", "teamkick");
                    this.message.send("help", "page", new String[] { "1", "2" });
                } else if (args[2].equals("2")) {
                    int blank = 3;
                    this.message.send("help", "team");
                    this.message.send("help", "teamdisband");
                    if (!cu.isDisabled("thome")) { this.message.send("help", "thome"); } else { blank++; }
                    if (!cu.isDisabled("thomes")) { this.message.send("help", "thomes"); } else { blank++; }
                    if (!cu.isDisabled("setthome")) { this.message.send("help", "setthome"); } else { blank++; }
                    if (!cu.isDisabled("delthome")) { this.message.send("help", "delthome"); } else { blank++; }
                    this.blank(blank);
                    this.message.send("help", "page", new String[] { "2", "2" });
                }
            } else if ("2".equalsIgnoreCase(args[1])) {
                this.message.send("help", "help");
                this.message.send("help", "helpadmin");
                this.message.send("help", "helpmisc");
                this.blank(6);
                this.message.send("help", "page", new String[] { "2", "2" });
            } else {
                this.message.send("help", "nochapter");
            }
        } else {
            int blank = 0;
            this.message.send("help", "help");
            this.message.send("help", "helpchat");
            this.message.send("help", "helpinventory");
            this.message.send("help", "helpgamemode");
            this.message.send("help", "helpteleport");
            this.message.send("help", "helpstats");
            this.message.send("help", "helpeconomy");
            if (!cu.isDisabled("team")) { this.message.send("help", "helpteam"); } else { blank++; }
            this.message.send("help", "helpenvironment");
            if (blank != 0) {
                this.message.send("help", "helpadmin");
                this.message.send("help", "helpmisc");
                this.message.send("help", "page", new String[] { "1", "1" });
            } else {
                this.message.send("help", "page", new String[]{"1", "2"});
            }
        }
        return true;
    }

    /**
     * Sends a set number of blank lines to the command executor.
     * @param number int - Number of blank lines to send.
     */
    private void blank(int number) {
        for (int i = 0; i < number; i++) {
            this.message.send("generic", "blankmessage");
        }
    }
}
