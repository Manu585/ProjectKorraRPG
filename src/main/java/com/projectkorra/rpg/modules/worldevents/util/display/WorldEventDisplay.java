package com.projectkorra.rpg.modules.worldevents.util.display;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;

public interface WorldEventDisplay {
	void startDisplay(WorldEvent event);
	void updateDisplay(WorldEvent event, double progress);
	void stopDisplay(WorldEvent event);
}

