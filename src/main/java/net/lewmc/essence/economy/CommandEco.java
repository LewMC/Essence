package net.lewmc.essence.economy;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.foundry.Files;
import net.lewmc.foundry.command.FoundryCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /eco command - Economy management command series
 */
public class CommandEco extends FoundryCommand {
    private final Essence plugin;

    /**
     * Constructor for the CommandEco class.
     * @param plugin References to the main plugin class.
     */
    public CommandEco(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * The permission required to run the command.
     * @return String - The permission string.
     */
    @Override
    protected String requiredPermission() {
        return "essence.economy.admin";
    }

    /**
     * /eco command handler.
     * @param cs        Information about who sent the command - player or console.
     * @param command   Information about what command was sent.
     * @param s         Command label - not used here.
     * @param args      The command's arguments.
     * @return boolean true/false - was the command accepted and processed or not?
     */
    @Override
    protected boolean onRun(CommandSender cs, Command command, String s, String[] args) {
        UtilMessage message = new UtilMessage(this.plugin, cs);
        
        if (args.length == 0) {
            // Show usage information
            showUsage(message);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "set":
                return handleSetCommand(cs, args, message);
            case "give":
                return handleGiveCommand(cs, args, message);
            case "take":
                return handleTakeCommand(cs, args, message);
            case "help":
                showUsage(message);
                return true;
            default:
                message.send("generic", "invalidcommand");
                showUsage(message);
                return true;
        }
    }
    
    /**
     * Handle the 'set' subcommand
     * @param cs CommandSender
     * @param args Command arguments
     * @param message UtilMessage instance
     * @return boolean success
     */
    private boolean handleSetCommand(CommandSender cs, String[] args, UtilMessage message) {
        if (args.length != 3) {
            message.send("economy", "ecosetusage");
            return true;
        }
        
        String playerName = args[1];
        double amount;
        
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            message.send("economy", "invalidamount");
            return true;
        }
        
        if (amount < 0) {
            message.send("economy", "negativeamount");
            return true;
        }
        
        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null) {
            message.send("generic", "playernotfound");
            return true;
        }
        
        Files playerDataFile = new Files(this.plugin.foundryConfig, this.plugin);
        playerDataFile.load(playerDataFile.playerDataFile(targetPlayer));
        playerDataFile.set("economy.balance", amount);
        playerDataFile.save();
        
        String symbol = this.plugin.config.get("economy.symbol").toString();
        message.send("economy", "ecoset", new String[]{targetPlayer.getName(), symbol + amount});
        
        return true;
    }
    
    /**
     * Handle the 'give' subcommand
     * @param cs CommandSender
     * @param args Command arguments
     * @param message UtilMessage instance
     * @return boolean success
     */
    private boolean handleGiveCommand(CommandSender cs, String[] args, UtilMessage message) {
        if (args.length != 3) {
            message.send("economy", "ecogiveusage");
            return true;
        }
        
        String playerName = args[1];
        double amount;
        
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            message.send("economy", "invalidamount");
            return true;
        }
        
        if (amount <= 0) {
            message.send("economy", "positiveamount");
            return true;
        }
        
        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null) {
            message.send("generic", "playernotfound");
            return true;
        }
        
        Files playerDataFile = new Files(this.plugin.foundryConfig, this.plugin);
        playerDataFile.load(playerDataFile.playerDataFile(targetPlayer));
        double currentBalance = playerDataFile.getDouble("economy.balance");
        playerDataFile.set("economy.balance", currentBalance + amount);
        playerDataFile.save();
        
        String symbol = this.plugin.config.get("economy.symbol").toString();
        message.send("economy", "ecogive", new String[]{symbol + amount, targetPlayer.getName()});
        
        return true;
    }
    
    /**
     * Handle the 'take' subcommand
     * @param cs CommandSender
     * @param args Command arguments
     * @param message UtilMessage instance
     * @return boolean success
     */
    private boolean handleTakeCommand(CommandSender cs, String[] args, UtilMessage message) {
        if (args.length != 3) {
            message.send("economy", "ecotakeusage");
            return true;
        }
        
        String playerName = args[1];
        double amount;
        
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            message.send("economy", "invalidamount");
            return true;
        }
        
        if (amount <= 0) {
            message.send("economy", "positiveamount");
            return true;
        }
        
        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null) {
            message.send("generic", "playernotfound");
            return true;
        }
        
        Files playerDataFile = new Files(this.plugin.foundryConfig, this.plugin);
        playerDataFile.load(playerDataFile.playerDataFile(targetPlayer));
        double currentBalance = playerDataFile.getDouble("economy.balance");
        
        // Check if the player has sufficient balance
        if (currentBalance < amount) {
            message.send("economy", "insufficientfunds");
            return true;
        }
        
        double newBalance = currentBalance - amount;
        playerDataFile.set("economy.balance", newBalance);
        playerDataFile.save();
        
        String symbol = this.plugin.config.get("economy.symbol").toString();
        message.send("economy", "ecotake", new String[]{symbol + amount, targetPlayer.getName()});
        
        return true;
    }
    
    /**
     * Show usage information for the eco command
     * @param message UtilMessage instance
     */
    private void showUsage(UtilMessage message) {
        message.send("economy", "ecousage");
    }
}