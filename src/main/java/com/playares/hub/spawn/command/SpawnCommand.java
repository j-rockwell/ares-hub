package com.playares.hub.spawn.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.playares.commons.logger.Logger;
import com.playares.hub.spawn.SpawnManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@CommandAlias("spawn")
@AllArgsConstructor
public final class SpawnCommand extends BaseCommand {
    @Getter public final SpawnManager manager;

    @CommandAlias("spawn")
    public void onSpawn(Player player) {
        player.teleport(manager.getSpawn().getBukkit());
    }

    @Subcommand("set")
    @CommandPermission("areshub.spawn.set")
    @Description("Update the Spawn location of the Hub")
    public void onSet(Player player) {
        manager.getHandler().updateSpawn(player);
        Logger.print(player.getName() + " updated the Hub spawn location to " + manager.getSpawn().toString());
    }
}