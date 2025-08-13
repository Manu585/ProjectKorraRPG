package com.projectkorra.rpg.modules.worldevents.display;

import org.bukkit.entity.Player;

/**
 * Handles viewings of displays from WorldEvents, specifically made for BossBar
 */
public interface ViewerDisplay {
    void addViewer(Player viewer);
    void removeViewer(Player viewer);
}
