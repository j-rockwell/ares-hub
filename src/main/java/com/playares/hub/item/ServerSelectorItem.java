package com.playares.hub.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.playares.commons.item.custom.CustomItem;
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
public final class ServerSelectorItem implements CustomItem {
    @Getter public final Hub hub;

    @Override
    public Material getMaterial() {
        return Material.COMPASS;
    }

    @Override
    public String getName() {
        return ChatColor.AQUA + "Select Server";
    }

    @Override
    public List<String> getLore() {
        final List<String> lore = Lists.newArrayList();

        lore.add(ChatColor.GRAY + "Right-click while holding this item");
        lore.add(ChatColor.GRAY + "to open a menu of all our server options!");

        return lore;
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return Maps.newHashMap();
    }

    @Override
    public Runnable getRightClick(Player who) {
        return () -> hub.getQueueManager().getHandler().openSelectorMenu(who);
    }
}