package net.lewmc.essence.teleportation;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilCommand;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.teleportation.tp.UtilTeleport;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CommandBack extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the BackCommand class.
     *
     * @param plugin References to the main plugin class.
     */
    public CommandBack(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission.
     *
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.teleport.back";
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
        UtilCommand cmd = new UtilCommand(this.plugin, cs);
        if (cmd.isDisabled("back")) { return cmd.disabled(); }

        Player p =  (Player) cs;

        Files playerData = new Files(this.plugin.config, this.plugin);
        playerData.load(playerData.playerDataFile(p));

        if (playerData.get("last-location") == null) {
            new UtilMessage(this.plugin, cs).send("back", "cant");
            return true;
        }

        new UtilTeleport(this.plugin).doTeleport(
                p,
                Bukkit.getServer().getWorld(Objects.requireNonNull(playerData.getString("last-location.world"))),
                playerData.getDouble("last-location.X"),
                playerData.getDouble("last-location.Y"),
                playerData.getDouble("last-location.Z"),
                (float) playerData.getDouble("last-location.yaw"),
                (float) playerData.getDouble("last-location.pitch"),
                0
        );

        playerData.close();

        new UtilMessage(this.plugin, cs).send("back", "going");
        return true;
    }
}