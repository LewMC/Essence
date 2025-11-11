package net.lewmc.essence.inventory;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.command.FoundryPlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * /skull command.
 */
public class CommandSkull extends FoundryPlayerCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandSkull class.
     */
    public CommandSkull(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The required permission
     * @return String - the permission string
     */
    @Override
    protected String requiredPermission() {
        return "essence.inventory.skull";
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
        UtilMessage msg = new UtilMessage(this.plugin, cs);

        if (!(cs instanceof Player player)) {
            msg.send("generic","playernotfound");
            return true;
        }

        if (args.length < 1) {
            msg.send("skull","usage");
            return true;
        }

        String targetName = args[0];
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (meta != null) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(targetName));
            meta.setDisplayName(ChatColor.GOLD + targetName + "'s Head");
            skull.setItemMeta(meta);
        }

        player.getInventory().addItem(skull);
        msg.send("skull","given", new String[]{targetName});
        return true;
    }
}