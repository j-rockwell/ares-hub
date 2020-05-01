package com.playares.hub.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.playares.commons.item.custom.CustomItem;
import com.playares.commons.services.customitems.CustomItemService;
import com.playares.hub.Hub;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public final class LeaveQueueItem implements CustomItem {
    @Getter public final Hub hub;

    @Override
    public Material getMaterial() {
        return Material.BARRIER;
    }

    @Override
    public String getName() {
        return ChatColor.RED + "Leave Queue";
    }

    @Override
    public List<String> getLore() {
        final List<String> lore = Lists.newArrayList();

        lore.add(ChatColor.GRAY + "Right-click this item while holding it to");
        lore.add(ChatColor.GRAY + "to leave your current queue");

        return lore;
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return Maps.newHashMap();
    }

    @Override
    public Runnable getRightClick(Player who) {
        return () -> {

            final CustomItemService customItemService = (CustomItemService)hub.getService(CustomItemService.class);

            customItemService.getItem(ServerSelectorItem.class).ifPresent(item -> {
                who.getInventory().clear();
                who.getInventory().setItem(4, item.getItem());

                who.sendMessage(ChatColor.RED + "You have left the server queue");
            });
        };
    }
}