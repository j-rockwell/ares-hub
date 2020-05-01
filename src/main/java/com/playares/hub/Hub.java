package com.playares.hub;

import co.aikar.commands.PaperCommandManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.playares.bridge.BridgeService;
import com.playares.commons.AresPlugin;
import com.playares.commons.connect.mongodb.MongoDB;
import com.playares.commons.services.account.AccountService;
import com.playares.commons.services.alts.AltWatcherService;
import com.playares.commons.services.customitems.CustomItemService;
import com.playares.commons.services.event.CustomEventService;
import com.playares.commons.services.serversync.ServerSyncService;
import com.playares.commons.util.general.Configs;
import com.playares.essentials.EssentialsService;
import com.playares.hub.listener.HubListener;
import com.playares.hub.queue.QueueManager;
import com.playares.hub.spawn.SpawnManager;
import com.playares.luxe.LuxeService;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

public final class Hub extends AresPlugin {
    @Getter public QueueManager queueManager;
    @Getter public SpawnManager spawnManager;

    @Override
    public void onEnable() {
        final YamlConfiguration config = Configs.getConfig(this, "config");
        final String databaseUri = config.getString("database_settings.uri");
        final String databaseName = config.getString("database_settings.name");

        final MongoDB database = new MongoDB(databaseUri);
        registerDatabase(database);
        database.openConnection();

        registerCommandManager(new PaperCommandManager(this));
        registerProtocolLibrary(ProtocolLibrary.getProtocolManager());

        registerListener(new HubListener());

        // Services
        registerService(new CustomEventService(this));
        registerService(new AccountService(this, databaseName));
        registerService(new CustomItemService(this));
        registerService(new EssentialsService(this, databaseName));
        registerService(new BridgeService(this));
        registerService(new AltWatcherService(this, databaseName));
        registerService(new ServerSyncService(this, databaseName));
        registerService(new LuxeService(this, databaseName));
        startServices();

        queueManager = new QueueManager(this);
        spawnManager = new SpawnManager(this);

        if (!getServer().getMessenger().getOutgoingChannels().contains("BungeeCord")) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
