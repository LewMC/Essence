package net.lewmc.essence.commands;

import net.lewmc.essence.utils.MessageUtil;

/**
 * Help command class.
 */
public class HelpCommand {
    private final MessageUtil message;
    private final String[] args;

    /**
     * Constructor for the HelpCommand class.
     * @param message MessageUtil - Instance of the MessageUtil class.
     * @param args String[] - Array of command arguments.
     */
    public HelpCommand(MessageUtil message, String[] args) {
        this.message = message;
        this.args = args;
    }

    /**
     * Runs the /help command.
     * @return If the command was executed correctly.
     */
    public boolean runHelpCommand() {
        if (args.length > 1) {
            if ("inventory".equalsIgnoreCase(args[1])) {
                if (args.length < 3 || args[2].equals("1")) {
                    this.message.send("help", "inventory");
                    this.message.send("help", "anvil");
                    this.message.send("help", "cartography");
                    this.message.send("help", "craft");
                    this.message.send("help", "grindstone");
                    this.message.send("help", "loom");
                    this.message.send("help", "smithing");
                    this.message.send("help", "stonecutter");
                    this.message.send("help", "echest");
                    this.message.send("help", "page", new String[] { "1", "2" });
                } else if (args[2].equals("2")) {
                    this.message.send("help", "inventory");
                    this.message.send("help", "trash");
                    this.blank(7);
                    this.message.send("help", "page", new String[] { "2", "2" });
                }
            } else if ("gamemode".equalsIgnoreCase(args[1])) {
                this.message.send("help", "gamemode");
                this.message.send("help", "gmc");
                this.message.send("help", "gms");
                this.message.send("help", "gma");
                this.message.send("help", "gmsp");
                this.blank(4);
                this.message.send("help", "page", new String[] { "1", "1" });
            } else if ("teleport".equalsIgnoreCase(args[1])) {
                if (args.length < 3 || args[2].equals("1")) {
                    this.message.send("help", "teleport");
                    this.message.send("help", "tp");
                    this.message.send("help", "tpa");
                    this.message.send("help", "tpaccept");
                    this.message.send("help", "tpdeny");
                    this.message.send("help", "tpahere");
                    this.message.send("help", "tptoggle");
                    this.message.send("help", "tpcancel");
                    this.message.send("help", "warp");
                    this.message.send("help", "page", new String[] { "1", "3" });
                } else if (args[2].equals("2")) {
                    this.message.send("help", "teleport");
                    this.message.send("help", "warps");
                    this.message.send("help", "setwarp");
                    this.message.send("help", "delwarp");
                    this.message.send("help", "home");
                    this.message.send("help", "homes");
                    this.message.send("help", "sethome");
                    this.message.send("help", "delhome");
                    this.message.send("help", "back");
                    this.message.send("help", "page", new String[] { "2", "3" });
                } else if (args[2].equals("3")) {
                    this.message.send("help", "teleport");
                    this.message.send("help", "setspawn");
                    this.message.send("help", "spawn");
                    this.message.send("help", "world");
                    this.message.send("help", "tpr");
                    this.blank(4);
                    this.message.send("help", "page", new String[] { "3", "3" });
                }
            } else if ("stats".equalsIgnoreCase(args[1])) {
                this.message.send("help", "stats");
                this.message.send("help", "feed");
                this.message.send("help", "heal");
                this.message.send("help", "repair");
                this.blank(5);
                this.message.send("help", "page", new String[] { "1", "1" });
            } else if ("economy".equalsIgnoreCase(args[1])) {
                this.message.send("help", "economy");
                this.message.send("help", "pay");
                this.message.send("help", "bal");
                this.blank(6);
                this.message.send("help", "page", new String[] { "1", "1" });
            } else if ("admin".equalsIgnoreCase(args[1])) {
                this.message.send("help", "admin");
                this.message.send("help", "info");
                this.message.send("help", "seen");
                this.message.send("help", "reload");
                this.blank(5);
                this.message.send("help", "page", new String[] { "1", "1" });
            } else if ("chat".equalsIgnoreCase(args[1])) {
                this.message.send("help", "chat");
                this.message.send("help", "broadcast");
                this.message.send("help", "msg");
                this.message.send("help", "reply");
                this.blank(5);
                this.message.send("help", "page", new String[] { "1", "1" });
            } else if ("misc".equalsIgnoreCase(args[1])) {
                this.message.send("help", "misc");
                this.message.send("help", "rules");
                this.blank(7);
                this.message.send("help", "page", new String[] { "1", "1" });
            } else if ("team".equalsIgnoreCase(args[1])) {
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
                    this.message.send("help", "team");
                    this.message.send("help", "teamdisband");
                    this.blank(7);
                    this.message.send("help", "page", new String[] { "2", "2" });
                }
            } else if ("2".equalsIgnoreCase(args[1])) {
                this.message.send("help", "help");
                this.message.send("help", "helpmisc");
                this.blank(7);
                this.message.send("help", "page", new String[] { "2", "2" });
            } else {
                this.message.send("help", "nochapter");
            }
        } else {
            this.message.send("help", "help");
            this.message.send("help", "helpchat");
            this.message.send("help", "helpinventory");
            this.message.send("help", "helpgamemode");
            this.message.send("help", "helpteleport");
            this.message.send("help", "helpstats");
            this.message.send("help", "helpeconomy");
            this.message.send("help", "helpteam");
            this.message.send("help", "helpadmin");
            this.message.send("help", "page", new String[] { "1", "2" });
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
