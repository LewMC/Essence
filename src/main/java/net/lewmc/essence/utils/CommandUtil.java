package net.lewmc.essence.utils;

import net.lewmc.essence.Essence;

import java.util.List;
import java.util.Objects;

public class CommandUtil {
    private final Essence plugin;

    public CommandUtil(Essence plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled(String command) {
        List<String> disabledCommands = this.plugin.getConfig().getStringList("disabled-commands");

        for (String key : disabledCommands) {
            if (Objects.equals(key, command)) {
                return false;
            }
        }

        return true;
    }
}
