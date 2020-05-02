package com.playares.hub.queue;

import com.playares.commons.services.serversync.data.SyncedServer;
import com.playares.hub.queue.data.ServerQueue;
import com.playares.hub.queue.menu.SelectorMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
public final class QueueHandler {
    @Getter public final QueueManager manager;

    public void openSelectorMenu(Player player) {
        final SelectorMenu menu = new SelectorMenu(manager.getPlugin(), player);
        menu.open();
    }

    public void processQueues() {
        for (SyncedServer server : manager.getQueueSnapshot().keySet()) {
            final ServerQueue queue = getManager().getServerQueues().get(server.getServerId());

            if (queue.getQueue().isEmpty()) {
                continue;
            }

            final ServerQueue.QueuedPlayer processed = queue.getSortedQueue().get(0);
            final Player player = Bukkit.getPlayer(processed.getUniqueId());

            if (player == null) {
                queue.remove(processed.getUniqueId());
                continue;
            }

            if (server.status.equals(SyncedServer.ServerStatus.OFFLINE)) {
                continue;
            }

            if (server.status.equals(SyncedServer.ServerStatus.WHITELISTED) && !player.hasPermission("hub.queue.bypasswhitelist")) {
                continue;
            }

            if (server.getPlayerList().size() >= server.getMaxPlayers()) {
                continue;
            }

            if (server.isPremiumRequired() && (!player.hasPermission("hub.premium") && !player.hasPermission("hub.staff"))) {
                continue;
            }

            queue.remove(processed.getUniqueId());
            server.send(player);
        }
    }
}