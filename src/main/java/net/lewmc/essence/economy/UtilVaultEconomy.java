package net.lewmc.essence.economy;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilPlayer;
import net.lewmc.foundry.Files;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Collections;
import java.util.List;

/**
 * An implementation of the Vault economy interface.
 */
public class UtilVaultEconomy implements Economy {
    private final Essence plugin;

    /**
     * Constructor for the VaultEconomy class.
     * @param plugin Essence - Reference to the main plugin class.
     */
    public UtilVaultEconomy(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks if the economy is enabled.
     * @return true - If it's here it's enabled!
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Returns Essence's economy name.
     * @return String - The economy name.
     */
    @Override
    public String getName() {
        return "Essence Economy";
    }

    /**
     * If Essence supports banks.
     * @return false - It doesn't
     */
    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    /**
     * Formats the amount into an Essence economy string.
     * @param v Double - The amount to format.
     * @return String - A formatted string.
     */
    @Override
    public String format(double v) {
        return this.plugin.getConfig().getString("economy.symbol") + " " + v;
    }

    /**
     * Gets the plural currency name.
     * @return String - The plural currency name.
     */
    @Override
    public String currencyNamePlural() {
        return this.currencyNameSingular();
    }

    /**
     * Gets the singular currency name.
     * @return String - The singular currency name.
     */
    @Override
    public String currencyNameSingular() {
        return this.plugin.getConfig().getString("economy.symbol");
    }

    /**
     * Checks if a player has an account.
     * @param s String - The player name.
     * @return boolean - If the player has an account.
     */
    @Override
    public boolean hasAccount(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return this.hasAccount(player);
    }

    /**
     * Checks if a player has an account.
     * @param offlinePlayer String - The player (type of offlinePlayer).
     * @return boolean - If the player has an account.
     */
    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        Files playerData = new Files(this.plugin.config, this.plugin);
        return playerData.exists(playerData.playerDataFile(offlinePlayer.getUniqueId()));
    }

    /**
     * Checks if a player has an account.
     * @param s String - The player name.
     * @return boolean - If the player has an account.
     */
    @Override
    public boolean hasAccount(String s, String s1) {
        return this.hasAccount(s);
    }

