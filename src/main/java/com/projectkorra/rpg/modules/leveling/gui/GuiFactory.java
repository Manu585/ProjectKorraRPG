package com.projectkorra.rpg.modules.leveling.gui;

import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.modules.leveling.rpgplayer.RpgPlayer;
import com.projectkorra.rpg.ui.InventoryUI;
import com.projectkorra.rpg.ui.builder.InventoryUIBuilder;
import com.projectkorra.rpg.ui.service.InventoryService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuiFactory {
    private final InventoryService service;

    public GuiFactory(InventoryService service) {
        this.service = service;
    }

    public void openMainMenu(Player player) {
        service.open(player, createMainMenu(player));
    }

    public InventoryUI createMainMenu(Player player) {
        InventoryUIBuilder mainMenu = InventoryUIBuilder.create(3, ChatUtil.color("&1Main Menu"));

        ItemStack filler = makeItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        ItemStack vines = makeItem(Material.TWISTING_VINES, " ");
        for (int y = 0; y < mainMenu.getHeight(); y++) {
            for (int x = 0; x < mainMenu.getWidth(); x++) {
                if (x == 0 || x == 8) {
                    mainMenu.withItem(x, y, vines);
                    continue;
                }
                mainMenu.withItem(x, y, filler);
            }
        }

        RpgPlayer rpgPlayer = new RpgPlayer(player.getUniqueId(), 5, 0); // Test instance

        InventoryUI skillTreeMenu = InventoryUIBuilder
                .create(3, ChatUtil.color("&6Skilltree"))
                .withButton(4, 1, makeItem(Material.DIAMOND, ChatUtil.color("&a&lCLICK ME")), this::onSubMenu1Click)
                .withButton(0, 2, makeItem(Material.ARROW, ChatUtil.color("&cBack")), click -> openMainMenu((Player) click.getWhoClicked()))
                .build();

        // NOT USING LAMBDA TO SHOW OTHER JR. DEVS THE PROPER MULTI-LINE WAY
        mainMenu.withButton(3, 1, makeItem(Material.DIAMOND_BLOCK, ChatUtil.color("&1SkillTree")), click -> {
            Player clicker = (Player) click.getWhoClicked();
            service.open(clicker, skillTreeMenu);
        }).withItem(5, 1, makeItem(Material.NETHER_STAR, ChatUtil.color("&bStats"), List.of("Level: " + rpgPlayer.getLevel() + "\n" + "XP: " + rpgPlayer.getXp())));

        return mainMenu.build();
    }

    private void onSubMenu1Click(@NotNull InventoryClickEvent click) {
        Player p = (Player) click.getWhoClicked();
        p.sendMessage(ChatUtil.color("&aHello from Sub Menu 1"));
    }

    private static @NotNull ItemStack makeItem(Material material, String name) {
        return makeItem(material, name, List.of());
    }

    private static @NotNull ItemStack makeItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new IllegalStateException("Cannot get ItemMeta for " + material);

        meta.setDisplayName(name);
        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }
}
