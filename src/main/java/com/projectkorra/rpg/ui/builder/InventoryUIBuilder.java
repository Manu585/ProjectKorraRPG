package com.projectkorra.rpg.ui.builder;

import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.ui.InventoryUI;
import com.projectkorra.rpg.ui.Slot;
import com.projectkorra.rpg.ui.impl.BasicInventoryUI;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class InventoryUIBuilder {
    private static final int COLUMNS = Slot.COLUMNS;
    private final Map<Slot, ItemStack> items = new HashMap<>();
    private final Map<Slot, Consumer<InventoryClickEvent>> clickHandlers = new HashMap<>();

    private final int rows;
    private final String title;

    private InventoryUIBuilder(int rows, String title) {
        this.rows = rows;
        this.title = title;
    }

    public static InventoryUIBuilder create(int rows, String title) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("rows must be between 1 and 6!");
        }
        return new InventoryUIBuilder(rows, ChatUtil.color(title));
    }

    public InventoryUIBuilder withItem(int x, int y, ItemStack item) {
        validateXY(x, y);
        items.put(Slot.of(x, y), item);
        return this;
    }

    public InventoryUIBuilder withButton(int x, int y, ItemStack item, Consumer<InventoryClickEvent> onClick) {
        withItem(x, y, item);
        clickHandlers.put(Slot.of(x, y), onClick);
        return this;
    }

    private void validateXY(int x, int y) {
        if (x < 0 || x > 8 || y < 0 || y >= rows) {
            throw new IllegalArgumentException("Slot out of bounds! x=" + x + " (0-8), y=" + y + " (0-" + (rows-1) + ")");
        }
    }

    public InventoryUI build() {
        return new BasicInventoryUI(rows, title, items, clickHandlers);
    }

    public int getWidth() {
        return COLUMNS;
    }

    public int getHeight() {
        return rows;
    }
}
