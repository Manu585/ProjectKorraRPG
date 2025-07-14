package com.projectkorra.rpg.modules.leveling.gui;

import com.projectkorra.rpg.ui.InventoryUI;
import com.projectkorra.rpg.ui.builder.InventoryUIBuilder;
import com.projectkorra.rpg.ui.menu.Menu;
import com.projectkorra.rpg.ui.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MainMenu implements Menu {
    private static final int ROWS = 3;

    @Override
    public InventoryUI buildUI(Player player) {
        Menu skillTree = new SkillTreeMenu();

        InventoryUIBuilder mainMenuUI = InventoryUIBuilder.create(ROWS, "&1Main Menu");

        ItemStack filler = ItemUtil.create(Material.GRAY_STAINED_GLASS_PANE, " ");
        ItemStack vines = ItemUtil.create(Material.TWISTING_VINES, " ");
        for (int y = 0; y < mainMenuUI.getHeight(); y++) {
            for (int x = 0; x < mainMenuUI.getWidth(); x++) {
                if (x == 0 || x == 8) {
                    mainMenuUI.withItem(x, y, vines);
                    continue;
                }
                mainMenuUI.withItem(x, y, filler);
            }
        }

        mainMenuUI.withButton(3, 1, ItemUtil.create(Material.NETHER_STAR, "&1SkillTree"), click -> skillTree.open(player));

        return mainMenuUI.build();
    }
}
