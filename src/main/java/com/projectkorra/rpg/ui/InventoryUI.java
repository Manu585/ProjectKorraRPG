package com.projectkorra.rpg.ui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public interface InventoryUI extends InventoryHolder {
    void open(@NotNull Player player);

    default void handleClick(InventoryClickEvent event) {}

    // Close callback
    default void onClose(@NotNull Player player) {}

    @Override @NotNull Inventory getInventory();
}