    /**
     * Checks if a player has an account.
     * @param offlinePlayer String - The player (type of offlinePlayer).
     * @return boolean - If the player has an account.
     */
    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return this.hasAccount(offlinePlayer);
    }

    /**
     * Gets the balance of the player.
     * @param s String - The player to check.
     * @return double - The player's balance.
     */
    @Override
    public double getBalance(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return this.getBalance(player);
    }

    /**
     * Gets the balance of the player.
     * @param offlinePlayer OfflinePlayer - The player to check.
     * @return double - The player's balance.
     */
    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        Files fileUtil = new Files(this.plugin.config, this.plugin);

        fileUtil.load(fileUtil.playerDataFile(offlinePlayer.getUniqueId()));
        double bal = fileUtil.getDouble("economy.balance");
        fileUtil.close();
        return bal;
    }

    /**
     * Gets the balance of the player.
     * @param s String - The player to check.
     * @param s1 String - Not implemented.
     * @return double - The player's balance.
     */
    @Override
    public double getBalance(String s, String s1) {
        return this.getBalance(s);
    }

    /**
     * Gets the balance of the player.
     * @param offlinePlayer OfflinePlayer - The player to check.
     * @param s String - Not implemented.
     * @return double - The player's balance.
     */
    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return this.getBalance(offlinePlayer);
    }

    /**
     * Checks if a player has enough money.
     * @param s String - The player to check.
     * @param v double - The amount to check for.
     * @return boolean - The result of the operation.
     */
    @Override
    public boolean has(String s, double v) {
        if (s == null || s.isEmpty()) {
            return false;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return this.has(player, v);
    }

    /**
     * Checks if a player has enough money.
     * @param offlinePlayer OfflinePlayer - The player to check.
     * @param v double - The amount to check for.
     * @return boolean - The result of the operation.
     */
    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        Files fileUtil = new Files(this.plugin.config, this.plugin);
        fileUtil.load(fileUtil.playerDataFile(offlinePlayer.getUniqueId()));
        double bal = fileUtil.getDouble("economy.balance");
        fileUtil.close();
        return (v >= bal);
    }

    /**
     * Checks if a player has enough money.
     * @param s String - The player to check.
     * @param s1 String - Not implemented.
     * @param v double - The amount to check for.
     * @return boolean - The result of the operation.
     */
    @Override
    public boolean has(String s, String s1, double v) {
        return this.has(s, v);
    }

    /**
     * Checks if a player has enough money.
     * @param offlinePlayer OfflinePlayer - The player to check.
     * @param s String - Not implemented.
     * @param v double - The amount to check for.
     * @return boolean - The result of the operation.
     */
    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return this.has(offlinePlayer, v);
    }

    /**
     * Withdraws money from a player's account.
     * @param s String - The player to withdraw money from.
     * @param v double - Amount
     * @return EconomyResponse - The result of the operation.
     */
    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        if (s == null || s.isEmpty()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player name cannot be empty or null.");
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return this.withdrawPlayer(player, v);
    }

    /**
     * Withdraws money from a player's account.
     * @param offlinePlayer OfflinePlayer - The player to withdraw money from.
     * @param v double - Amount
     * @return EconomyResponse - The result of the operation.
     */
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        Files fileUtil = new Files(this.plugin.config, this.plugin);
        fileUtil.load(fileUtil.playerDataFile(offlinePlayer.getUniqueId()));
        double bal = fileUtil.getDouble("economy.balance");
        if (fileUtil.set("economy.balance", bal - v)) {
            bal = fileUtil.getDouble("economy.balance");
            fileUtil.close();
            return new EconomyResponse(v, bal, EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(v, bal, EconomyResponse.ResponseType.FAILURE, "Unable to modify player data.");
        }
    }

    /**
     * Withdraws money from a player's account.
     * @param s String - The player to withdraw money from.
     * @param s1 String - Not implemented.
     * @param v double - Amount
     * @return EconomyResponse - The result of the operation.
     */
    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return this.withdrawPlayer(s, v);
    }

    /**
     * Withdraws money from a player's account.
     * @param offlinePlayer OfflinePlayer - The player to withdraw money from.
     * @param s String - Not implemented.
     * @param v double - Amount
     * @return EconomyResponse - The result of the operation.
     */
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return this.withdrawPlayer(offlinePlayer, v);
    }

    /**
     * Deposits money into a player's account.
     * @param s String - The player to deposit money to.
     * @param v double - Amount
     * @return EconomyResponse - The result of the operation.
     */
    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        if (s == null || s.isEmpty()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player name cannot be empty or null.");
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return this.depositPlayer(player, v);
    }

    /**
     * Deposits money into a player's account.
     * @param offlinePlayer OfflinePlayer - The player to deposit money to.
     * @param v double - Amount
     * @return EconomyResponse - The result of the operation.
     */
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        Files fileUtil = new Files(this.plugin.config, this.plugin);
        fileUtil.load(fileUtil.playerDataFile(offlinePlayer.getUniqueId()));
        double bal = fileUtil.getDouble("economy.balance");
        if (fileUtil.set("economy.balance", bal + v)) {
            bal = fileUtil.getDouble("economy.balance");
            fileUtil.close();
            return new EconomyResponse(v, bal, EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(v, bal, EconomyResponse.ResponseType.FAILURE, "Unable to modify player data.");
        }
    }

    /**
     * Deposits money into a player's account.
     * @param s String - The player to deposit money to.
     * @param s1 String - Not implemented.
     * @param v double - Amount
     * @return EconomyResponse - The result of the operation.
     */
    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return this.depositPlayer(s, v);
    }

    /**
     * Deposits money into a player's account.
     * @param offlinePlayer OfflinePlayer - The player to deposit money to.
     * @param s String - Not implemented.
     * @param v double - Amount
     * @return EconomyResponse - The result of the operation.
     */
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return this.depositPlayer(offlinePlayer, v);
    }

    /**
     * Creates a bank account. (Not implemented)
     * @param s String - Not implemented.
     * @param s1 String - Not implemented
     * @return EconomyResponse - Fails (not implemented)
     */
    @Override
    public EconomyResponse createBank(String s, String s1) {
        return this.bankNotImplemented();
    }

    /**
     * Creates a bank account. (Not implemented)
     * @param s String - Not implemented.
     * @param offlinePlayer OfflinePlayer - Not implemented
     * @return EconomyResponse - Fails (not implemented)
     */
    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return this.bankNotImplemented();
    }

    /**
     * Deletes a bank account. (Not implemented)
     * @param s String - Not implemented.
     * @return EconomyResponse - Fails (not implemented)
     */
    @Override
    public EconomyResponse deleteBank(String s) {
        return this.bankNotImplemented();
    }

    /**
     * Gets a bank balance. (Not implemented)
     * @param s String - Not implemented.
     * @return EconomyResponse - Fails (not implemented)
     */
    @Override
    public EconomyResponse bankBalance(String s) {
        return this.bankNotImplemented();
    }

    /**
     * Checks if a bank has the required amount of funds. (Not implemented)
     * @param s String - Not implemented.
     * @param v double - Not implemented
     * @return EconomyResponse - Fails (not implemented)
     */
    @Override
    public EconomyResponse bankHas(String s, double v) {
        return this.bankNotImplemented();
    }

    /**
     * Withdraws money from a player's bank account. (Not implemented)
     * @param s String - Not implemented.
     * @param v double - Not implemented
     * @return EconomyResponse - Fails (not implemented)
     */
    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return this.bankNotImplemented();
    }

    /**
     * Deposits money into a player's bank account. (Not implemented)
     * @param s String - Not implemented.
     * @param v double - Not implemented
     * @return EconomyResponse - Fails (not implemented)
     */
    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return this.bankNotImplemented();
    }

    /**
     * Checks if a player is a bank owner (Not implemented)
     * @param s String - Not implemented.
     * @param s1 String - Not implemented
     * @return EconomyResponse - Fails (not implemented)
     */
    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return this.bankNotImplemented();
    }

    /**
     * Checks if a player is a bank owner (Not implemented)
     * @param s String - Not implemented.
     * @param offlinePlayer OfflinePlayer - Not implemented
     * @return EconomyResponse - Fails (not implemented)
     */
    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return this.bankNotImplemented();
    }

    /**
     * Checks if a player is a bank member (Not implemented)
     * @param s String - Not implemented.
     * @param s1 String - Not implemented
     * @return EconomyResponse - Fails (not implemented)
     */
    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return this.bankNotImplemented();
    }

    /**
     * Checks if a player is a bank member (Not implemented)
     * @param s String - Not implemented.
     * @param offlinePlayer OfflinePlayer - Not implemented
     * @return EconomyResponse - Fails (not implemented)
     */
    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return this.bankNotImplemented();
    }

    /**
     * Gets a list of banks. (Not implemented)
     * @return List - Returns an empty list (Not implemented)
     */
    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    /**
     * Creates a new player account.
     * @param s String - The player to create an account for.
     * @return boolean - If the operation was successful.
     */
    @Override
    public boolean createPlayerAccount(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return this.createPlayerAccount(player);
    }

    /**
     * Creates a new player account.
     * @param offlinePlayer OfflinePlayer - The player to create an account for.
     * @return boolean - If the operation was successful.
     */
    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        UtilPlayer playerUtil = new UtilPlayer(this.plugin, offlinePlayer.getPlayer());
        return playerUtil.createPlayerData();
    }

    /**
     * Creates a new player account.
     * @param s String - The player to create an account for.
     * @param s1 String - Unused (not implemented)
     * @return boolean - If the operation was successful.
     */
    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return this.createPlayerAccount(s);
    }

    /**
     * Creates a new player account.
     * @param offlinePlayer OfflinePlayer - The player to create an account for.
     * @param s String - Unused (not implemented)
     * @return boolean - If the operation was successful.
     */
    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return this.createPlayerAccount(offlinePlayer);
    }

    /**
     * Returns a new EconomyResponse that informs the system that bank accounts are not implemented.
     * @return EconomyResponse - Preset value stating that bank accounts are not implemented.
     */
    public EconomyResponse bankNotImplemented() {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Essence does not support bank accounts, please consider using another economy provider if you require this feature.");
    }
}
