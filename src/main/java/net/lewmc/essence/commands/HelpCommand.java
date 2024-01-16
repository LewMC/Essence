package net.lewmc.essence.commands;

import net.lewmc.essence.Essence;
import net.lewmc.essence.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class HelpCommand {
    private MessageHandler message;
    private String[] args;

    public HelpCommand(MessageHandler message, String[] args) {
        this.message = message;
        this.args = args;
    }

    public boolean runHelpCommand() {
        this.message.PrivateMessage("--------------- Essence Help ---------------", false);
        if (args.length > 1) {
            if ("inventory".equalsIgnoreCase(args[1])) {
                this.message.PrivateMessage("/anvil - Open an anvil", false);
                this.message.PrivateMessage("/cartography - Open a cartography table", false);
                this.message.PrivateMessage("/craft - Open a crafting table", false);
                this.message.PrivateMessage("/grindstone - Open a grindstone", false);
                this.message.PrivateMessage("/loom - Open a loom", false);
                this.message.PrivateMessage("/smithing - Open a smithing table", false);
                this.message.PrivateMessage("/stonecutter - Open a stonecutter", false);
                this.message.PrivateMessage("/echest - Open your ender chest", false);
                this.message.PrivateMessage("----------------- Inventory ----------------", false);
            } else if ("gamemode".equalsIgnoreCase(args[1])) {
                this.message.PrivateMessage("/gmc - Switch to creative mode.", false);
                this.message.PrivateMessage("/gms - Switch to survival mode.", false);
                this.message.PrivateMessage("/gma - Switch to adventure mode.", false);
                this.message.PrivateMessage("/gmsp - Switch to spectator mode.", false);
                this.message.PrivateMessage("", false);
                this.message.PrivateMessage("", false);
                this.message.PrivateMessage("", false);
                this.message.PrivateMessage("", false);
                this.message.PrivateMessage("----------------- Gamemode -----------------", false);
            } else if ("teleport".equalsIgnoreCase(args[1])) {
                this.message.PrivateMessage("/tp <name/coordomate> - Teleport.", false);
                this.message.PrivateMessage("/warp - List warps.", true);
                this.message.PrivateMessage("/warp <name> - Teleport to a warp.", false);
                this.message.PrivateMessage("/setwarp <name> - Create a warp where you are.", false);
                this.message.PrivateMessage("/delwarp <name> - Delete a warp.", true);
                this.message.PrivateMessage("/home <name> - Teleport to a home", true);
                this.message.PrivateMessage("/sethome <name> - Create a home where you are.", true);
                this.message.PrivateMessage("/delhome <name> - Delete a home.", true);
                this.message.PrivateMessage("----------------- Teleport -----------------", false);
            } else if ("stats".equalsIgnoreCase(args[1])) {
                this.message.PrivateMessage("/feed [user] - Feed yourself or another.", false);
                this.message.PrivateMessage("/heal [user] - Feed yourself or another.", false);
                this.message.PrivateMessage("", false);
                this.message.PrivateMessage("", false);
                this.message.PrivateMessage("", false);
                this.message.PrivateMessage("", false);
                this.message.PrivateMessage("", false);
                this.message.PrivateMessage("", false);
                this.message.PrivateMessage("------------------ Stats -------------------", false);
            }
            else {
                this.message.PrivateMessage("Unable to find help chapter.", true);
                this.message.PrivateMessage("--------------- Essence Help ---------------", false);
            }
        } else {
            this.message.PrivateMessage("/es help inventory - Inventory commands.", false);
            this.message.PrivateMessage("/es help gamemode - Gamemode commands.", false);
            this.message.PrivateMessage("/es help teleport - Teleport commands.", false);
            this.message.PrivateMessage("/es help stats - Manage your stats.", false);
            this.message.PrivateMessage("", false);
            this.message.PrivateMessage("Commands that appear in red are temporarily", true);
            this.message.PrivateMessage("unavailable due to technical issues.", true);
            this.message.PrivateMessage("", false);
            this.message.PrivateMessage("--------------- Essence Help ---------------", false);
        }
        return true;
    }
}
