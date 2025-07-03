package com.projectkorra.rpg.managers;

import com.projectkorra.rpg.util.guiframework.GUI;
import com.projectkorra.rpg.util.guiframework.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Map;

public class GuiManager implements Listener {
    private final Map<Player, GUI> openGuis = new HashMap<>();

    public void openGui(Player player, GUI gui) {
        openGuis.put(player, gui);
        gui.open(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(final InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        GUI gui = openGuis.get(player);

        if (gui == null || event.getInventory().getHolder() != gui) return;

        GuiItem guiItem = gui.getAllItems().get(event.getRawSlot());

        if (gui.areClicksDisabled() && guiItem == null) {
            event.setCancelled(true);
            return;
        }

        if (guiItem != null) {
            guiItem.onClick(event);
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClos(final InventoryCloseEvent event) {
        if (!openGuis.containsKey((Player) event.getPlayer())) return;
        openGuis.remove((Player) event.getPlayer());
    }
}
