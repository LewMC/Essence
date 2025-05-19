package net.lewmc.essence.module.teleportation.tp;

import net.lewmc.essence.Essence;

import java.util.Map;
import java.util.Objects;

/**
 * The TeleportRequestUtil class.
 */
public class UtilTeleportRequest {
    private final Essence plugin;

    /**
     * Constructor for the TeleportRequestUtil class.
     * @param plugin Reference to main Essence class.
     */
    public UtilTeleportRequest(Essence plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates a teleportation request.
     * @param requester String - The user sending the request.
     * @param requested String - The user being requested.
     * @param teleportToRequester boolean - If the user being requested should be teleported to the requester (true), or if the requester should be teleported to them (false).
     */
    public void createRequest(String requester, String requested, boolean teleportToRequester) {
        this.deleteFromRequester(requester);

        if (teleportToRequester) {
            this.plugin.teleportRequests.put(requested, new String[]{requester, "true"});
        } else {
            this.plugin.teleportRequests.put(requested, new String[]{requester, "false"});
        }
    }

    /**
     * Deletes a teleport request by finding it from the requesting player.
     * @param requester String - Name of the requester.
     * @return boolean - If the request was found and deleted.
     */
    public boolean deleteFromRequester(String requester) {
        boolean found = false;
        if (this.plugin.teleportRequests == null) {
            return false;
        }

        for (Map.Entry<String, String[]> entry : this.plugin.teleportRequests.entrySet()) {
            String[] values = entry.getValue();
            String key = entry.getKey();

            if (values.length > 0 && values[0].equalsIgnoreCase(requester)) {
                this.plugin.teleportRequests.remove(key);
                found = true;
            }
        }
        return found;
    }

    /**
     * Deletes a request by finding it from the requested player.
     * @param requested String - Name of the requested player.
     */
    public void deleteFromRequested(String requested) {
        if (this.plugin.teleportRequests == null) {
            return;
        }
        this.plugin.teleportRequests.remove(requested);
    }

    /**
     * Accepts a teleportation request.
     * @param requested String - Name of the requested player.
     * @return boolean - Success
     */
    public boolean acceptRequest(String requested) {
        String[] tpaRequest = this.plugin.teleportRequests.get(requested);
        if (tpaRequest == null) {
            return false;
        }

        UtilTeleport tpu = new UtilTeleport(this.plugin);
        if (Objects.equals(tpaRequest[1], "true")) {
            tpu.doTeleport(
                    this.plugin.getServer().getPlayer(requested),
                    this.plugin.getServer().getPlayer(tpaRequest[0]).getLocation(),
                    0
            );
        } else {
            tpu.doTeleport(
                    this.plugin.getServer().getPlayer(tpaRequest[0]),
                    this.plugin.getServer().getPlayer(requested).getLocation(),
                    0
            );
        }

        this.deleteFromRequested(requested);
        return true;
    }
}
