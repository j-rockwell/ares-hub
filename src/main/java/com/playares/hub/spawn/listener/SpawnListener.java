package com.playares.hub.spawn.listener;

import com.playares.hub.spawn.SpawnManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;

@AllArgsConstructor
public final class SpawnListener implements Listener {
    @Getter public final SpawnManager manager;

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(true);

        if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
            event.getEntity().teleport(manager.getSpawn().getBukkit());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        player.setWalkSpeed(0.4F);
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(manager.getSpawn().getBukkit());
    }
}