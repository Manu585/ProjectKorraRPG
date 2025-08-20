package com.projectkorra.rpg.modules.worldevents.gui;

import com.projectkorra.rpg.ui.InventoryUI;
import com.projectkorra.rpg.ui.builder.InventoryUIBuilder;
import com.projectkorra.rpg.ui.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WorldEventAttributionGui implements Menu {
    private static final int ROWS = 3;

    @Override
    public InventoryUI buildUI(Player player) {
        return InventoryUIBuilder.create(ROWS, "Attribution").fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).build();
    }
}
