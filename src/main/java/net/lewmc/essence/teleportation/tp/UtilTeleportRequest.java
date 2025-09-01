package net.lewmc.essence.teleportation.tp;

import net.lewmc.essence.Essence;
import net.lewmc.essence.core.UtilMessage;
import org.bukkit.entity.Player;

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

        // 使用Iterator来安全地遍历和删除元素，避免ConcurrentModificationException
        java.util.Iterator<Map.Entry<String, String[]>> iterator = this.plugin.teleportRequests.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String[]> entry = iterator.next();
            String[] values = entry.getValue();

            if (values.length > 0 && values[0].equalsIgnoreCase(requester)) {
                iterator.remove(); // 使用Iterator的remove方法安全删除
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

        String requesterName = tpaRequest[0];
        Player requesterPlayer = this.plugin.getServer().getPlayer(requesterName);
        
        UtilTeleport tpu = new UtilTeleport(this.plugin);
        if (Objects.equals(tpaRequest[1], "true")) {
            tpu.doTeleport(
                    this.plugin.getServer().getPlayer(requested),
                    this.plugin.getServer().getPlayer(requesterName).getLocation(),
                    0
            );
        } else {
            tpu.doTeleport(
                    this.plugin.getServer().getPlayer(requesterName),
                    this.plugin.getServer().getPlayer(requested).getLocation(),
                    0
            );
        }

        // 通知请求发起者请求已被接受
        if (requesterPlayer != null) {
            new UtilMessage(this.plugin, requesterPlayer).send("teleport", "requestaccepted", new String[]{requested});
        }

        this.deleteFromRequested(requested);
        return true;
    }
}
