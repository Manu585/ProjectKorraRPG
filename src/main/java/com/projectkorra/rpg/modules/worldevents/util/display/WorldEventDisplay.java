package com.projectkorra.rpg.modules.worldevents.util.display;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;

/**
 * Handles Lifecycle of messages / displays for {@link WorldEvent}
 */
public interface WorldEventDisplay {
	void startDisplay(WorldEvent event);
	void stopDisplay(WorldEvent event);
}

