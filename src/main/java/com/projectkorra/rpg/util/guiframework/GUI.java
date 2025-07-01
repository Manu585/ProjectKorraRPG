package com.projectkorra.rpg.util.guiframework;

import com.projectkorra.rpg.ProjectKorraRPG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public abstract class GUI implements InventoryHolder, Listener {
    private final Inventory inventory;
    private final int rows;
    private final String title;

    private final Map<Integer, GuiItem> allItems = new HashMap<>();

    public GUI(int rows, String title) {
        this.rows = rows;
        this.title = title;
        this.inventory = Bukkit.createInventory(this, rows * 9, title);

        Bukkit.getPluginManager().registerEvents(this, ProjectKorraRPG.getPlugin());
    }

    public void setItem(int slot, GuiItem item) {
        allItems.put(slot, item);
        inventory.setItem(slot, item.getItem());
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);
        GuiItem guiItem = allItems.get(event.getRawSlot());
        if (guiItem != null) guiItem.onClick(event);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public int getRows() {
        return rows;
    }

    public String getTitle() {
        return title;
    }

    public Map<Integer, GuiItem> getAllItems() {
        return allItems;
    }
}
