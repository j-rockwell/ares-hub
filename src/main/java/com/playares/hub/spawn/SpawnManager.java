package com.playares.hub.spawn;

import com.playares.commons.location.PLocatable;
import com.playares.commons.util.general.Configs;
import com.playares.hub.Hub;
import com.playares.hub.spawn.command.SpawnCommand;
import com.playares.hub.spawn.listener.SpawnListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;

public final class SpawnManager {
    @Getter public final Hub plugin;
    @Getter public final SpawnHandler handler;
    @Getter @Setter public PLocatable spawn;

    public SpawnManager(Hub plugin) {
        this.plugin = plugin;
        this.handler = new SpawnHandler(this);

        plugin.registerListener(new SpawnListener(this));
        plugin.registerCommand(new SpawnCommand(this));

        final YamlConfiguration config = Configs.getConfig(plugin, "config");
        final double x = config.getDouble("spawn.x");
        final double y = config.getDouble("spawn.y");
        final double z = config.getDouble("spawn.z");
        final float yaw = (float)config.getDouble("spawn.yaw");
        final float pitch = (float)config.getDouble("spawn.pitch");
        final String world = config.getString("spawn.world");

        this.spawn = new PLocatable(world, x, y, z, yaw, pitch);
    }
}