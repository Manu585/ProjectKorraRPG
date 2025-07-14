package com.projectkorra.rpg.modules.leveling.gui;

import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.ui.InventoryUI;
import com.projectkorra.rpg.ui.builder.InventoryUIBuilder;
import com.projectkorra.rpg.ui.service.InventoryService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiFactory {
    private final InventoryService service;

    public GuiFactory(InventoryService service) {
        this.service = service;
    }

    public void openMainMenu(Player player) {
        InventoryUI menu = createMainMenu();
        service.open(player, menu);
    }

    public InventoryUI createMainMenu() {
        InventoryUI subMenu1 = InventoryUIBuilder
                .create(3, ChatUtil.color("&aSub menu 1"))
                .withButton(4, 1, makeItem(Material.DIAMOND, ChatUtil.color("&a&lCLICK ME")), this::onSubMenu1Click)
                .withButton(0, 2, makeItem(Material.ARROW, ChatUtil.color("&cBack")), click -> openMainMenu((Player) click.getWhoClicked()))
                .build();

        InventoryUI subMenu2 = InventoryUIBuilder
                .create(3, ChatUtil.color("&eSub menu 2"))
                .withButton(4, 1, makeItem(Material.EMERALD, ChatUtil.color("&e&lCLICK ME")), this::onSubMenu2Click)
                .withButton(0, 2, makeItem(Material.ARROW, ChatUtil.color("&cBack")), click -> openMainMenu((Player) click.getWhoClicked()))
                .build();

        InventoryUIBuilder mainMenu = InventoryUIBuilder.create(3, ChatUtil.color("&1Main Menu"));

        ItemStack filler = makeItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int y = 0; y < mainMenu.getHeight(); y++) {
            for (int x = 0; x < mainMenu.getWidth(); x++) {
                mainMenu.withItem(x, y, filler);
            }
        }

        // NOT USING LAMBDA TO SHOW JR. DEVS THE PROPER MULTI LINE WAY
        mainMenu.withButton(3, 1, makeItem(Material.DIAMOND_BLOCK, ChatUtil.color("&aOpen sub menu 1")), click -> {
            Player player = (Player) click.getWhoClicked();
            service.open(player, subMenu1);
        }).withButton(5, 1, makeItem(Material.EMERALD_BLOCK, ChatUtil.color("&eOpen sub menu 2")), click -> {
            Player player = (Player) click.getWhoClicked();
            service.open(player, subMenu2);
        });

        return mainMenu.build();
    }

    private void onSubMenu1Click(InventoryClickEvent click) {
        Player p = (Player) click.getWhoClicked();
        p.sendMessage(ChatUtil.color("&aHello from Sub Menu 1"));
    }

    private void onSubMenu2Click(InventoryClickEvent click) {
        Player p = (Player) click.getWhoClicked();
        p.sendMessage(ChatUtil.color("&eHello from Sub Menu 2"));
    }

    private static ItemStack makeItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            throw new IllegalStateException("Cannot get ItemMeta for " + material);
        }
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
