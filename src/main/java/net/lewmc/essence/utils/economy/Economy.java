package net.lewmc.essence.utils.economy;

import net.lewmc.essence.Essence;
import net.lewmc.essence.utils.FileUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Economy {
    private FileUtil file;
    private boolean isConsole = false;

    public Economy(Essence plugin, CommandSender cs) {
        if (cs instanceof Player player) {
            this.file = new FileUtil(plugin);
            this.file.load(this.file.playerDataFile(player.getUniqueId()));
        } else {
            this.isConsole = true;
        }
    }

    public double balance() {
        if (!this.isConsole) {
            double balance = this.file.getDouble("economy.balance");
            this.file.close();
            return balance;
        } else {
            return -1;
        }
    }

    public void balanceSet(double balance) {
        if (!this.isConsole) {
            this.file.set("economy.balance", balance);
            this.file.close();
        }
    }

    public void balanceAdd(double add) {
        if (!this.isConsole) {
            this.file.set("economy.balance", this.file.getDouble("economy.balance") + add);
            this.file.close();
        }
    }

    public void balanceSubtract(double add) {
        if (!this.isConsole) {
            this.file.set("economy.balance", this.file.getDouble("economy.balance") + add);
            this.file.close();
        }
    }
}
