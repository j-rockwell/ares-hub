package com.playares.hub.queue.menu;

import com.google.common.collect.Lists;
import com.playares.commons.item.ItemBuilder;
import com.playares.commons.logger.Logger;
import com.playares.commons.menu.ClickableItem;
import com.playares.commons.menu.Menu;
import com.playares.commons.services.customitems.CustomItemService;
import com.playares.commons.services.serversync.ServerSyncService;
import com.playares.commons.services.serversync.data.SyncedServer;
import com.playares.commons.util.bukkit.Scheduler;
import com.playares.hub.Hub;
import com.playares.hub.item.LeaveQueueItem;
import com.playares.hub.queue.data.ServerQueue;
import com.playares.luxe.LuxeService;
import com.playares.luxe.rank.data.Rank;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public final class SelectorMenu extends Menu {
    @Getter public final Hub hub;
    @Getter public BukkitTask updater;

    public SelectorMenu(Hub hub, Player player) {
        super(hub, player, "Select Server", 1);
        this.hub = hub;
    }

    @Override
    public void open() {
        super.open();
        this.updater = new Scheduler(hub).sync(this::update).repeat(0L, 20L).run();
    }

    private void update() {
        final ServerSyncService syncService = (ServerSyncService)hub.getService(ServerSyncService.class);
        final CustomItemService customItemService = (CustomItemService)hub.getService(CustomItemService.class);

        if (syncService == null) {
            Logger.error("Failed to update menu for " + player.getName() + " because the Server Sync Service is not initialized");
            return;
        }

        clear();

        final SyncedServer civ = syncService.getServers().stream().filter(server -> server.getType().equals(SyncedServer.ServerType.CIV)).findFirst().orElse(null);

        if (civ != null) {
            ServerQueue queue = hub.getQueueManager().getServerQueues().get(civ.getServerId());

            if (queue == null) {
                queue = new ServerQueue(civ);
                hub.getQueueManager().getServerQueues().put(civ.getServerId(), queue);
            }

            final ItemBuilder builder = new ItemBuilder()
                    .setMaterial(Material.DIAMOND_HELMET)
                    .setName(civ.getDisplayName());

            final List<String> lore = Lists.newArrayList();

            for (String description : civ.getDescription()) {
                lore.add(ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', description));
            }

            lore.add(ChatColor.RESET + " ");
            lore.add(ChatColor.GOLD + "Status" + ChatColor.YELLOW + ": " + civ.getStatus().getDisplayName());
            lore.add(ChatColor.GOLD + "Online" + ChatColor.YELLOW + ": " + ChatColor.GRAY + civ.getPlayerList().size());
            lore.add(ChatColor.GOLD + "Queue" + ChatColor.YELLOW + ": " + ChatColor.GRAY + queue.getQueue().size());

            lore.add(ChatColor.RESET + " ");
            lore.add(ChatColor.GREEN + "Click to join!");

            builder.addLore(lore);

            final ServerQueue finalizedQueue = queue;

            addItem(new ClickableItem(builder.build(), 0, click -> {
                final LuxeService luxe = (LuxeService)hub.getService(LuxeService.class);

                Rank rank = null;

                if (luxe != null) {
                    rank = luxe.getRankManager().getHighestRank(player);
                }

                final ServerQueue currentQueue = hub.getQueueManager().getCurrentQueue(player);

                if (currentQueue != null) {
                    currentQueue.remove(player.getUniqueId());
                }

                finalizedQueue.add(player.getUniqueId(), rank);

                player.sendMessage(ChatColor.RESET + "Adding you to the " + civ.getDisplayName() + ChatColor.RESET + " queue...");
                player.sendMessage(ChatColor.AQUA + "You are now " + ChatColor.YELLOW + "#" + finalizedQueue.getPosition(player.getUniqueId()) + ChatColor.AQUA + " in queue to join " + civ.getDisplayName());
                player.closeInventory();

                customItemService.getItem(LeaveQueueItem.class).ifPresent(item -> player.getInventory().setItem(4, item.getItem()));
            }));
        }
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        super.onInventoryClose(event);

        if (this.updater != null) {
            this.updater.cancel();
            this.updater = null;
        }
    }
}
