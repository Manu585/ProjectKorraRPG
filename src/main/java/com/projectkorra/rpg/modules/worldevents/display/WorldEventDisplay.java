package com.projectkorra.rpg.modules.worldevents.display;

import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;

/**
 * Handles Lifecycle of messages / displays for {@link WorldEvent}
 */
public interface WorldEventDisplay {
	void startDisplay(WorldEvent event);
	void stopDisplay(WorldEvent event);
}

