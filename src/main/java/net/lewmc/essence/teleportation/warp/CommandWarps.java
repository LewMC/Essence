package net.lewmc.essence.teleportation.warp;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Objects;
import java.util.Set;

public class CommandWarps extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the WarpsCommand class.
     * @param plugin References to the main plugin class.
     */
    public CommandWarps(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.warp.list";
    }

    /**
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("warps")) { return cmd.disabled(); }

        UtilMessage msg = new UtilMessage(this.plugin, cs);

        Files data = new Files(this.plugin.config, this.plugin);
        data.load("/data/warps.yml");

        Set<String> keys = data.getKeys("warps", false);

        if (keys == null || Objects.equals(keys.toString(), "[]")) {
            data.close();
            msg.send("warp", "noneset");
            return true;
        }

        StringBuilder warps = new StringBuilder();
        int i = 0;

        for (String key : keys) {
            if (i == 0) {
                warps.append(key);
            } else {
                warps.append(", ").append(key);
            }
            i++;
        }
        data.close();
        msg.send("warp", "list", new String[] { warps.toString() });
        return true;
    }
}