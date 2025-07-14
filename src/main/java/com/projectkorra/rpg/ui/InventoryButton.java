package com.projectkorra.rpg.ui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface InventoryButton {
    ItemStack getItem();

    void handleClick(InventoryClickEvent event);
}
