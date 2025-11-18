package net.lewmc.essence.core;

import java.util.ArrayList;
import java.util.List;

/**
 * The Player Type.
 * @since 1.11.0
 */
public class TypePlayer {
    public SubtypeUser user = new SubtypeUser();
    public SubtypeEconomy economy = new SubtypeEconomy();
    public SubtypeLocation lastLocation = new SubtypeLocation();
    public SubtypeLocation lastSleep = new SubtypeLocation();

    public static class SubtypeUser {
        public boolean acceptingTeleportRequests;
        public String lastSeen;
        public String lastKnownName;
        public String nickname;
        public String ipAddress;
        public List<String> ignoringPlayers = new ArrayList<>();
        public String team;
    }

    public static class SubtypeEconomy {
        public double balance;
        public boolean acceptingPayments;
    }

    public static class SubtypeLocation {
        public String world;
        public double x;
        public double y;
        public double z;
        public float yaw;
        public float pitch;
    }
}
