package net.lewmc.essence.utils;

import java.util.UUID;

public class HomeUtil {
    public String HomeWrapper(UUID player, String home) {
        return "homes." + player + "." + home;
    }
    public String HomeName(UUID player, String home) {
        return player + "." + home;
    }
}
