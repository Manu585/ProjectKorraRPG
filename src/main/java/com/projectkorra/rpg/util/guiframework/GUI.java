package com.projectkorra.rpg.util.guiframework;

import com.projectkorra.rpg.util.guiframework.util.Slot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class GUI implements InventoryHolder {
    private final Map<Slot, GuiItem> allItems = new HashMap<>();

    private final Inventory inventory;
    private final int rows;
    private final String title;

    private boolean clicksDisabled = false;

    public GUI(int rows, String title) {
        this.rows = rows;
        this.title = title;
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
    }

    public void setItem(Slot slot, GuiItem item) {
        allItems.put(slot, item);
        inventory.setItem(slot.getIndex(), item.getItem());
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public void disableAllClicks() {
        this.clicksDisabled = true;
    }

    public void enableAllClicks() {
        this.clicksDisabled = false;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public Map<Slot, GuiItem> getAllItems() {
        return allItems;
    }

    public int getRows() {
        return rows;
    }

    public String getTitle() {
        return title;
    }

    public boolean areClicksDisabled() {
        return clicksDisabled;
    }
}
