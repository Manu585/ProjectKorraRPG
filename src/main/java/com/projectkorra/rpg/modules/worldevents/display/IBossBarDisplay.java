package com.projectkorra.rpg.modules.worldevents.display;

import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import org.bukkit.entity.Player;

public interface IBossBarDisplay {
    long tickPeriod();
    void updateTick(WorldEvent event, double progress);
    void addViewer(Player viewer);
    void removeViewer(Player viewer);
}
