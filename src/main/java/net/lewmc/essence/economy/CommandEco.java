package net.lewmc.essence.economy;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import net.lewmc.essence.core.UtilPlayer;
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
                return handleSetCommand(args, message);
            case "give":
                return handleGiveCommand(args, message);
            case "take":
                return handleTakeCommand(args, message);
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
     * @param args Command arguments
     * @param message UtilMessage instance
     * @return boolean success
     */
    private boolean handleSetCommand(String[] args, UtilMessage message) {
        if (args.length != 3) {
            message.send("economy", "ecosetusage");
            return true;
        }
        
        Double amount = validateAmount(args[2], message, true);
        if (amount == null) {
            return true;
        }
        
        Player targetPlayer = findPlayer(args[1], message);
        if (targetPlayer == null) {
            return true;
        }

        if (new UtilPlayer(this.plugin).setPlayer(targetPlayer.getUniqueId(), UtilPlayer.KEYS.ECONOMY_BALANCE, amount)) {
            String symbol = this.plugin.config.get("economy.symbol").toString();
            message.send("economy", "ecoset", new String[]{targetPlayer.getName(), symbol + amount});

            return true;
        } else {
            message.send("generic", "exception");
            this.plugin.log.warn("Unable to update player balance: player "+targetPlayer.getName()+" may be null");
            return false;
        }
    }
    
    /**
     * Handle the 'give' subcommand
     * @param args Command arguments
     * @param message UtilMessage instance
     * @return boolean success
     */
    private boolean handleGiveCommand(String[] args, UtilMessage message) {
        if (args.length != 3) {
            message.send("economy", "ecogiveusage");
            return true;
        }
        
        Double amount = validateAmount(args[2], message, false);
        if (amount == null) {
            return true;
        }
        
        Player targetPlayer = findPlayer(args[1], message);
        if (targetPlayer == null) {
            return true;
        }

        UtilPlayer up = new UtilPlayer(this.plugin);
        if (up.setPlayer(targetPlayer.getUniqueId(), UtilPlayer.KEYS.ECONOMY_BALANCE, (Double) up.getPlayer(targetPlayer.getUniqueId(), UtilPlayer.KEYS.ECONOMY_BALANCE) + amount)) {
            String symbol = this.plugin.config.get("economy.symbol").toString();
            message.send("economy", "ecogive", new String[]{symbol + amount, targetPlayer.getName()});

            return true;
        } else {
            message.send("generic", "exception");
            this.plugin.log.warn("Unable to update player balance: player "+targetPlayer.getName()+" may be null");
            return false;
        }
    }
    
    /**
     * Handle the 'take' subcommand
     * @param args Command arguments
     * @param message UtilMessage instance
     * @return boolean success
     */
    private boolean handleTakeCommand(String[] args, UtilMessage message) {
        if (args.length != 3) {
            message.send("economy", "ecotakeusage");
            return true;
        }
        
        Double amount = validateAmount(args[2], message, false);
        if (amount == null) {
            return true;
        }
        
        Player targetPlayer = findPlayer(args[1], message);
        if (targetPlayer == null) {
            return true;
        }

        UtilPlayer up = new UtilPlayer(this.plugin);
        
        if ((Double) up.getPlayer(targetPlayer.getUniqueId(), UtilPlayer.KEYS.ECONOMY_BALANCE) < amount) {
            message.send("economy", "insufficientfunds");
            return true;
        }

        if (up.setPlayer(targetPlayer.getUniqueId(), UtilPlayer.KEYS.ECONOMY_BALANCE, (Double) up.getPlayer(targetPlayer.getUniqueId(), UtilPlayer.KEYS.ECONOMY_BALANCE) - amount)) {
            String symbol = this.plugin.config.get("economy.symbol").toString();
            message.send("economy", "ecotake", new String[]{symbol + amount, targetPlayer.getName()});

            return true;
        } else {
            message.send("generic", "exception");
            this.plugin.log.warn("Unable to update player balance: player "+targetPlayer.getName()+" may be null");
            return false;
        }
    }
    
    /**
     * Validate and parse amount from string
     * @param amountStr String representation of amount
     * @param message UtilMessage instance for error reporting
     * @param allowZero Whether zero is allowed
     * @return Double amount if valid, null if invalid
     */
    private Double validateAmount(String amountStr, UtilMessage message, boolean allowZero) {
        double amount;
        
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            message.send("economy", "invalidamount");
            return null;
        }
        
        if (allowZero && amount < 0) {
            message.send("economy", "negativeamount");
            return null;
        } else if (!allowZero && amount <= 0) {
            message.send("economy", "positiveamount");
            return null;
        }
        
        return amount;
    }
    
    /**
     * Find and validate player
     * @param playerName Player name to find
     * @param message UtilMessage instance for error reporting
     * @return Player if found, null if not found
     */
    private Player findPlayer(String playerName, UtilMessage message) {
        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null) {
            message.send("generic", "playernotfound");
            return null;
        }
        return targetPlayer;
    }
    
    /**
     * Show usage information for the eco command
     * @param message UtilMessage instance
     */
    private void showUsage(UtilMessage message) {
        message.send("economy", "ecousage");
    }
}