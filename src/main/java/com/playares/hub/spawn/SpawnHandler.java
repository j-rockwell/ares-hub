package com.playares.hub.spawn;

import com.playares.commons.location.PLocatable;
import com.playares.commons.util.general.Configs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class SpawnHandler {
    @Getter public final SpawnManager manager;

    /**
     * Handles updating the Spawn location to players current location
     * @param player Player
     */
    public void updateSpawn(Player player) {
        final YamlConfiguration config = Configs.getConfig(manager.getPlugin(), "config");

        config.set("spawn.x", player.getLocation().getX());
        config.set("spawn.y", player.getLocation().getY());
        config.set("spawn.z", player.getLocation().getZ());
        config.set("spawn.yaw", player.getLocation().getYaw());
        config.set("spawn.pitch", player.getLocation().getPitch());
        config.set("spawn.world", player.getLocation().getWorld().getName());

        Configs.saveConfig(manager.getPlugin(), "config", config);

        manager.setSpawn(new PLocatable(player));

        player.sendMessage(ChatColor.GREEN + "Spawn has been updated");
    }
}
