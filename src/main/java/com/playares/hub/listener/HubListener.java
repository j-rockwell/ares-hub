package com.playares.hub.listener;

import com.playares.commons.event.PlayerBigMoveEvent;
import com.playares.commons.event.PlayerDamagePlayerEvent;
import com.playares.commons.util.bukkit.Players;
import com.playares.essentials.EssentialsService;
import com.playares.hub.Hub;
import com.playares.luxe.LuxeService;
import com.playares.luxe.rank.data.Rank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

@AllArgsConstructor
public final class HubListener implements Listener {
    @Getter public final Hub plugin;

    private void checkPermissions(Player player, Cancellable event) {
        if (player.hasPermission("areshub.edit")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final EssentialsService essentialsService = (EssentialsService)plugin.getService(EssentialsService.class);
        final LuxeService luxeService = (LuxeService)plugin.getService(LuxeService.class);

        if (luxeService == null || essentialsService == null) {
            event.setJoinMessage(null);
            return;
        }

        final Player player = event.getPlayer();
        final Rank rank = luxeService.getRankManager().getHighestRank(player);

        if (rank == null) {
            event.setJoinMessage(null);
            return;
        }

        if (!essentialsService.getVanishManager().isVanished(player)) {
            event.setJoinMessage(luxeService.getRankManager().formatName(player) + ChatColor.YELLOW + " joined the lobby");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onPlayerMove(PlayerBigMoveEvent event) {
        final Player player = event.getPlayer();
        final Block floorBlock = event.getTo().getBlock();

        if (floorBlock == null || !floorBlock.getType().name().contains("_PLATE")) {
            return;
        }

        final Vector velocity = player.getLocation().getDirection();

        if (floorBlock.getType().equals(Material.WOOD_PLATE)) {
            velocity.setY(velocity.getY() + 0.3);
            velocity.multiply(3.00);
            Players.playSound(player, Sound.ENDERDRAGON_WINGS);
        }

        if (floorBlock.getType().equals(Material.STONE_PLATE)) {
            velocity.setY(velocity.getY() + 0.3);
            velocity.multiply(4.00);
            Players.playSound(player, Sound.ENDERDRAGON_WINGS);
        }

        if (floorBlock.getType().equals(Material.IRON_PLATE)) {
            velocity.setY(velocity.getY() + 0.2);
            velocity.multiply(5.00);
            Players.playSound(player, Sound.ENDERDRAGON_WINGS);
        }

        if (floorBlock.getType().equals(Material.GOLD_PLATE)) {
            velocity.setY(velocity.getY() + 0.15);
            velocity.multiply(6.00);
            Players.playSound(player, Sound.ENDERDRAGON_WINGS);
        }

        player.setVelocity(velocity);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(PlayerDamagePlayerEvent event) {
        final Player damager = event.getDamager();
        final Player damaged = event.getDamaged();

        event.setCancelled(true);

        if (damaged.hasPermission("areshub.staff")) {
            return;
        }

        Players.spawnEffect(damager, damaged.getLocation().add(0, 1.0, 0), Effect.EXPLOSION_LARGE, 5, 1);
        Players.playSound(damager, Sound.ITEM_PICKUP);

        damager.hidePlayer(damaged);
        damager.sendMessage(ChatColor.AQUA + "Pop!");
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent event) {
        event.setFoodLevel(10);
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        checkPermissions(event.getPlayer(), event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        checkPermissions(event.getPlayer(), event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        checkPermissions(event.getPlayer(), event);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        checkPermissions(player, event);
    }
}