package com.projectkorra.rpg.util.guiframework;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class GuiItem {
    private final ItemStack item;
    private final Consumer<InventoryClickEvent> action;

    public GuiItem(ItemStack item, Consumer<InventoryClickEvent> action) {
        this.item = item;
        this.action = action;
    }

    public ItemStack getItem() {
        return item;
    }

    public void onClick(InventoryClickEvent event) {
        if (action != null) action.accept(event);
    }
}
