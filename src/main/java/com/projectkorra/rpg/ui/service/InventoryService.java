package com.projectkorra.rpg.ui.service;

import com.projectkorra.rpg.ui.InventoryUI;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps track of open UIs and provides open / close methods.
 */
public class InventoryService {
    private final Map<Player, InventoryUI> openUis = new ConcurrentHashMap<>();

    public void open(Player player, InventoryUI ui) {
        InventoryUI old = openUis.put(player, ui);

        // Close any prevously open menus for the player
        if (old != null && old != ui) {
            old.onClose(player);
        }

        ui.open(player);
    }

    public InventoryUI get(Player player) {
        return openUis.get(player);
    }

    public void close(Player player) {
        InventoryUI old = openUis.remove(player);
        if (old != null) {
            old.onClose(player);
        }
    }

    public void closeAll() {
        for (Player player : openUis.keySet()) {
            openUis.get(player).onClose(player);
        }
        openUis.clear();
    }

    public Map<Player, InventoryUI> getOpenUis() {
        return openUis;
    }
}
