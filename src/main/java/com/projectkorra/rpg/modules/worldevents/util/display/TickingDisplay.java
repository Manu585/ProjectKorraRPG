package com.projectkorra.rpg.modules.worldevents.util.display;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;

/**
 * Handles BossBar smoothness (per tick or second update)
 */
public interface TickingDisplay {
    long tickPeriod();
    void updateTick(WorldEvent event, double progress);
}
