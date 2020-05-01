package com.playares.hub.queue.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.playares.commons.services.serversync.data.SyncedServer;
import com.playares.commons.util.general.Time;
import com.playares.luxe.rank.data.Rank;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class ServerQueue {
    @Getter public final SyncedServer server;
    @Getter public final List<QueuedPlayer> queue;

    public ServerQueue(SyncedServer server) {
        this.server = server;
        this.queue = Collections.synchronizedList(Lists.newArrayList());
    }

    /**
     * Adds the provided Bukkit UUID to this queue
     * @param uniqueId Bukkit UUID
     * @param rank Luxe Rank
     */
    public void add(UUID uniqueId, Rank rank) {
        final int weight = (rank != null) ? rank.getWeight() : 0;
        queue.add(new QueuedPlayer(uniqueId, weight));
    }

    /**
     * Removes the provided Bukkit UUID from this queue
     * @param uniqueId Bukkit UUID
     */
    public void remove(UUID uniqueId) {
        queue.removeIf(player -> player.getUniqueId().equals(uniqueId));
    }

    /**
     * Returns the position in queue for this queue
     * @param uniqueId Bukkit UUID
     * @return Queue Position
     */
    public int getPosition(UUID uniqueId) {
        int pos = 1;

        for (QueuedPlayer player : getSortedQueue()) {
            if (player.getUniqueId().equals(uniqueId)) {
                break;
            }

            pos += 1;
        }

        return pos;
    }

    /**
     * Returns true if provided UUID is in queue to join this server
     * @param uniqueId Bukkit UUID
     * @return True if in queue
     */
    public boolean isQueueing(UUID uniqueId) {
        return queue.stream().anyMatch(player -> player.getUniqueId().equals(uniqueId));
    }

    /**
     * Returns an Immutable List of Queue Players sorted by the queue processing order
     * @return Immutable List of Queue Players
     */
    public ImmutableList<QueuedPlayer> getSortedQueue() {
        final List<QueuedPlayer> players = Lists.newArrayList(queue);

        players.sort((o1, o2) -> {
            if (o1.getJoinTime() == o2.getJoinTime()) {
                return 0;
            }

            if (o1.getJoinTime() >= o2.getJoinTime()) {
                return 1;
            }

            return -1;
        });

        return ImmutableList.copyOf(players);
    }

    public final class QueuedPlayer {
        @Getter public final UUID uniqueId;
        @Getter public long joinTime;
        @Getter @Setter public int weight;

        public QueuedPlayer(UUID uniqueId, int weight) {
            this.uniqueId = uniqueId;
            this.joinTime = Time.now();
            this.weight = weight;
        }
    }
}
