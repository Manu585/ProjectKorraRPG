package com.projectkorra.rpg.modules.leveling.gui.master;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.util.guiframework.GUI;
import com.projectkorra.rpg.util.guiframework.GuiItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class MainGui extends GUI {
    private static final int ROWS = 3;
    private final Player player;

    public MainGui(Player player) {
        super(ROWS, ChatUtil.color("Leveling"));
        this.player = player;

        open(player);
        disableAllClicks();

        for (int i = 0; i < getInventory().getSize(); i++) {
            if (i == 5) continue;
            getInventory().setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        this.setupGui();
    }

    private void setupGui() {
        GuiItem shardButton = new GuiItem(amethystShardItem(), event -> {
            Player player = (Player) event.getWhoClicked();
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

            if (bPlayer.getElements().isEmpty()) {
                player.sendMessage("You don't have any elements yet!");
                event.setCancelled(true);
                return;
            }

            if (bPlayer.getElements().size() == 1) {
                Element element = bPlayer.getElements().getFirst();
                event.setCancelled(true);
                new AbilityMenu(element).show(player);
            } else {
                event.setCancelled(true);
                new ElementPicker(player).show(player);
            }
        });

        getInventory().setItem(5, shardButton.getItem());
    }

    private ItemStack amethystShardItem() {
        ItemStack amethystShard = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta amethystShardMeta = amethystShard.getItemMeta();
        assert amethystShardMeta != null;
        amethystShardMeta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Attributes");
        amethystShard.setItemMeta(amethystShardMeta);

        return amethystShard;
    }

    private ItemStack getHead() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        assert skull != null;
        skull.setDisplayName(player.getName());
        skull.setOwningPlayer(player);
        skull.setOwnerProfile(player.getPlayerProfile());
        item.setItemMeta(skull);
        return item;
    }

    private ItemStack netherStarItem() {
        ItemStack netherStar = new ItemStack(Material.NETHER_STAR);
        ItemMeta netherStarMeta = netherStar.getItemMeta();
        assert netherStarMeta != null;
        netherStarMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.ITALIC + "Skilltree");
        netherStar.setItemMeta(netherStarMeta);
        return netherStar;
    }
}
