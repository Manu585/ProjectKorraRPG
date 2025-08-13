package com.projectkorra.rpg.modules.worldevents.display;

import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;

/**
 * Handles BossBar smoothness (per tick or second update)
 */
public interface TickingDisplay {
    long tickPeriod();
    void updateTick(WorldEvent event, double progress);
}
