package com.projectkorra.rpg.modules.leveling.gui;

import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.ui.InventoryUI;
import com.projectkorra.rpg.ui.builder.InventoryUIBuilder;
import com.projectkorra.rpg.ui.menu.Menu;
import com.projectkorra.rpg.ui.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SkillTreeMenu implements Menu {
    private static final int ROWS = 3;

    @Override
    public InventoryUI buildUI(Player player) {
        return InventoryUIBuilder.create(ROWS, "&6Skill Tree")
                .withButton(4, 1, ItemUtil.create(Material.DIAMOND, "&a&lCLICK ME"),
                        e -> player.sendMessage(ChatUtil.color("&aHello from Skill Tree"))
                )
                .withButton(0, 2, ItemUtil.create(Material.ARROW, "&cBack"),
                        e -> new MainMenu().open(player)
                )
                .build();
    }
}
