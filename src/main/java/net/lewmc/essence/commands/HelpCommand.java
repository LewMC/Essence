package net.lewmc.essence.commands;

import net.lewmc.essence.utils.MessageUtil;

public class HelpCommand {
    private final MessageUtil message;
    private final String[] args;

    public HelpCommand(MessageUtil message, String[] args) {
        this.message = message;
        this.args = args;
    }

    public boolean runHelpCommand() {
        if (args.length > 1) {
            if ("inventory".equalsIgnoreCase(args[1])) {
                if (args.length < 3 || args[2].equals("1")) {
                    this.message.PrivateMessage("--------- Essence Help - Inventory ---------", false);
                    this.message.PrivateMessage("/anvil - Open an anvil.", false);
                    this.message.PrivateMessage("/cartography - Open a cartography table.", false);
                    this.message.PrivateMessage("/craft - Open a crafting table.", false);
                    this.message.PrivateMessage("/grindstone - Open a grindstone.", false);
                    this.message.PrivateMessage("/loom - Open a loom.", false);
                    this.message.PrivateMessage("/smithing - Open a smithing table.", false);
                    this.message.PrivateMessage("/stonecutter - Open a stonecutter.", false);
                    this.message.PrivateMessage("/echest - Open your ender chest.", false);
                    this.message.PrivateMessage("----------------- Page 1/2 -----------------", false);
                } else if (args[2].equals("2")) {
                    this.message.PrivateMessage("--------- Essence Help - Inventory ---------", false);
                    this.message.PrivateMessage("/trash - Open a disposal menu.", false);
                    this.blank(7);
                    this.message.PrivateMessage("----------------- Page 2/2 -----------------", false);
                }
            } else if ("gamemode".equalsIgnoreCase(args[1])) {
                this.message.PrivateMessage("---------- Essence Help - Gamemode ---------", false);
                this.message.PrivateMessage("/gmc - Switch to creative mode.", false);
                this.message.PrivateMessage("/gms - Switch to survival mode.", false);
                this.message.PrivateMessage("/gma - Switch to adventure mode.", false);
                this.message.PrivateMessage("/gmsp - Switch to spectator mode.", false);
                this.blank(4);
                this.message.PrivateMessage("----------------- Page 1/1 -----------------", false);
            } else if ("teleport".equalsIgnoreCase(args[1])) {
                if (args.length < 3 || args[2].equals("1")) {
                    this.message.PrivateMessage("---------- Essence Help - Teleport ---------", false);
                    this.message.PrivateMessage("/tp <name/coordomate> - Teleport.", false);
                    this.message.PrivateMessage("/warp <name> - Go to a warp.", false);
                    this.message.PrivateMessage("/warps - View warp list.", false);
                    this.message.PrivateMessage("/setwarp <name> - Create a warp where you are.", false);
                    this.message.PrivateMessage("/delwarp <name> - Delete a warp.", false);
                    this.message.PrivateMessage("/home [name] - Go home.", false);
                    this.message.PrivateMessage("/homes - View home list.", false);
                    this.message.PrivateMessage("/sethome [name] - Create a home where you are.", false);
                    this.message.PrivateMessage("----------------- Page 1/2 -----------------", false);
                } else if (args[2].equals("2")) {
                    this.message.PrivateMessage("--------- Essence Help - Inventory ---------", false);
                    this.message.PrivateMessage("/delhome [name] - Delete a home.", false);
                    this.message.PrivateMessage("/back - Go back to your last location.", false);
                    this.message.PrivateMessage("/tpr - Teleport to a random location.", false);
                    this.blank(5);
                    this.message.PrivateMessage("----------------- Page 2/2 -----------------", false);
                }
            } else if ("stats".equalsIgnoreCase(args[1])) {
                this.message.PrivateMessage("----------- Essence Help - Stats -----------", false);
                this.message.PrivateMessage("/feed [user] - Feed yourself or another.", false);
                this.message.PrivateMessage("/heal [user] - Feed yourself or another.", false);
                this.blank(6);
                this.message.PrivateMessage("----------------- Page 1/1 -----------------", false);
            } else if ("economy".equalsIgnoreCase(args[1])) {
                this.message.PrivateMessage("---------- Essence Help - Economy ----------", false);
                this.message.PrivateMessage("/pay <user> <amount> - Pay someone!", false);
                this.message.PrivateMessage("/bal - View your balance.", false);
                this.blank(6);
                this.message.PrivateMessage("----------------- Page 1/1 -----------------", false);
            }
            else {
                this.message.PrivateMessage("Unable to find help chapter.", true);
            }
        } else {
            this.message.PrivateMessage("--------------- Essence Help ---------------", false);
            this.message.PrivateMessage("/es help inventory - Inventory commands.", false);
            this.message.PrivateMessage("/es help gamemode - Gamemode commands.", false);
            this.message.PrivateMessage("/es help teleport - Teleport commands.", false);
            this.message.PrivateMessage("/es help stats - Manage your stats.", false);
            this.message.PrivateMessage("/es help economy - Economy commands.", false);
            this.blank(3);
            this.message.PrivateMessage("----------------- Page 1/1 -----------------", false);
        }
        return true;
    }

    private void blank(int number) {
        for (int i = 0; i < number; i++) {
            this.message.PrivateMessage("", false);
        }
    }
}
