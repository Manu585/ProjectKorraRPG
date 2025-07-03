package com.projectkorra.rpg.util.guiframework;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public abstract class GUI implements InventoryHolder {
    private final Map<Integer, GuiItem> allItems = new HashMap<>();

    private final Inventory inventory;
    private final int rows;
    private final String title;

    private boolean clicksDisabled = false;

    public GUI(int rows, String title) {
        this.rows = rows;
        this.title = title;
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
    }

    public void setItem(int slot, GuiItem item) {
        allItems.put(slot, item);
        inventory.setItem(slot, item.getItem());
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
    public Inventory getInventory() {
        return inventory;
    }

    public Map<Integer, GuiItem> getAllItems() {
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
