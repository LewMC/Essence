package net.lewmc.essence.utils;

import java.util.UUID;

public class HomeUtil {
    public String HomeName(UUID player, String home) {
        return player + "-" + home;
    }
}
