package com.projectkorra.rpg.modules.leveling.gui;

import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.plugin.Plugin;

public class GuiManager {

    private final Plugin plugin;
    private final List<Gui> allGuis = new ArrayList<>();

    public GuiManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        // TODO: Initialize GUI components
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public List<Gui> getAllGuis() {
        return allGuis;
    }
}
