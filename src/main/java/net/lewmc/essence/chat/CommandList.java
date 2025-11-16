package net.lewmc.essence.chat;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.Permissions;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.List;

public class CommandList extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandList class.
     * @param plugin References to the main plugin class.
     */
    public CommandList(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.chat.list";
    }

    /**
     * /nick command handler.
     * @param cs Information about who sent the command - player or console.
     * @param command Information about what command was sent.
     * @param s Command label - not used here.
     * @param args The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage msg = new UtilMessage(this.plugin, cs);

        Collection<? extends Player> players = this.plugin.getServer().getOnlinePlayers();

        List<String> visiblePlayerNames;

        if (new Permissions(cs).has("essence.chat.list.invisible")) {
            visiblePlayerNames = players.stream()
                    .filter(player -> !player.hasPotionEffect(PotionEffectType.INVISIBILITY))
                    .map(Player::getName)
                    .toList();
        } else {
            visiblePlayerNames = players.stream()
                    .map(Player::getName)
                    .toList();
        }

        msg.send("list", "online", new String[]{ String.valueOf(this.plugin.getServer().getOnlinePlayers().size()), String.valueOf(this.plugin.getServer().getMaxPlayers())});
        msg.send("list", "list", new String[]{ String.join(", ", visiblePlayerNames) });

        return true;
    }
}