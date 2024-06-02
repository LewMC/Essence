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
                    this.message.PrivateMessage("help", "inventory");
                    this.message.PrivateMessage("help", "anvil");
                    this.message.PrivateMessage("help", "cartography");
                    this.message.PrivateMessage("help", "craft");
                    this.message.PrivateMessage("help", "grindstone");
                    this.message.PrivateMessage("help", "loom");
                    this.message.PrivateMessage("help", "smithing");
                    this.message.PrivateMessage("help", "stonecutter");
                    this.message.PrivateMessage("help", "echest");
                    this.message.PrivateMessage("help", "page", "1", "2");
                } else if (args[2].equals("2")) {
                    this.message.PrivateMessage("help", "inventory");
                    this.message.PrivateMessage("help", "trash");
                    this.blank(7);
                    this.message.PrivateMessage("help", "page", "2", "2");
                }
            } else if ("gamemode".equalsIgnoreCase(args[1])) {
                this.message.PrivateMessage("help", "gamemode");
                this.message.PrivateMessage("help", "gmc");
                this.message.PrivateMessage("help", "gms");
                this.message.PrivateMessage("help", "gma");
                this.message.PrivateMessage("help", "gmsp");
                this.blank(4);
                this.message.PrivateMessage("help", "page", "1", "1");
            } else if ("teleport".equalsIgnoreCase(args[1])) {
                if (args.length < 3 || args[2].equals("1")) {
                    this.message.PrivateMessage("help", "teleport");
                    this.message.PrivateMessage("help", "tp");
                    this.message.PrivateMessage("help", "tpa");
                    this.message.PrivateMessage("help", "tpaccept");
                    this.message.PrivateMessage("help", "tpdeny");
                    this.message.PrivateMessage("help", "tpahere");
                    this.message.PrivateMessage("help", "tptoggle");
                    this.message.PrivateMessage("help", "tpcancel");
                    this.message.PrivateMessage("help", "warp");
                    this.message.PrivateMessage("help", "page", "1", "3");
                } else if (args[2].equals("2")) {
                    this.message.PrivateMessage("help", "teleport");
                    this.message.PrivateMessage("help", "warps");
                    this.message.PrivateMessage("help", "setwarp");
                    this.message.PrivateMessage("help", "delwarp");
                    this.message.PrivateMessage("help", "home");
                    this.message.PrivateMessage("help", "homes");
                    this.message.PrivateMessage("help", "sethome");
                    this.message.PrivateMessage("help", "delhome");
                    this.message.PrivateMessage("help", "back");
                    this.message.PrivateMessage("help", "page", "2", "3");
                } else if (args[2].equals("3")) {
                    this.message.PrivateMessage("help", "teleport");
                    this.message.PrivateMessage("help", "setspawn");
                    this.message.PrivateMessage("help", "spawn");
                    this.message.PrivateMessage("help", "world");
                    this.blank(5);
                    this.message.PrivateMessage("help", "page", "3", "3");
                }
            } else if ("stats".equalsIgnoreCase(args[1])) {
                this.message.PrivateMessage("help", "stats");
                this.message.PrivateMessage("help", "feed");
                this.message.PrivateMessage("help", "heal");
                this.message.PrivateMessage("help", "repair");
                this.blank(5);
                this.message.PrivateMessage("help", "page", "1", "1");
            } else if ("economy".equalsIgnoreCase(args[1])) {
                this.message.PrivateMessage("help", "economy");
                this.message.PrivateMessage("help", "pay");
                this.message.PrivateMessage("help", "bal");
                this.blank(6);
                this.message.PrivateMessage("help", "page", "1", "1");
            } else if ("team".equalsIgnoreCase(args[1])) {
                if (args.length < 3 || args[2].equals("1")) {
                    this.message.PrivateMessage("help", "team");
                    this.message.PrivateMessage("help", "teamcreate");
                    this.message.PrivateMessage("help", "teamjoin");
                    this.message.PrivateMessage("help", "teamleave");
                    this.message.PrivateMessage("help", "teamrequests");
                    this.message.PrivateMessage("help", "teamaccept");
                    this.message.PrivateMessage("help", "teamdecline");
                    this.message.PrivateMessage("help", "teamtransfer");
                    this.message.PrivateMessage("help", "teamkick");
                    this.message.PrivateMessage("help", "page", "1", "2");
                } else if (args[2].equals("2")) {
                    this.message.PrivateMessage("help", "team");
                    this.message.PrivateMessage("help", "teamdisband");
                    this.blank(7);
                    this.message.PrivateMessage("help", "page", "2", "2");
                }
            }
            else {
                this.message.PrivateMessage("help", "nochapter");
            }
        } else {
            this.message.PrivateMessage("help", "help");
            this.message.PrivateMessage("help", "helpinventory");
            this.message.PrivateMessage("help", "helpgamemode");
            this.message.PrivateMessage("help", "helpteleport");
            this.message.PrivateMessage("help", "helpstats");
            this.message.PrivateMessage("help", "helpeconomy");
            this.message.PrivateMessage("help", "helpteam");
            this.blank(2);
            this.message.PrivateMessage("help", "page", "1", "1");
        }
        return true;
    }

    /**
     * Sends a set number of blank lines to the command executor.
     * @param number int - Number of blank lines to send.
     */
    private void blank(int number) {
        for (int i = 0; i < number; i++) {
            this.message.PrivateMessage("generic", "blankmessage");
        }
    }
}
