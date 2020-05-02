package com.playares.hub.queue;

import com.google.common.collect.Maps;
import com.playares.commons.logger.Logger;
import com.playares.commons.services.customitems.CustomItemService;
import com.playares.commons.services.serversync.data.SyncedServer;
import com.playares.commons.util.bukkit.Scheduler;
import com.playares.hub.Hub;
import com.playares.hub.item.LeaveQueueItem;
import com.playares.hub.item.ServerSelectorItem;
import com.playares.hub.queue.data.ServerQueue;
import com.playares.hub.queue.listener.QueueListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public final class QueueManager {
    @Getter public final Hub plugin;
    @Getter public final QueueHandler handler;
    @Getter public final Map<SyncedServer, ServerQueue> serverQueues;
    @Getter public BukkitTask queueProcessor;
    @Getter public BukkitTask queueNotifier;

    public QueueManager(Hub plugin) {
        this.plugin = plugin;
        this.handler = new QueueHandler(this);
        this.serverQueues = Maps.newConcurrentMap();

        this.queueProcessor = new Scheduler(plugin).sync(handler::processQueues).repeat(0L, 20L).run();

        this.queueNotifier = new Scheduler(plugin).sync(() -> serverQueues.values().forEach(queue -> queue.getQueue().forEach(queuePlayer -> {
            final Player player = Bukkit.getPlayer(queuePlayer.getUniqueId());
            player.sendMessage(ChatColor.AQUA + "You are currently " + ChatColor.YELLOW + "#" + queue.getPosition(queuePlayer.getUniqueId()) + ChatColor.AQUA + " in queue to join " + queue.getServer().getDisplayName());
        }))).repeat(20L, 10 * 20L).run();

        plugin.registerListener(new QueueListener(this));

        final CustomItemService customItemService = (CustomItemService)plugin.getService(CustomItemService.class);

        if (customItemService == null) {
            Logger.error("Failed to obtain Custom Item Service while initializing the Hub Queues");
            return;
        }

        customItemService.registerNewItem(new ServerSelectorItem(plugin));
        customItemService.registerNewItem(new LeaveQueueItem(plugin));
    }

    /**
     * Returns the current queue for the provided Bukkit Player
     * @param player Bukkit Player
     * @return ServerQueue
     */
    public ServerQueue getCurrentQueue(Player player) {
        for (ServerQueue queue : serverQueues.values()) {
            for (ServerQueue.QueuedPlayer queuedPlayer : queue.getQueue()) {
                if (queuedPlayer.getUniqueId().equals(player.getUniqueId())) {
                    return queue;
                }
            }
        }

        return null;
    }
}
