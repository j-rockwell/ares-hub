package com.playares.hub.queue.listener;

import com.playares.commons.logger.Logger;
import com.playares.commons.services.customitems.CustomItemService;
import com.playares.commons.services.serversync.data.SyncedServer;
import com.playares.commons.services.serversync.event.ServerSyncEvent;
import com.playares.commons.util.bukkit.Players;
import com.playares.hub.item.ServerSelectorItem;
import com.playares.hub.queue.QueueManager;
import com.playares.hub.queue.data.ServerQueue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public final class QueueListener implements Listener {
    @Getter public final QueueManager manager;

    @EventHandler
    public void onServerSync(ServerSyncEvent event) {
        for (SyncedServer updated : event.getServers()) {
            final SyncedServer server = manager.getServerQueues().keySet().stream().filter(s -> s.getServerId() == updated.getServerId() && s.getType().equals(updated.getType())).findFirst().orElse(null);

            if (server != null) {
                server.setDisplayName(updated.getDisplayName());
                server.setDescription(updated.getDescription());
                server.setPlayerList(updated.getPlayerList());
                server.setStatus(updated.getStatus());
                continue;
            }

            manager.getServerQueues().put(updated, new ServerQueue(updated));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final CustomItemService customItemService = (CustomItemService)manager.getPlugin().getService(CustomItemService.class);

        Players.resetHealth(player);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        if (customItemService == null) {
            Logger.error("Could not give " + player.getName() + " queue items because the Custom Item Service is not initialized");
            return;
        }

        customItemService.getItem(ServerSelectorItem.class).ifPresent(item -> {
            final ServerSelectorItem selector = (ServerSelectorItem)item;

            player.getInventory().setItem(4, selector.getItem());
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        for (ServerQueue queue : manager.getServerQueues().values()) {
            queue.remove(player.getUniqueId());
        }
    }
}