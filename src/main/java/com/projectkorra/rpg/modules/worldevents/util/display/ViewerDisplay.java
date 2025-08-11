package com.projectkorra.rpg.modules.worldevents.util.display;

import org.bukkit.entity.Player;

public interface ViewerDisplay {
    void addViewer(Player viewer);
    void removeViewer(Player viewer);
}
