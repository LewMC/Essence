package net.lewmc.essence.utils.economy;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.FileUtil;
import net.lewmc.essence.utils.LogUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Economy {
    private final Essence plugin;
    private Player p;
    private FileUtil file;
    private boolean isConsole = false;

    public Economy(Essence plugin, CommandSender cs) {
        this.plugin = plugin;
        if (cs instanceof Player p) {
            this.p = p;
            this.file = new FileUtil(plugin);
        } else {
            this.isConsole = true;
        }
    }

    private void openFile() {
        if (!isConsole) {
            this.file.load(this.file.playerDataFile(this.p.getUniqueId()));
        }
    }

    public double balance() {
        if (!this.isConsole) {
            this.openFile();
            double bal = this.file.getDouble("economy.balance");
            this.file.close();
            return bal;
        } else {
            return -1;
        }
    }

    public void balanceSet(double balance) {
        if (!this.isConsole) {
            this.openFile();
            this.file.set("economy.balance", balance);
            this.file.save();
            this.file.close();
        }
    }

    public void balanceAdd(double add) {
        if (!this.isConsole) {
            this.openFile();
            this.file.set("economy.balance", this.file.getDouble("economy.balance") + add);
            this.file.save();
            this.file.close();
        }
    }

    public void balanceSubtract(double add) {
        if (!this.isConsole) {
            this.openFile();
            this.file.set("economy.balance", this.file.getDouble("economy.balance") + add);
            this.file.save();
            this.file.close();
        }
    }
}
